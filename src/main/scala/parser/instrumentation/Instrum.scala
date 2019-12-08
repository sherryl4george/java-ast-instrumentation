package parser.instrumentation
import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom.{ASTNode, Block, CompilationUnit, EnhancedForStatement, ExpressionStatement, ForStatement, MethodDeclaration, ReturnStatement, Statement, VariableDeclarationStatement}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.{DoStatementVisitor, EnhancedForVisitor, ExpressionStatementVisitor, ForStatementVisitor, IfStatementVisitor, MethodDeclarationVisitor, ReturnStatementVisitor, SwitchStatementVisitor, VDStatementVisitor, WhileStatementVisitor}

/**
 * The Main Instrumentor class.
 * This is the seed for instrumenting all statements.
 * Instrumented statements include -
 * Assignments, Expression Statements (Method invocations, Field Access, Infix, Prefix, postfix expressions, Class Instance creation, Array access, array initialization ),
 * Method Declarations, Control Statements (if, for, while, do-while, for each, switch), Return Statements, Variable Declarations.
 * @param cu - A compilation unit.
 */
class Instrum(val cu: CompilationUnit) extends LazyLogging {
    private[this] val rewriter = ASTRewrite.create(cu.getAST)
    private[this] var nodes : List[ASTNode] = List()

  /**
   * The main method where instrumentation begins.
   * Visits all statements and creates a list of ASTNodes that were visited.
   * Invokes individual instrumentors on each of these nodes.
   * @return
   */
  def startInstrum(): ASTRewrite = {
      //Create individual visitors for each statement.
      logger.debug("Begin instrumentation")
      val expressionStatementVisitor = new ExpressionStatementVisitor
      val vdStatementVisitor = new VDStatementVisitor
      val returnStatementVisitor = new ReturnStatementVisitor
      val forStatementVisitor = new ForStatementVisitor
      val enhancedForStatementVisitor = new EnhancedForVisitor
      val doStatementVisitor = new DoStatementVisitor
      val whileStatementVisitor = new WhileStatementVisitor
      val ifStatementVisitor = new IfStatementVisitor
      val switchStatementVisitor = new SwitchStatementVisitor
      val methodDeclarationVisitor = new MethodDeclarationVisitor

      //Visit all statements.
      cu.accept(expressionStatementVisitor)
      cu.accept(vdStatementVisitor)
      cu.accept(returnStatementVisitor)
      cu.accept(forStatementVisitor)
      cu.accept(enhancedForStatementVisitor)
      cu.accept(doStatementVisitor)
      cu.accept(whileStatementVisitor)
      cu.accept(ifStatementVisitor)
      cu.accept(switchStatementVisitor)
      cu.accept(methodDeclarationVisitor)

      //Create a nodes list with all the visited nodes.
      expressionStatementVisitor.getExpressionStatements.map(createNodes(_))
      vdStatementVisitor.getVariableDeclarationStatements.map(createNodes(_))
      returnStatementVisitor.getReturnStatements.map(createNodes(_))
      forStatementVisitor.getForStatements.map(createNodes(_))
      enhancedForStatementVisitor.getForStatements.map(createNodes(_))
      doStatementVisitor.getDoStatements.map(createNodes(_))
      whileStatementVisitor.getWhileStatements.map(createNodes(_))
      ifStatementVisitor.getIfStatements.map(createNodes(_))
      switchStatementVisitor.getSwitchStatements.map(createNodes(_))
      methodDeclarationVisitor.getMethodDeclarations.map(createNodes(_))

      logger.info("Total number of nodes to be instrumented - " + nodes.length)
      //Invoke the instrumentor on each node.
      nodes.map(instrumHelper(_))
      rewriter
    }

