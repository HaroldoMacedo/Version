package com.versioning.model;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.versioning.entity.Entity;

/**
 * @deprecated
 * @author Haroldo Macêdo
 *
 */
public class ExecuteVersionMapper {

  private ExecuteVersion executeVersion;
  
  ExecuteVersionMapper(MethodVersionExecuter executer) {
    executeVersion = getMethodAnnotations(executer);
    if (executeVersion == null)
      return;
      
    //  TODO: Remove the println.
    System.out.println("Input : '" + executeVersion.inputEntity() + "', v" + executeVersion.inputVersion());
    System.out.println("Output : '" + executeVersion.outputEntity() + "', v" + executeVersion.outputVersion());
    
  }

  public ExecuteVersion getExecuteVersion() {
    return executeVersion;
  }

  /**
   * Get the annotations for the "execute()" method.
   * 
   * @return
   */
  private ExecuteVersion getMethodAnnotations(MethodVersionExecuter executer) {
//    Method method; 
    try {
      //  Find the method in the class.
//      method = Arrays
//          .asList(executer.getClass().getMethods())
//          .stream()
//          .filter(p-> p.getName().equals(("execute")))
//          .findFirst()
//          .get();
      Method method = executer.getClass().getMethod("execute", new Class[] { Entity.class });

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
