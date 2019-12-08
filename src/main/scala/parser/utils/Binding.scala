package parser.utils

import org.eclipse.jdt.core.dom.{IBinding, IMethodBinding, ITypeBinding, IVariableBinding}

object Binding {

  def getBindingLabel(binding: IBinding): (String, String) = {
    if (binding == null) ("","") //$NON-NLS-1$
    else binding.getKind match {
      case IBinding.VARIABLE => {
        val variableBinding: IVariableBinding = binding.asInstanceOf[IVariableBinding]
//        (if(declaringClass.length> 0) declaringClass else declaringMethodClass + declaringMethod + "(" + methodParameters +")", variableBinding.getVariableDeclaration.toString)
        (recurseBinding(variableBinding), variableBinding.getVariableDeclaration.toString)
      }
      case IBinding.METHOD => {
        val methodBinding: IMethodBinding = binding.asInstanceOf[IMethodBinding]
        val declaringMethodClass = if(methodBinding != null && methodBinding.getDeclaringClass != null) methodBinding.getDeclaringClass.getQualifiedName else ""
        (declaringMethodClass, recurseBinding(methodBinding))
      }
      case _ => ("","")
    }
  }

  def recurseBinding(binding: IBinding): String = {
    binding match {
      case typeBinding: IVariableBinding => {
        if (typeBinding.getDeclaringClass != null) {
          recurseBinding(typeBinding.getDeclaringClass) + "." + typeBinding.getName
        } else if (typeBinding.getDeclaringMethod != null) {
          recurseBinding(typeBinding.getDeclaringMethod) + "." + typeBinding.getName
        } else {
          typeBinding.getName
        }
      }
      case typeBinding: ITypeBinding => {
        if (typeBinding.getDeclaringClass != null) {
          recurseBinding(typeBinding.getDeclaringClass) + "." + typeBinding.getName
        } else if (typeBinding.getDeclaringMethod != null) {
          recurseBinding(typeBinding.getDeclaringMethod) + "." + typeBinding.getName
        } else {
          typeBinding.getName
        }
      }
      case typeBinding: IMethodBinding => {
        val bindingName: String = typeBinding.getName + "(" + typeBinding.getParameterTypes.map(_.getName).mkString(", ") + ")"
        if (typeBinding.getDeclaringClass != null) {
          recurseBinding(typeBinding.getDeclaringClass) + "." + bindingName
        } else {
          bindingName
        }
      }
      case _ => ""
    }
  }
}