  /**
   * The Instrumentor class that identifies the type of statement and invokes the corresponding instrumentor
   * Collects each instrumentor attributes and creates the instrumentation statement with information containing -
   * Line number, Statement Type, Binding and Variable name/value.
   * Rewrites by adding these instrumentation statements into the AST.
   * @param node
   * @return
   */
  def instrumHelper(node: ASTNode) = {
      //Identify the type of expression.
      node.getNodeType match {
        // Expression Statement
        case ASTNode.EXPRESSION_STATEMENT => {
          logger.debug("Expression statement instrument begin")
          val expressionStatement = node.asInstanceOf[ExpressionStatement]
          val attributes = new AssignmentInstrum().assignmentInstrumHelper(expressionStatement)
          val name = ExpressionUtils.getTextForExpression(expressionStatement.getExpression)
          logger.info("This is an expression of type -" + name)
          val log = makeLog(attributes,node,name)
          //Rewrite is done after the statement.
          rewrite(log,node,"after")
        }

        //Return Statement
        case ASTNode.RETURN_STATEMENT => {
          logger.debug("Return statement instrument begin")
          val parent = getParent(node.getParent)
          val returnStatement = node.asInstanceOf[ReturnStatement]
          val attributes = new ReturnInstrum().returnInstrumHelper(returnStatement)
          val log = makeLog(attributes,node,"ReturnStatement")
          //Rewrite is done before the statement.
          rewrite(log,node,"before")
        }

        //If, Switch, While, Do-while
        case ASTNode.SWITCH_STATEMENT |
             ASTNode.IF_STATEMENT |
            ASTNode.WHILE_STATEMENT |
            ASTNode.DO_STATEMENT => {
          logger.debug("Instrument control statements switch/if/while/do statements")
          val parent = getParent(node.getParent)
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          //Rewrite is done before statement.
          rewrite(log,node,"before")
        }

        /**
         * For and For-each - handled separately. The variable used in the initializer/expression may be declared within the for loop.
         * To handle this case, instrumentation is added  before the first statement in the loop body.
         */
        case ASTNode.FOR_STATEMENT => {
          logger.debug("For statement instrument begin")
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          rewrite(log,node.asInstanceOf[ForStatement].getBody,"first")
        }

        case ASTNode.ENHANCED_FOR_STATEMENT => {
          logger.debug("Enhanced For statement instrument begin")
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          rewrite(log,node.asInstanceOf[EnhancedForStatement].getBody,"first")
        }

        //Variable Declaration Statement
        case ASTNode.VARIABLE_DECLARATION_STATEMENT => {
          logger.debug("Variable declaration statement instrument begin")
          val parent = getParent(node.getParent)
          val results = new VDSInstrum().varDFragmentInstrumHelper(node.asInstanceOf[VariableDeclarationStatement])
          results.map(x => {
            val log = makeLog(x._1,x._2,node,"VariableDeclaration")
            //Rewrite is done after statement.
            rewrite(log,node,"after")
          })
        }

        //Method Declaration.
        case ASTNode.METHOD_DECLARATION => {
          logger.debug("Method Declaration instrument begin")
          val methodDeclaration = node.asInstanceOf[MethodDeclaration]
          val attributes = new MethodDeclarationInstrum().methodDeclarationInstrumHelper(methodDeclaration)
          val log = makeLog(attributes,node,"MethodDeclaration")
          val methodBody = methodDeclaration.getBody
          if(methodBody != null)
          //Instrumentation statements are added as the first statement in the method body.
            rewrite(log,methodBody,"first")
        }
      }
    }

  /**
   * create the nodes list. Add all visited nodes to the list.
   * @param node
   */
  def createNodes(node : ASTNode): Unit = {
      nodes = nodes :+ node
    }

  /**
   * Identifies the parent for each node. This is necessary to identify the binding.
   * @param parent
   * @return
   */
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
    else
        getParent(parent.getParent)
    }

  /**
   * Logging for Variable Declaration to handle the existence of an initializer as a special case .
   * Creates and returns the instrumentation statement to be inserted.
   * @param attributes
   * @param hasInitializer
   * @param node
   * @param name
   * @return
   */
    def makeLog(attributes : List[Attribute], hasInitializer : Boolean, node : ASTNode, name : String) = {
      var log = ""
      if (attributes.length > 0) {
        log = "TemplateClass.instrum(" + cu.getLineNumber(node.getStartPosition)
        log += ", " + utils.wrapStringInQuotes("VariableDeclaration")
        attributes.map(attribute => {
          if (hasInitializer)
            log += ", new AP(" + attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"
          else
            log += ", new AP(" + attribute.expType + ", " + attribute.binding + ", " + utils.wrapStringInQuotes("") + ")"
        }).mkString(", ")
        log += ");"
      }
      log
    }

  /**
   * Logging for all other statements other than Variable declaration statement. Creates and returns the instrumentation statement to be inserted.
   * @param attributes
   * @param node
   * @param name
   * @return
   */
    def makeLog(attributes : List[Attribute], node : ASTNode, name : String): String ={
      var log = ""
      if(attributes.length > 0) {
        log = "TemplateClass.instrum(" + cu.getLineNumber(node.getStartPosition)
        log += ", " + utils.wrapStringInQuotes(name)
        attributes.map(attribute => {
          log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"

        }).mkString(", ")
        log += ");"
      }
      log
    }

  /**
   * Adds the instrumentation statement into AST at the specified position using an ASTRewriter.
   * @param log
   * @param node
   * @param position
   */
    def rewrite(log:String, node: ASTNode, position:String) = {
      val textToAdd = cu.getAST.newTextElement
      textToAdd.setText(log)
      position match {
        case "after" => {
          val parent = getParent(node.getParent)
          val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
          lrw.insertAfter(textToAdd, node, null)
        }
        case "before" => {
          val parent = getParent(node.getParent)
          val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
          lrw.insertBefore(textToAdd, node, null)
        }
        case "first" => {
          val lrw = rewriter.getListRewrite(node, Block.STATEMENTS_PROPERTY)
          lrw.insertFirst(textToAdd, null)
        }
      }
    }
}
