package parser.utils

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom.{IBinding, IMethodBinding, ITypeBinding, IVariableBinding}

object Binding extends LazyLogging{

  def getBindingLabel(binding: IBinding): (String, String) = {
    logger.trace("Finding binding of item - Started")
    /**
      * Handle only MethodBinding and VariableBinding
      * Does not hadle any other kind of bindings
      */
    if (binding == null) ("","") //$NON-NLS-1$
    else binding.getKind match {
      case IBinding.VARIABLE => {
        // Get the qualified name and also the binding and pass it back
        val variableBinding: IVariableBinding = binding.asInstanceOf[IVariableBinding]
        (recurseBinding(variableBinding), variableBinding.getVariableDeclaration.toString)
      }
      case IBinding.METHOD => {
        // Get the declaringMethodClass and get the binding and pass it back
        val methodBinding: IMethodBinding = binding.asInstanceOf[IMethodBinding]
        val declaringMethodClass = if(methodBinding != null && methodBinding.getDeclaringClass != null) methodBinding.getDeclaringClass.getQualifiedName else ""
        (declaringMethodClass, recurseBinding(methodBinding))
      }
      case _ => ("","")
    }
  }

  /**
    * Recursively find the bind of each variable
    * We deal wit only Type, Method and Variable Binding
    * Type can be declared in a class or method
    * Method can be declared only in class
    * Variable can be declared in a class or method
    * Using the above info recurse till we get a binding not
    * in the above list or null and give back the qualifying name of the variable
    * @param binding
    * @return
    */
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
