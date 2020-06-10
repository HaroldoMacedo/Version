package com.versioning.test;

import com.versioning.entity.Entity;
import com.versioning.map.EntityVersionMapper;
import com.versioning.map.VersionMapper;
import com.versioning.model.VersioningConfigurationException;

public class IdMap_V1V2 extends EntityVersionMapper {

  public IdMap_V1V2() throws VersioningConfigurationException {
  }

  @Override
  @VersionMapper(entityName = "Id", fromVersion = 1, toVersion = 2)
  public Entity map(Entity entity) {
    IdV1 idFrom = (IdV1)entity;

    IdV2 idTo = new IdV2(idFrom.getId() + ""); 

    return idTo;
  }
}
