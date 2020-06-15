package com.versioning;

import com.versioning.entity.Entity;
import com.versioning.model.ExecuteOperationVersion;

public interface VersionExecuter {
  public Entity execute(Entity entity, Class<? extends Entity> returnEntityClass) throws VersioningConfigurationException;
  
  public static VersionExecuter get(ExecuteOperationVersion executer) throws VersioningConfigurationException {
    return new VersionOperationWrapper(executer);
  }
}
