package parser.instrumentation


import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors._

class ControlInstrum(val cu:CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)
  private[this] var attributes: List[Attribute] = List()
  private[this] var statements : List[Statement] = List()

  def startInstrum(): ASTRewrite ={
    controlInstrum()
    rewriter
  }

  def controlInstrum() = {

    val forStatementVisitor = new ForStatementVisitor
    val enhancedForStatementVisitor = new EnhancedForVisitor
    val doStatementVisitor = new DoStatementVisitor
    val whileStatementVisitor = new WhileStatementVisitor
    val ifStatementVisitor = new IfStatementVisitor
    val switchStatementVisitor = new SwitchStatementVisitor

    cu.accept(forStatementVisitor)
    cu.accept(enhancedForStatementVisitor)
    cu.accept(doStatementVisitor)
    cu.accept(whileStatementVisitor)
    cu.accept(ifStatementVisitor)
    cu.accept(switchStatementVisitor)

    forStatementVisitor.getForStatements.map(createStatements(_))
    enhancedForStatementVisitor.getForStatements.map(createStatements(_))
    doStatementVisitor.getDoStatements.map(createStatements(_))
    whileStatementVisitor.getWhileStatements.map(createStatements(_))
    ifStatementVisitor.getIfStatements.map(createStatements(_))
    switchStatementVisitor.getSwitchStatements.map(createStatements(_))

    statements.map(controlInstrumHelper(_))
  }

  def createStatements(statement : Statement): Unit = {
    statements = statements :+ statement
  }

  def controlInstrumHelper(statement: Statement): Unit =
  {
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
      else
        getParent(parent.getParent)
    }
    val parent = getParent(statement.getParent)
    val (expression : Option[Expression], name : Option[String])  = statement.getNodeType match {
      case ASTNode.SWITCH_STATEMENT => (Some(statement.asInstanceOf[SwitchStatement].getExpression), Some("SwitchStatement"))
      case ASTNode.IF_STATEMENT => (Some(statement.asInstanceOf[IfStatement].getExpression),Some("IfStatement"))
      case ASTNode.FOR_STATEMENT => (Some(statement.asInstanceOf[ForStatement].getExpression),Some("ForStatement"))
      case ASTNode.ENHANCED_FOR_STATEMENT => (Some(statement.asInstanceOf[EnhancedForStatement].getExpression),Some("ForStatement"))
      case ASTNode.WHILE_STATEMENT => (Some(statement.asInstanceOf[WhileStatement].getExpression),Some("WhileStatement"))
      case ASTNode.DO_STATEMENT => (Some(statement.asInstanceOf[DoStatement].getExpression),Some("DoStatement"))
      case _ => (None,None)
    }

    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expression.get)
    var log = ""
    if(attributes.length > 0) {
      log = "TemplateClass.instrum(" + cu.getLineNumber(statement.getStartPosition)
      log += ", " + utils.wrapStringInQuotes(name.getOrElse("Unknown"))
      attributes.map(attribute => {parent
        log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"

      }).mkString(", ")
      log += ");"
    }
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText(log)
    val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
    statement.getNodeType match {
      case ASTNode.FOR_STATEMENT => {
        val lrw = rewriter.getListRewrite(statement.asInstanceOf[ForStatement].getBody, Block.STATEMENTS_PROPERTY)
        lrw.insertFirst(textToAdd,null)
      }
      case ASTNode.ENHANCED_FOR_STATEMENT => {
        val lrw = rewriter.getListRewrite(statement.asInstanceOf[EnhancedForStatement].getBody, Block.STATEMENTS_PROPERTY)
        lrw.insertFirst(textToAdd,null)      }
      case _ => {
        val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
        lrw.insertBefore(textToAdd, statement, null)
      }
    }
    }
}

