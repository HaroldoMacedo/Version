package com.versioning.model;

import com.versioning.entity.Entity;

public interface MethodVersionExecuter {

  public Entity execute(Entity entity) throws VersioningConfigurationException;

}
