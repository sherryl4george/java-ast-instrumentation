package parser.converters

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.MethodDeclarationVisitor

import scala.jdk.CollectionConverters._

/**
 * The last bit of conversion to add imports, identify the Main() method.
 * This is invoked after instrumentation is done and all instrumentation statements are added.
 * @param cu
 */
class FinalConverter(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  /**
   * Begin Final instrumentation to identify Main() and add imports.
   * @return
   */
  def startInstrum(): ASTRewrite ={
    //Instrument all method declarations.
    methodDeclarationInstrum()

    //Add import statements. This is necessary to import the Template class and its required parameters.
    val lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY)
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText("import astparser.*;")
    lrw.insertLast(textToAdd, null)
    rewriter
  }

  /**
   * Identify all method declaration statements and visit them.
   * Invoke the instrumentation method on the the method declaration.
   * This was needed to identify the Main() class for adding final printing/file-writing method calls.
   * This will also be needed to start the program.
   */
  def methodDeclarationInstrum(): Unit ={
    val methodDeclarationVisitor = new MethodDeclarationVisitor
    cu.accept(methodDeclarationVisitor)
    val methodDeclarations = methodDeclarationVisitor.getMethodDeclarations
    methodDeclarations.map(methodDeclarationInstrumHelper(_))
  }

  /**
   * Identify the main() method to find the starting point of the program.
   * Also, add additional print/write to file method calls to get data.
   * @param methodDeclaration
   */
  def methodDeclarationInstrumHelper(methodDeclaration: MethodDeclaration) = {
    val parameters: List[SingleVariableDeclaration] = methodDeclaration.parameters().asScala.toList.asInstanceOf[List[SingleVariableDeclaration]]
    val methodBody = methodDeclaration.getBody

    // Check if the current method is public static void main(String[] args]
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
                // We have found the main
                val textToAdd = cu.getAST.newTextElement

                //Add writeToFile() method to the main() method.
                textToAdd.setText("TemplateClass.finalizeInstrum();")
                val lrw = rewriter.getListRewrite(methodBody, Block.STATEMENTS_PROPERTY)
                lrw.insertLast(textToAdd, null)
            }
          }
        }
      }
    }
  }
}
