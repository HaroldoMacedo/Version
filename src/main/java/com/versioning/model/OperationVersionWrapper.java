package com.versioning.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

import com.versioning.VersioningConfigurationException;
import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;
import com.versioning.map.MapEntityVersion;

/**
 * Wraps the execution of an operation by deciding which version to call.
 * Also, before calling the operator, mapping the input object to the correct version.
 * Before returning, map to the caller expected version.
 * 
 * @author Haroldo Macêdo
 *
 */
public class OperationVersionWrapper  {

  private ExecuteVersion executerVersionAnnoations;
  private ExecuteOperationVersion executeOperationVersion;

  /**
   * Create the wrapper to execute the actual available version of the operation.
   * 
   * @param executeOperationVersion
   * @param returnEntityClass
   */
  public OperationVersionWrapper(ExecuteOperationVersion executeOperationVersion) throws VersioningConfigurationException {
    this.executeOperationVersion = executeOperationVersion;
    this.executerVersionAnnoations = getMethodAnnotations(executeOperationVersion.getClass());
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
    Entity entityIn = MapEntityVersion.mapRequest(entity, inputEntityVersionAnnotations, executerVersionAnnoations);

    // Execute operation
    Entity entityOut = executeOperationVersion.execute(entityIn);

    // Map Output
    Entity entityRet = MapEntityVersion.mapResponse(entityOut, outputEntityVersionAnnotations, executerVersionAnnoations);

    return entityRet;
  }

  /**
   * Return the annotations of the ExecuteOperationVersion class.
   * 
   * @param executerClass
   * @return
   */
  private ExecuteVersion getMethodAnnotations(Class<? extends ExecuteOperationVersion> executerClass) throws VersioningConfigurationException {
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
      VersionEntity entityVersionAnnotations = (VersionEntity) Arrays
          .asList(entity.getAnnotations())
          .stream()
          .filter(p -> p instanceof VersionEntity)
          .findFirst().get();

      return entityVersionAnnotations;
    } catch (NoSuchElementException e) {
      throw new VersioningConfigurationException("Annotation for class " + entity.getName() + " is not defined.");
    }
  }
}
