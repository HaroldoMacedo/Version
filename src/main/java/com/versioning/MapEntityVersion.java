package com.versioning;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;
import com.versioning.map.EntityVersion;
import com.versioning.map.EntityVersionMapper;
import com.versioning.model.ExecuteVersion;
import com.versioning.model.VersionPath;

class MapEntityVersion {

  //
  //  Entities. Main object of this class. Contains the whole version framework structure.
  //
  private static final Map<String, VersionPath> entities = new HashMap<>();;

  /**
   * Execute a map for a request call on the entity {entity} version {versionInput} to the version {executeVersion}
   *  
   * @param entity - Entity to map from version {versionInput} to the version {executeVersion}.
   * @param versionInput - Version of the input entity.
   * @param executeVersion - Version of the input entity on the executable object.
   * @return Entity transformed to the executable version.
   * @throws VersioningConfigurationException - If some configuration error is found, such as lack of annotations.
   */
  static Entity mapRequest(Entity entity, VersionEntity versionInput, ExecuteVersion executeVersion) throws VersioningConfigurationException {
    System.out.println("Mapping request of entity '" + versionInput.name() + "' from version " + versionInput.version() + " to version " + executeVersion.inputVersion() + ".");
    
    //  Don't know how to map different entities.
    if (!versionInput.name().equals(executeVersion.inputEntity()))
      throw new VersioningConfigurationException("Don't know how to map!. Trying to map entity '" + versionInput.name() + "' to entity '" + executeVersion.inputEntity() + "'." );
    
    if (versionInput.version() > executeVersion.inputVersion())
      throw new VersioningConfigurationException("Request can't map to previous version. Trying to map entity '" + versionInput.name() + "' from version " + versionInput.version() + " to version " + executeVersion.inputVersion() + ".");
    
    return map(entity, versionInput.name(), versionInput.version(), executeVersion.inputVersion());
  }
  
  /**
   * Execute a map for a response on the entity {entity} version {executeVersion} to the version {versionOutput}
   * 
   * @param entity - Entity to map from version version {executeVersion} to the version {versionOutput}.
   * @param versionOutput - Version of the returned entity
   * @param executeVersion - Version returned by the executable object.
   * @return Entity transformed from the return of the executable version to the expected {versionOutput} version.
   * @throws VersioningConfigurationException - If some configuration error is found, such as lack of annotations.
   */
  static Entity mapResponse(Entity entity, VersionEntity versionOutput, ExecuteVersion executeVersion) throws VersioningConfigurationException {
    System.out.println("Mapping response of entity '" + versionOutput.name() + "' from version " + executeVersion.outputVersion() + " to version " + versionOutput.version() + ".");

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

    //  Execute all mappers.
    Entity entityReturn = entity;
    for (EntityVersionMapper evm : mappers) {
      System.out.println(" - " + evm.getClass().getName());
      entityReturn = evm.map(entityReturn);
    }
    System.out.println("Mapping End");
    
    return entityReturn;
  }
  
  /**
   * Register entity mappers.
   * 
   * @param entityVersionMappers - A list of entityVersionMappers to register.
   */
  static void registerMappers(EntityVersionMapper... entityVersionMappers) {
    System.out.println("Registering entity mappers");
    for (EntityVersionMapper entityVersionMapper : entityVersionMappers) {
        addVersionMapper(entityVersionMapper);
    }
    System.out.println("Registration done");
  }
  
  /**
   * Adds one entity mapper to the framework.
   * 
   * @param entityVersionMapper - Entity mapper that knwos how to transform entity from version {fromVersion} to {toVersion}.
   */
  private static void addVersionMapper(EntityVersionMapper entityVersionMapper) {
    //  Get the annotations of the entityVersionMapper.
    EntityVersion versionMapper;
    try {
      versionMapper = getMethodAnnotations(entityVersionMapper.getClass());
    } catch (VersioningConfigurationException e) {
      e.printStackTrace();
      return;
    }
    
    //  Validate version numbers.
    if (versionMapper.fromVersion() <= 0 || versionMapper.toVersion() <= 0) {
      System.out.println("Invalid version number of entity '" + versionMapper.entityName() + "'\tfrom version " + versionMapper.fromVersion() + " to version "
          + versionMapper.toVersion() + ". Versions start in 1. Registration ignored.");
      return;
    }

    // No mapping for same versions.
    if (versionMapper.fromVersion() == versionMapper.toVersion()) {
      System.out.println("Ignoring same version maping of entity '" + versionMapper.entityName() + "'\tfrom version " + versionMapper.fromVersion() + " to version " + versionMapper.toVersion() + ".");
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
    System.out.println("\tMap entity '" + versionMapper.entityName() + "'\tfrom version " + versionMapper.fromVersion() + " to version " + versionMapper.toVersion());
    versionPath.add(entityVersionMapper, versionMapper.fromVersion(), versionMapper.toVersion());
  }

  /**
   * Read the annotations of a EntityVersionMapper method.
   * 
   * @param mapperClass
   * @return
   * @throws VersioningConfigurationException
   */
  private static EntityVersion getMethodAnnotations(Class<? extends EntityVersionMapper> mapperClass) throws VersioningConfigurationException {
    EntityVersion versionMapper;
    try {
      Method method = mapperClass.getMethod("map", new Class[] { Entity.class });
      versionMapper = (EntityVersion)Arrays
          .asList(method.getAnnotations())
          .stream()
          .filter(p -> p instanceof EntityVersion)
          .findFirst()
          .get();
    } catch (NoSuchMethodException | NoSuchElementException e) {
      throw new VersioningConfigurationException("Annotation for method 'map()' of " + mapperClass.getName() + " is not defined.");
    }

    return versionMapper;
  }
  
}

