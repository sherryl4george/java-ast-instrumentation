package parser.converters

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.{DoStatementVisitor, EnhancedForVisitor, ForStatementVisitor, IfStatementVisitor, WhileStatementVisitor}


/** *
  * Change all statements to blocks.
  */
class BlockConverter(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  def startBlockConvert(): ASTRewrite = {
    block()
    rewriter
  }

  def block(): Unit = {
    val whileStatementVisitor = new WhileStatementVisitor
    val forStatementVisitor = new ForStatementVisitor
    val doStatementVisitor = new DoStatementVisitor
    val ifStatementVisitor = new IfStatementVisitor
    val rangeForStatementVisitor = new EnhancedForVisitor

    cu.accept(whileStatementVisitor)
    cu.accept(doStatementVisitor)
    cu.accept(forStatementVisitor)
    cu.accept(ifStatementVisitor)
    cu.accept(rangeForStatementVisitor)

    val whileStatements = whileStatementVisitor.getWhileStatements
    val forStatements = forStatementVisitor.getForStatements
    val doStatements = doStatementVisitor.getDoStatements
    val ifStatements = ifStatementVisitor.getIfStatements
    val enhancedForStatements = rangeForStatementVisitor.getForStatements

    whileStatements.map(whileToBody(_))
    doStatements.map(doToBody(_))
    forStatements.map(forToBody(_))
    enhancedForStatements.map(enhancedForToBody(_))
    ifStatements.map(ifToBody(_))
  }

    def ifToBody(ifStatement : IfStatement) = {
     val ifBody = ifStatement.getThenStatement
      if(!ifBody.isInstanceOf[Block]){
        val block = ifStatement.getAST.newBlock
        val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(ifBody, null)
        rewriter.replace(ifBody, block, null)
      }
      val elseBody = ifStatement.getElseStatement
      if(elseBody != null && !elseBody.isInstanceOf[Block])
        {
          val block = ifStatement.getElseStatement.getAST.newBlock()
          val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
          lrw.insertLast(elseBody, null)
          rewriter.replace(elseBody, block, null)
        }
    }

    def whileToBody(whileStatement : WhileStatement) = {
      val whileBody = whileStatement.getBody
      if (!whileBody.isInstanceOf[Block]) {
        val block = whileStatement.getAST.newBlock
        val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(whileBody, null)
        rewriter.replace(whileBody, block, null)
      }
    }

  def forToBody(forStatement : ForStatement) = {
    val forBody = forStatement.getBody
    if (!forBody.isInstanceOf[Block]) {
      val block = forStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(forBody, null)
      rewriter.replace(forBody, block, null)
    }
  }

  def enhancedForToBody(forStatement : EnhancedForStatement) = {
    val forBody = forStatement.getBody
    if (!forBody.isInstanceOf[Block]) {
      val block = forStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(forBody, null)
      rewriter.replace(forBody, block, null)
    }
  }

  def doToBody(doStatement : DoStatement) = {
    val doBody = doStatement.getBody
    if (!doBody.isInstanceOf[Block]) {
      val block = doStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(doBody, null)
      rewriter.replace(doBody, block, null)
    }
  }
  }


