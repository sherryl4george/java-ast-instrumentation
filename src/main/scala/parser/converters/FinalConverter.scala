package parser.converters

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.MethodDeclarationVisitor

import scala.jdk.CollectionConverters._

class FinalConverter(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  def startInstrum(): ASTRewrite ={
    methodDeclarationInstrum()
    val lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY)
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText("import test.*;")
    lrw.insertLast(textToAdd, null)
    rewriter
  }

  def methodDeclarationInstrum(): Unit ={
    val methodDeclarationVisitor = new MethodDeclarationVisitor
    cu.accept(methodDeclarationVisitor)
    val methodDeclarations = methodDeclarationVisitor.getMethodDeclarations
    methodDeclarations.map(methodDeclarationInstrumHelper(_))
  }

  def methodDeclarationInstrumHelper(methodDeclaration: MethodDeclaration) = {
    val parameters: List[SingleVariableDeclaration] = methodDeclaration.parameters().asScala.toList.asInstanceOf[List[SingleVariableDeclaration]]
    val methodBody = methodDeclaration.getBody
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
