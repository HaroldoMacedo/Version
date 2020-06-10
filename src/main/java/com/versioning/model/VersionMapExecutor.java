package com.versioning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;
import com.versioning.map.EntityVersionMapper;
import com.versioning.map.VersionMapper;

public class VersionMapExecutor {

  static {
    System.out.println("   ---- Carregando classe VersionMapExecutor ----");
    entities = new HashMap<>();
  }
  
  //
  //  Entities. Main object of this class. Contains the whole version framework structure.
  private static final Map<String, FromToEntityVersionMappers> entities;
  private static final List<EntityVersionMapper> noMap = new ArrayList<>();

  /**
   * Add a version mapper to map versions.
   * @param entityVersionMapper
   * @param versionMapper
   */
  public static void addVersionMapper(EntityVersionMapper entityVersionMapper, VersionMapper versionMapper) {
    FromToEntityVersionMappers evm = getEntityVersionMap(versionMapper.entityName());
    evm.include(entityVersionMapper, versionMapper.fromVersion(), versionMapper.toVersion());
  }

  private static FromToEntityVersionMappers getEntityVersionMap(String name) {
    //  Find the entityVersionMap of entity {name}.
    FromToEntityVersionMappers _evm = entities.get(name);
    // If entityVersionMap doesn't exist, create one and store in {entities}.
    if (_evm == null) {
      _evm = new FromToEntityVersionMappers();
      entities.put(name, _evm);
    }
    return _evm;
  }

  /**
   * Execute a map for a request call on the entity {entity} version {versionInput} to the version {executeVersion}
   *  
   * @param entity - Entity to map from version {versionInput} to the version {executeVersion}.
   * @param versionInput - Version of the input entity.
   * @param executeVersion - Version of the input entity on the executable object.
   * @return Entity transformed to the executable version.
   */
  public static Entity mapRequest(Entity entity, VersionEntity versionInput, ExecuteVersion executeVersion) throws VersioningConfigurationException {
    System.out.println("Mapping request of entity '" + executeVersion.inputEntity() + "' from version " + versionInput.version() + " to version " + executeVersion.inputVersion());
    
    //  Don't know how to map different entities.
    if (!versionInput.name().equals(executeVersion.inputEntity()))
      throw new VersioningConfigurationException("Trying to map entity '" + versionInput.name() + "' to entity '" + executeVersion.inputEntity() + "'. Don't know how to map!" );
    
    //  Same version, no mapping needed.
    if (versionInput.version() == executeVersion.inputVersion())
      return entity;
    
    Entity entityReturn = entity;
    for (EntityVersionMapper evm : findMappers(versionInput.name(), versionInput.version(), executeVersion.inputVersion()))
      entityReturn = evm.map(entityReturn);
    
    return entityReturn;
  }
  
  /**
   * Execute a map for a response on the entity {entity} version {executeVersion} to the version {versionOutput}
   * 
   * @param entity - Entity to map from version version {executeVersion} to the version {versionOutput}.
   * @param versionOutput - Version of the returned entity
   * @param executeVersion - Version returned by the executable object.
   * @return Entity transformed from the return of the executable version to the expected {versionOutput} version.
   */
  public static Entity mapResponse(Entity entity, VersionEntity versionOutput, ExecuteVersion executeVersion) {
    System.out.println("Mapping request of entity '" + executeVersion.outputEntity() + "' from version " + executeVersion.outputVersion() + " to version " + versionOutput.version());
    return entity;
  }
  
  private static Iterable<EntityVersionMapper> findMappers(String entityName, int fromVersion, int toVersion) throws VersioningConfigurationException {
    FromToEntityVersionMappers evm = entities.get(entityName);
    if (evm == null)
      throw new VersioningConfigurationException("No class to map entity '" + entityName + "' from version " + fromVersion + " to version " + toVersion);
    return evm.getVersionsMapperFromTo(fromVersion, toVersion);
  }
}

/**
 * Stores all version mappers of an entity.
 * @author Haroldo Macêdo
 *
 */
class FromToEntityVersionMappers {
  // Attributes.
  private Map<FromToVersions, EntityVersionMapper> entityMapper = new HashMap<>();

  /**
   * Return list of the version mappers from initial to final version.
   * 
   * @param fromVersion
   * @param toVersion
   * @return
   */
  List<EntityVersionMapper> getVersionsMapperFromTo(int fromVersion, int toVersion) {
    List<EntityVersionMapper> evm = new ArrayList<>();
    for (int version = fromVersion; version < toVersion; version++) {
      //  TODO: Include mappers to skiped versions (non-sequencial). 
      evm.add(entityMapper.get(new FromToVersions(version, version+1)));
    }
    
    return evm;
  }
  
  // Entity Version Map
  ///////////////////////////////////////////////////////////////////////////
  void include(EntityVersionMapper entityVersionMapper, int fromVersion, int toVersion) {
    FromToVersions fromToVersions = new FromToVersions(fromVersion, toVersion);

    // If this mapperVersion has been registered, don't register again.
    EntityVersionMapper em = entityMapper.get(fromToVersions);
    if (em != null) {
      // If trying to register another mapVersion to the same entity and from and to versions, with a different class, log an error.
      if (!em.getClass().equals(entityVersionMapper.getClass()))
        System.out.println("Version entity for " + entityVersionMapper.getClass() + " that maps from version " + fromVersion + " to " + toVersion
            + " is duplicated and will not be used.");
      return;
    }

    //  Store entityMapper.
    entityMapper.put(fromToVersions, entityVersionMapper);
  }
}

