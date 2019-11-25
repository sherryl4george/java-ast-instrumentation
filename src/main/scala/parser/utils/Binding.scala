package parser.utils

import org.eclipse.jdt.core.dom.{IBinding, IMethodBinding, IVariableBinding}

object Binding {

  def getBindingLabel(binding: IBinding): (String, String) = {
    if (binding == null) ("","") //$NON-NLS-1$
    else binding.getKind match {
      case IBinding.VARIABLE => {
        //        "> variable binding"

        val variableBinding: IVariableBinding = binding.asInstanceOf[IVariableBinding]

        val declaringClass = if (variableBinding.getDeclaringClass != null) variableBinding.getDeclaringClass.getQualifiedName else ""
        val declaringMethod = if (variableBinding != null && variableBinding.getDeclaringMethod != null) variableBinding.getDeclaringMethod.getName else ""
        val methodParameters = if (variableBinding != null && variableBinding.getDeclaringMethod != null && variableBinding.getDeclaringMethod.getParameterTypes != null) variableBinding.getDeclaringMethod.getParameterTypes.map(_.getName).mkString(", ") else ""
        val declaringMethodClass = if (variableBinding != null && variableBinding.getDeclaringMethod != null && variableBinding.getDeclaringMethod.getDeclaringClass != null) variableBinding.getDeclaringMethod.getDeclaringClass.getQualifiedName + "." else ""

        (if(declaringClass.length> 0) declaringClass else declaringMethodClass + declaringMethod + "(" + methodParameters +")", variableBinding.getVariableDeclaration.toString)
      }
      //      case IBinding.TYPE => "> type binding"
      case IBinding.METHOD => {
        //        "> method binding"
        val methodBinding: IMethodBinding = binding.asInstanceOf[IMethodBinding]
        val isMethodDeclaration = if (methodBinding eq methodBinding.getMethodDeclaration) " ( == this)" else " ( != this)"
        val declaringMethod = methodBinding.getMethodDeclaration().getName
        val methodParameters = if(methodBinding != null && methodBinding.getParameterTypes != null) methodBinding.getParameterTypes.map(_.getName).mkString(", ") else ""
        val declaringMethodClass = if(methodBinding != null && methodBinding.getDeclaringClass != null) methodBinding.getDeclaringClass.getQualifiedName else ""
        (declaringMethodClass, declaringMethodClass + "." + declaringMethod + "(" + methodParameters +")")
      }
//      case IBinding.TYPE => {
//        val typeBinding: ITypeBinding  = binding.asInstanceOf[ITypeBinding]
//
//        ("","")
//      }
      //      case IBinding.PACKAGE => "> package binding"
      //      case IBinding.ANNOTATION => "> annotation binding"
      //      case IBinding.MEMBER_VALUE_PAIR =>
      //        "> member value pair binding"
      //      case IBinding.MODULE =>
      //        "> module binding"
      case _ => ("","")
    }
  }

}
