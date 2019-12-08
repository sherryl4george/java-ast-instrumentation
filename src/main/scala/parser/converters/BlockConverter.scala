package parser.converters

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.{DoStatementVisitor, EnhancedForVisitor, ForStatementVisitor, IfStatementVisitor, WhileStatementVisitor}

/**
 * Converts all constructs to blocks.
 * This is done as a first step in re-writing.
 * This helps in simplifying detailed re-writing as well as adding instrumentation in non-blocked statements in the future.
 * @param cu
 */
class BlockConverter(val cu: CompilationUnit) extends LazyLogging{
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  /**
   * converts each clause to a block and returns a rewriter.
   * @return ASTRewrite
   */
  def startBlockConvert(): ASTRewrite = {
    logger.debug("Begin block rewriting")
    block()
    logger.debug("Block rewrite ends")
    rewriter
}

  /**
   * The block conversion method
   * Converts while, for, do-while, if, for-each statements into blocks, if not already blocked.
   * while(i > 0)
   * i++ ;        -----------------> modified to
   * while(i > 0) {
   * i++;
   * }
   */
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

    val totalStatements = whileStatements.length + forStatements.length + doStatements.length + ifStatements.length + enhancedForStatements.length
    logger.info("Total statements that are going to be re-written - " + totalStatements)

    whileStatements.map(whileToBody(_))
    doStatements.map(doToBody(_))
    forStatements.map(forToBody(_))
    enhancedForStatements.map(enhancedForToBody(_))
    ifStatements.map(ifToBody(_))
  }

  /**
   * Blocks if-elseif-else statements, if they contain single statements without blocks.
   * @param ifStatement
   */
  def ifToBody(ifStatement : IfStatement) = {
    logger.debug("If Blocking begin")
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
          logger.debug("Else body blocking done")
        }
    logger.debug("If Blocking done")
    }

  /**
   * Blocks while statements, if they contain single statements without blocks.
   * @param whileStatement
   */
    def whileToBody(whileStatement : WhileStatement) = {
      logger.debug("While blocking begin")
      val whileBody = whileStatement.getBody
      if (!whileBody.isInstanceOf[Block]) {
        val block = whileStatement.getAST.newBlock
        val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(whileBody, null)
        rewriter.replace(whileBody, block, null)
      }
      logger.debug("while blocking end")
    }

  /**
   * Blocks for statements if they contain single statements without a block.
   * @param forStatement
   */
  def forToBody(forStatement : ForStatement) = {
    logger.debug("For Body begin")
    val forBody = forStatement.getBody
    if (!forBody.isInstanceOf[Block]) {
      val block = forStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(forBody, null)
      rewriter.replace(forBody, block, null)
    }
    logger.debug("For Body end")
  }

  /**
   * Blocks range based for statements if they contain single statements without a block.
   * @param forStatement
   */
  def enhancedForToBody(forStatement : EnhancedForStatement) = {
    logger.debug("Enhanced For Body begin")
    val forBody = forStatement.getBody
    if (!forBody.isInstanceOf[Block]) {
      val block = forStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(forBody, null)
      rewriter.replace(forBody, block, null)
    }
    logger.debug("Enhanced For Body End")
  }

  /**
   * Blocks do-while statements if they contain single statements without a block.
   * @param doStatement
   */
  def doToBody(doStatement : DoStatement) = {
    logger.debug("Do body block begin")
    val doBody = doStatement.getBody
    if (!doBody.isInstanceOf[Block]) {
      val block = doStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(doBody, null)
      rewriter.replace(doBody, block, null)
    }
    logger.debug("Do body block end")
  }
  }


