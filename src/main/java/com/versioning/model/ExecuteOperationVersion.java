package com.versioning.model;

import com.versioning.VersioningConfigurationException;
import com.versioning.entity.Entity;

public interface ExecuteOperationVersion {

  public Entity execute(Entity entity) throws VersioningConfigurationException;

}
