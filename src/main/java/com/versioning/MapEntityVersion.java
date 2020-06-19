package com.versioning;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.versioning.entity.Entity;
import com.versioning.entity.EntityVersion;
import com.versioning.map.EntityVersionMap;
import com.versioning.map.EntityVersionMapper;
import com.versioning.model.ExecuteVersion;

/**
 * Class that manages all entities versions available.<br>
 * It is done by storing every {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} object and
 * implementing an algorithm to return a list of entity mappers to be executed in order
 * to transform an entity from the initial version to the final version, 
 * and executing any intermediary version transformation that may be needed.<br><br>
 * Method {@link #registerMappers(EntityVersionMapper...) registerMappers()} must be called to
 * store (or register) the entities mappers {@link com.versioning.map.EntityVersionMapper EntityVersionMapper}.<br><br>
 * Later, a call to 
 * {@link #mapRequest(Entity, EntityVersion, ExecuteVersion) mapRequest()} or 
 * {@link #mapResponse(Entity, EntityVersion, ExecuteVersion) mapResponse()}
 * triggers the chain of execution to transform the version of the 
 * {@link com.versioning.entity.Entity Entity} passed as a parameter to the 
 * version accepted by the actual business execution class described by 
 * {@link com.versioning.model.ExecuteVersion ExecuteVersion}.
 * 
 * @author Haroldo Macêdo
 *
 */
class MapEntityVersion {

  private static Logger logger = LogManager.getFormatterLogger(MapEntityVersion.class.getName());

  /**
   * Main object of this framework.
   * Contains the all the version transformation objects for each entity name.
   */
  private static final Map<String, VersionPath> entities = new HashMap<>();

  /**
   * Transform the version of an {@link com.versioning.entity.Entity Entity} received in this request 
   * to the version accepted by the business executable object described by the 
   * {@link com.versioning.model.ExecuteVersion ExecuteVersion}.
   *  
   * @param entity - Entity to map from version {versionInput} to the version {executeVersion}.
   * @param versionInput - Version of the input entity.
   * @param executeVersion - Version of the input entity on the executable object.
   * @return Entity transformed to the executable version.
   * @throws VersioningConfigurationException - If some configuration error is found, such as lack of annotations.
   */
  static Entity mapRequest(Entity entity, EntityVersion versionInput, ExecuteVersion executeVersion) throws VersioningConfigurationException {
    logger.info("Mapping request of entity '%s' from version %d to version %d.", versionInput.name(), versionInput.version(), executeVersion.inputVersion());
    
    //  Don't know how to map different entities.
    if (!versionInput.name().equals(executeVersion.inputEntity()))
      throw new VersioningConfigurationException("Don't know how to map!. Trying to map entity '" + versionInput.name() + "' to entity '" + executeVersion.inputEntity() + "'." );
    
    if (versionInput.version() > executeVersion.inputVersion())
      throw new VersioningConfigurationException("Request can't map to previous version. Trying to map entity '" + versionInput.name() + "' from version " + versionInput.version() + " to version " + executeVersion.inputVersion() + ".");
    
    return map(entity, versionInput.name(), versionInput.version(), executeVersion.inputVersion());
  }
  
  /**
   * Transform the {@link com.versioning.entity.Entity Entity} version returned 
   * by the executable object described by 
   * {@link com.versioning.model.ExecuteVersion ExecuteVersion} to an {@link com.versioning.entity.Entity Entity} 
   * version that is accepted as the returned value of this response.
   * 
   * @param entity - Entity to map from version version {executeVersion} to the version {versionOutput}.
   * @param versionOutput - Version of the returned entity
   * @param executeVersion - Version returned by the executable object.
   * @return Entity transformed from the return of the executable version to the expected {versionOutput} version.
   * @throws VersioningConfigurationException - If some configuration error is found, such as lack of annotations.
   */
  static Entity mapResponse(Entity entity, EntityVersion versionOutput, ExecuteVersion executeVersion) throws VersioningConfigurationException {
    logger.info("Mapping response of entity '%s' from version %d to version %d.", versionOutput.name(), executeVersion.outputVersion(), versionOutput.version());

    //  Don't know how to map different entities.
    if (!versionOutput.name().equals(executeVersion.outputEntity()))
      throw new VersioningConfigurationException("Trying to map entity '" + versionOutput.name() + "' to entity '" + executeVersion.outputEntity() + "'. Don't know how to map!" );
    
    if (versionOutput.version() > executeVersion.outputVersion())
      throw new VersioningConfigurationException("Response can't map to next version. Trying to map entity '" + versionOutput.name() + "' from version " + executeVersion.outputVersion() + " to version " + versionOutput.version() + ".");
    
    return map(entity, versionOutput.name(), executeVersion.outputVersion(), versionOutput.version());
  }
  
