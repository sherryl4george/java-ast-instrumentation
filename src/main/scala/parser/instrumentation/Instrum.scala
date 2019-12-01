package parser.instrumentation
import org.eclipse.jdt.core.dom.{ASTNode, Block, CompilationUnit, DoStatement, EnhancedForStatement, Expression, ExpressionStatement, ForStatement, IfStatement, MethodDeclaration, ReturnStatement, SingleVariableDeclaration, Statement, SwitchStatement, VariableDeclaration, VariableDeclarationFragment, VariableDeclarationStatement, WhileStatement}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.{DoStatementVisitor, EnhancedForVisitor, ExpressionStatementVisitor, ForStatementVisitor, IfStatementVisitor, MethodDeclarationVisitor, ReturnStatementVisitor, SwitchStatementVisitor, VDStatementVisitor, WhileStatementVisitor}

class Instrum(val cu: CompilationUnit){
    private[this] val rewriter = ASTRewrite.create(cu.getAST)
    private[this] var nodes : List[ASTNode] = List()

  def startInstrum(): ASTRewrite = {
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

      expressionStatementVisitor.getExpressionStatements.map(createNodes(_))
      vdStatementVisitor.getExpressionStatements.map(createNodes(_))
      returnStatementVisitor.getReturnStatements.map(createNodes(_))
      forStatementVisitor.getForStatements.map(createNodes(_))
      enhancedForStatementVisitor.getForStatements.map(createNodes(_))
      doStatementVisitor.getDoStatements.map(createNodes(_))
      whileStatementVisitor.getWhileStatements.map(createNodes(_))
      ifStatementVisitor.getIfStatements.map(createNodes(_))
      switchStatementVisitor.getSwitchStatements.map(createNodes(_))
      methodDeclarationVisitor.getMethodDeclarations.map(createNodes(_))
      nodes.map(instrumHelper(_))
      rewriter
    }

    def instrumHelper(node: ASTNode) = {
      node.getNodeType match {
        case ASTNode.EXPRESSION_STATEMENT => {
          val expressionStatement = node.asInstanceOf[ExpressionStatement]
          val attributes = new AssignmentInstrum().assignmentInstrumHelper(expressionStatement)
          val name = ExpressionUtils.getTextForExpression(expressionStatement.getExpression)
          val log = makeLog(attributes,node,name)
          rewrite(log,node,"after")
        }

        case ASTNode.RETURN_STATEMENT => {
          val parent = getParent(node.getParent)
          val returnStatement = node.asInstanceOf[ReturnStatement]
          val attributes = new ReturnInstrum().returnInstrumHelper(returnStatement)
          val log = makeLog(attributes,node,"ReturnStatement")
          rewrite(log,node,"before")
        }

        case ASTNode.SWITCH_STATEMENT |
             ASTNode.IF_STATEMENT |
            ASTNode.WHILE_STATEMENT |
            ASTNode.DO_STATEMENT => {
          val parent = getParent(node.getParent)
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          rewrite(log,node,"before")
        }

        case ASTNode.FOR_STATEMENT => {
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          rewrite(log,node.asInstanceOf[ForStatement].getBody,"first")
        }

        case ASTNode.ENHANCED_FOR_STATEMENT => {
          val (attributes, name)= new ControlInstrum().controlInstrumHelper(node.asInstanceOf[Statement])
          val log = makeLog(attributes, node, name)
          rewrite(log,node.asInstanceOf[EnhancedForStatement].getBody,"first")
        }

        case ASTNode.VARIABLE_DECLARATION_STATEMENT => {
          val parent = getParent(node.getParent)
          val results = new VDSInstrum().varDFragmentInstrumHelper(node.asInstanceOf[VariableDeclarationStatement])
          results.map(x => {
            val log = makeLog(x._1,x._2,node,"VariableDeclaration")
            rewrite(log,node,"after")
          })
        }

        case ASTNode.METHOD_DECLARATION => {
          val methodDeclaration = node.asInstanceOf[MethodDeclaration]
          val attributes = new MethodDeclarationInstrum().methodDeclarationInstrumHelper(methodDeclaration)
          val log = makeLog(attributes,node,"MethodDeclaration")
          val methodBody = methodDeclaration.getBody
          if(methodBody != null)
            rewrite(log,methodBody,"first")
        }
      }
    }

    def createNodes(node : ASTNode): Unit = {
      nodes = nodes :+ node
    }

    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
    else
        getParent(parent.getParent)
    }

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
