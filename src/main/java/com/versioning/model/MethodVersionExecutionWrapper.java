package com.versioning.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;
import com.versioning.test.EmployeeMap_V2;
import com.versioning.test.EmployeeV2;
import com.versioning.test.GetEmployeePerIdMock_V2;
import com.versioning.test.IdMap_V1V2;
import com.versioning.test.IdV1;

/**
 * Wraps the execution of an operation by implementing a mapping algorithm to
 * execute the actual version of the operation.
 * 
 * @author Haroldo Macêdo
 *
 */
public class MethodVersionExecutionWrapper  {

  private ExecuteVersion executerVersionAnnoations;
  private MethodVersionExecuter methodVersionExecuter;

  /**
   * Create the wrapper to execute the actual available version of the operation.
   * 
   * @param methodVersionExecuter
   * @param returnEntityClass
   */
  public MethodVersionExecutionWrapper(MethodVersionExecuter methodVersionExecuter) throws VersioningConfigurationException {
    this.methodVersionExecuter = methodVersionExecuter;
    this.executerVersionAnnoations = getMethodAnnotations(methodVersionExecuter.getClass());
  }

  /**
   * Execute the actual operation version.
   * Maps the input object before calling the operation.
   * Maps the operation output to the return object version expected by the caller.  
   */
  public Entity execute(Entity entity, Class<? extends Entity> returnEntityClass) throws VersioningConfigurationException {

    VersionEntity inputEntityVersionAnnotations = getClassAnnotations(entity.getClass());
    VersionEntity outputEntityVersionAnnotations = getClassAnnotations(returnEntityClass);

    // Map Input
    Entity entityIn = VersionMapExecutor.mapRequest(entity, inputEntityVersionAnnotations, executerVersionAnnoations);

    // Execute operation
    Entity entityOut = methodVersionExecuter.execute(entityIn);

    // Map Output
    Entity entityRet = VersionMapExecutor.mapResponse(entityOut, outputEntityVersionAnnotations, executerVersionAnnoations);

    return entityRet;
  }

  /**
   * Return the annotations of the MethodVersionExecuter class.
   * 
   * @param executerClass
   * @return
   */
  private ExecuteVersion getMethodAnnotations(Class<? extends MethodVersionExecuter> executerClass) throws VersioningConfigurationException {
    ExecuteVersion entityVersionInput;
    try {
      Method method = executerClass.getMethod("execute", new Class[] { Entity.class });
      entityVersionInput = (ExecuteVersion)Arrays
          .asList(method.getAnnotations())
          .stream()
          .filter(p -> p instanceof ExecuteVersion)
          .findFirst()
          .get();
    } catch (NoSuchMethodException e) {
      throw new VersioningConfigurationException("Annotation for method 'execute()' of " + executerClass.getName() + " is not defined.");
    }
    
    return entityVersionInput;
  }

  /**
   * Return the annotations of the exec() method of an Entity class.
   * 
   * @param entity
   * @return
   */
  private VersionEntity getClassAnnotations(Class<? extends Entity> entity) throws VersioningConfigurationException {
    try {
      VersionEntity entityVersionAnnotations = (VersionEntity)Arrays
              .asList(entity.getAnnotations())
              .stream()
              .filter(p -> p instanceof VersionEntity)
              .findFirst()
              .get();
    
    return entityVersionAnnotations;
  } catch (NoSuchElementException e) {
    throw new VersioningConfigurationException("Annotation for class " + entity.getName() + " is not defined.");
  }
}

  // TODO: Remove this main method.
  public static void main(String[] args) throws VersioningConfigurationException {
    // Mocking a GET /employee/{id} factory
    new EmployeeMap_V2();
    new IdMap_V1V2();
    MethodVersionExecutionWrapper mve = new MethodVersionExecutionWrapper(new GetEmployeePerIdMock_V2());
    EmployeeV2 employee = (EmployeeV2)mve.execute(new IdV1(10), EmployeeV2.class);

//    System.out.println("Employee name = '" + employee.getFullName() + "'");
    System.out.println("Employee name = '" + employee.getFirstName() + "'");
  }
}