  /**
   * Do the map, either request or response.
   * 
   * @param entity - Entity to map from version version {executeVersion} to the version {versionOutput}.
   * @param entityName - Name of the entity to map.
   * @param fromVersion - Initial version.
   * @param toVersion - Final version.
   * @return - Entity mapped to the final version.
   * @throws VersioningConfigurationException - If some configuration error is found, such as lack of annotations.
   */
  private static Entity map(Entity entity, String entityName, int fromVersion, int toVersion) throws VersioningConfigurationException {
    //  Same version, no mapping needed.
    if (fromVersion == toVersion)
      return entity;
    
    //  Prepare the list of mappers to map entities
    VersionPath versionPath = entities.get(entityName);
    if (versionPath == null)
      throw new VersioningConfigurationException("No class to map entity '" + entityName + "' from version " + fromVersion + " to version " + toVersion);
    List<EntityVersionMapper> mappers = versionPath.getMappingPath(fromVersion, toVersion);
    if (mappers == null)
      throw new VersioningConfigurationException("No mapping was found to map entity '" + entityName + "' from version " + fromVersion + " to version " + toVersion);

    //  Execute all mappers.
    Entity entityReturn = entity;
    for (EntityVersionMapper evm : mappers) {
      logger.debug(" - %s", evm.getClass().getName());
      entityReturn = evm.map(entityReturn);
    }
    logger.debug("Mapping End");
    
    return entityReturn;
  }
  
  /**
   * Register entity mappers.
   * 
   * @param entityVersionMappers - A list of entityVersionMappers to register.
   */
  static void registerMappers(EntityVersionMapper... entityVersionMappers) {
    logger.debug("Registering entity mappers");
    for (EntityVersionMapper entityVersionMapper : entityVersionMappers) {
        addVersionMapper(entityVersionMapper);
    }
    logger.debug("Registration done");
  }
  
  /**
   * Adds one entity mapper to the framework.
   * 
   * @param entityVersionMapper - Entity mapper that knwos how to transform entity from version {fromVersion} to {toVersion}.
   */
  private static void addVersionMapper(EntityVersionMapper entityVersionMapper) {
    //  Get the annotations of the entityVersionMapper.
    EntityVersionMap versionMapper;
    try {
      versionMapper = getMethodAnnotations(entityVersionMapper.getClass());
    } catch (VersioningConfigurationException e) {
      e.printStackTrace();
      return;
    }
    
    //  Validate version numbers.
    if (versionMapper.fromVersion() <= 0 || versionMapper.toVersion() <= 0) {
      logger.warn("Invalid version number of entity '%s'\tfrom version %d to version %d. Versions start in 1. Registration ignored.", 
          versionMapper.entityName(), versionMapper.fromVersion(), versionMapper.toVersion());
      return;
    }

    // No mapping for same versions.
    if (versionMapper.fromVersion() == versionMapper.toVersion()) {
      logger.warn("Ignoring same version maping of entity '%s'\tfrom version %d to version %d.", 
          versionMapper.entityName(), versionMapper.fromVersion(), versionMapper.toVersion());
      return;
    }

    //  Get the entity this mapping maps.
    VersionPath versionPath = entities.get(versionMapper.entityName());
    // If entityVersionMap doesn't exist, create one and store in {entities}.
    if (versionPath == null) {
      //  First time, create and store entity.
      versionPath = new VersionPath();
      entities.put(versionMapper.entityName(), versionPath);
    }

    //  Register entity mapper.
    logger.debug("\tMap entity '%s'\tfrom version %d to version %d", 
        versionMapper.entityName(), versionMapper.fromVersion(), versionMapper.toVersion());
    versionPath.add(entityVersionMapper, versionMapper.fromVersion(), versionMapper.toVersion());
  }

  /**
   * Read the annotations of a EntityVersionMapper method.
   * 
   * @param mapperClass
   * @return
   * @throws VersioningConfigurationException
   */
  private static EntityVersionMap getMethodAnnotations(Class<? extends EntityVersionMapper> mapperClass) throws VersioningConfigurationException {
    EntityVersionMap versionMapper;
    try {
      Method method = mapperClass.getMethod("map", new Class[] { Entity.class });
      versionMapper = (EntityVersionMap)Arrays
          .asList(method.getAnnotations())
          .stream()
          .filter(p -> p instanceof EntityVersionMap)
          .findFirst()
          .get();
    } catch (NoSuchMethodException | NoSuchElementException e) {
      throw new VersioningConfigurationException("Annotation for method 'map()' of " + mapperClass.getName() + " is not defined.");
    }

    return versionMapper;
  }
  
}

