package parser.instrumentation

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.VDStatementVisitor

import scala.jdk.CollectionConverters._

class VDSInstrum(val cu: CompilationUnit, rewriter: ASTRewrite) {

  def startInstrum(): ASTRewrite ={
    varDSInstrum()
    rewriter
  }

  def varDSInstrum() = {
    val vdStatementVisitor = new VDStatementVisitor
    cu.accept(vdStatementVisitor)
    val vdStatemtemts = vdStatementVisitor.getExpressionStatements
    vdStatemtemts.map(varDFragmentInstrumHelper(_))
  }

  def varDFragmentInstrumHelper(statement: VariableDeclarationStatement): Unit ={
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
      else
        getParent(parent.getParent)
    }
    val parent = getParent(statement.getParent)
    val vdFragments:List[VariableDeclarationFragment] = statement.fragments().asScala.toList.filter(x=>x.isInstanceOf[VariableDeclaration]).map(x=>x.asInstanceOf[VariableDeclarationFragment])

    vdFragments.map(x=> {
      val attributes1: List[Attribute] = ExpressionUtils.recurseExpression(x.getName)
      val attributes2: List[Attribute] = ExpressionUtils.recurseExpression(x.getInitializer)
      val attributes = attributes1 ::: attributes2
      val hasInitializer = attributes2.length > 0
      var log = ""
      if(attributes.length > 0) {
        log = "TemplateClass.instrum(" + cu.getLineNumber(statement.getStartPosition)
        log += ", " + utils.wrapStringInQuotes("VDS")
        attributes.map(attribute => {
          if(hasInitializer)
            log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"
          else
            log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + utils.wrapStringInQuotes(attribute.variable) + ")"
        }).mkString(", ")
        log += ");"
      }
      val siso = cu.getAST.newTextElement
      siso.setText(log)
      val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
      lrw.insertAfter(siso, statement, null)
    })
  }
}
