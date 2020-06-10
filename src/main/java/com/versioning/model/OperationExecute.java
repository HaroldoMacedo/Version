package com.versioning.model;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @deprecated
 * @author Haroldo Macêdo
 *
 */
public abstract class OperationExecute {
  
  public OperationExecute() {
    ExecuteVersion executeVersion = getMethodAnnotations();
    if (executeVersion == null)
      return;
      
    //  TODO: Remove the println.
    System.out.println("Input : '" + executeVersion.inputEntity() + "', v" + executeVersion.inputVersion());
    System.out.println("Output : '" + executeVersion.outputEntity() + "', v" + executeVersion.outputVersion());
  }
  
  /**
   * Get the annotations for this class.
   * 
   * @return
   */
  private ExecuteVersion getMethodAnnotations() {
    Method method; 
    try {
      //  Find the method in the class.
      method = Arrays
          .asList(this.getClass().getMethods())
          .stream()
          .filter(p-> p.getName().equals(("execute")))
          .findFirst()
          .get();

    } catch (Exception e) {
      System.out.println("Method execute is not present");
      e.printStackTrace();
      return null;
    }
    try {
      //  Return the ExecuteVersion annotation.
      return (ExecuteVersion)Arrays
          .asList(method.getAnnotations())
          .stream()
          .filter(p -> p instanceof ExecuteVersion)
          .findFirst()
          .get();
    } catch (Exception e) {
      System.out.println("Annotation not found");
      e.printStackTrace();
      return null;
    }
  }
}
