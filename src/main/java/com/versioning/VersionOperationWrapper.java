package com.versioning;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.versioning.entity.Entity;
import com.versioning.entity.EntityVersion;
import com.versioning.model.ExecuteOperationVersion;
import com.versioning.model.ExecuteVersion;

/**
 * Wraps the execution of an operation by deciding which version to call.
 * Also, before calling the operator, mapping the input object to the correct version.
 * Before returning, map to the caller expected version.
 * 
 * @author Haroldo Macêdo
 *
 */
class VersionOperationWrapper implements VersionExecuter {

  private static Logger logger = LogManager.getFormatterLogger(VersionOperationWrapper.class.getName());
  
  private ExecuteVersion executerVersionAnnoations;
  private ExecuteOperationVersion executeOperationVersion;

  /**
   * Create the wrapper to execute the actual available version of the operation.
   * 
   * @param executeOperationVersion
   * @param returnEntityClass
   */
  VersionOperationWrapper(ExecuteOperationVersion executeOperationVersion) throws VersioningConfigurationException {
    this.executeOperationVersion = executeOperationVersion;
    this.executerVersionAnnoations = getMethodAnnotations(executeOperationVersion.getClass());
  }

  /**
   * Execute the actual operation by transforming the input entity version, calling the executer and 
   * transforming back to the expected returned entity version.<br>
   * <ul><li>Maps the input object before calling the operation.</li>
   * <li>Executes the operation.</li>
   * <li>Maps the operation output to the return entity version expected by the caller.</li></ul>  
   */
  @Override
  public Entity execute(Entity entity, Class<? extends Entity> returnEntityClass) throws VersioningConfigurationException {

    EntityVersion inputEntityVersionAnnotations = getClassAnnotations(entity.getClass());
    EntityVersion outputEntityVersionAnnotations = getClassAnnotations(returnEntityClass);

    logger.debug("Executing %s: Input is entity %s v%d. Output is entity %s v%d", executeOperationVersion.getClass().getName(), 
        inputEntityVersionAnnotations.name(), inputEntityVersionAnnotations.version(),
        outputEntityVersionAnnotations.name(), outputEntityVersionAnnotations.version());
    
    // Map Input
    Entity entityIn = MapEntityVersion.mapRequest(entity, inputEntityVersionAnnotations, executerVersionAnnoations);

    // Execute operation
    Entity entityOut = executeOperationVersion.execute(entityIn);

    // Map Output
    Entity entityRet = MapEntityVersion.mapResponse(entityOut, outputEntityVersionAnnotations, executerVersionAnnoations);

    logger.debug("Executed %s", executeOperationVersion.getClass().getName()); 
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
    } catch (Exception e) {
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
  private EntityVersion getClassAnnotations(Class<? extends Entity> entity) throws VersioningConfigurationException {
    try {
      EntityVersion entityVersionAnnotations = (EntityVersion) Arrays
          .asList(entity.getAnnotations())
          .stream()
          .filter(p -> p instanceof EntityVersion)
          .findFirst().get();

      return entityVersionAnnotations;
    } catch (Exception e) {
      throw new VersioningConfigurationException("Annotation for class " + entity.getName() + " is not defined.");
    }
  }
}
