package parser.instrumentation

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.MethodDeclarationVisitor

import scala.jdk.CollectionConverters._

class MethodDeclarationInstrum(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)
  private[this] var attributes: List[Attribute] = List()

  def startInstrum(): ASTRewrite ={
    methodDeclarationInstrum()
    val lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY)
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText("import astparser.*;")
    lrw.insertLast(textToAdd, null)
    rewriter
  }

  def methodDeclarationInstrum(): Unit ={
    val methodDeclarationVisitor = new MethodDeclarationVisitor
    cu.accept(methodDeclarationVisitor)
    val methodDeclarations = methodDeclarationVisitor.getMethodDeclarations
    //    expressionStatemtemts.filter(_.getExpression.isInstanceOf[Assignment]).map(assignmentInstrumHelper(_))
    methodDeclarations.map(methodDeclarationInstrumHelper(_))
  }

  def methodDeclarationInstrumHelper(methodDeclaration: MethodDeclaration) = {
    //    def getParent(parent: ASTNode): ASTNode = {
    //      if (parent.isInstanceOf[Block])
    //        parent
    //      else
    //        getParent(parent.getParent)
    //    }
    //
    //    val parent = getParent(expressionStatement.getParent)


    //    println(parameters)
    //
    //    var qualifiedName = new String
    //    qualifiedName = methodDeclaration.getName.getFullyQualifiedName //+ "."
    //
    //    val (binding, methodSignature) = Binding.getBindingLabel(methodDeclaration.resolveBinding())
    //    val newBinding = if(qualifiedName.length > 0) qualifiedName else binding
    //    var finalBinding = ""
    //    var finalMethodSignature = ""
    //    if(methodSignature.contains(newBinding) && methodSignature.indexOf(newBinding) ==  methodSignature.lastIndexOf(newBinding)){
    //      finalBinding = methodSignature
    //      finalMethodSignature = ""
    //    }
    //    else{
    //      finalBinding =  if(methodSignature.length > 0) newBinding+"."+methodSignature else newBinding
    //      finalMethodSignature = ""
    //    }
    val parameters: List[SingleVariableDeclaration] = methodDeclaration.parameters().asScala.toList.asInstanceOf[List[SingleVariableDeclaration]]
    var attributes : List[Attribute] = List()
    parameters.map(x=>{
      attributes = attributes ++ ExpressionUtils.recurseExpression(x.getName)
    })
    var log = new String
    if(attributes.length > 0) {
      log = "TemplateClass.instrum(" + cu.getLineNumber(methodDeclaration.getStartPosition)
      log += ", " + utils.wrapStringInQuotes("MethodDeclaration")
      attributes.map(attribute => {
        log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"
      }).mkString(", ")
      log += ");"
    }
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText(log)
    val methodBody = methodDeclaration.getBody
    if(methodBody != null) {
      val lrw = rewriter.getListRewrite(methodBody, Block.STATEMENTS_PROPERTY)
      lrw.insertFirst(textToAdd, null)
    }

    // Check if the current method is main(String[] args]
    if(methodDeclaration.getName.getIdentifier.equals("main")){
      if(methodDeclaration.getReturnType2 != null
        && methodDeclaration.getReturnType2.isPrimitiveType
        && methodDeclaration.getReturnType2.asInstanceOf[PrimitiveType].getPrimitiveTypeCode == PrimitiveType.VOID){
        val modifiers: List[Modifier] = methodDeclaration.modifiers.asScala.toList.asInstanceOf[List[Modifier]]
        val newModifiers = modifiers.filter(x => x.isPublic || x.isStatic)
        if(modifiers.length == 2 && newModifiers.length == modifiers.length){
          if(parameters.length == 1){
            val param : SingleVariableDeclaration = parameters.head
            if((param.getType.isArrayType && param.getType.asInstanceOf[ArrayType].getDimensions == 1 && param.getType.asInstanceOf[ArrayType].getElementType.isSimpleType &&
              param.getType.asInstanceOf[ArrayType].getElementType.asInstanceOf[SimpleType].getName.toString.equals("String")) ||
              (param.isVarargs && param.getType.isSimpleType && param.getType.asInstanceOf[SimpleType].getName.toString.equals("String")) ){
                // We hve found the main
                val textToAdd = cu.getAST.newTextElement
                textToAdd.setText("TemplateClass.writeToFile();")
                val lrw = rewriter.getListRewrite(methodBody, Block.STATEMENTS_PROPERTY)
                lrw.insertLast(textToAdd, null)
            }
          }

        }
      }
    }

  }


}
