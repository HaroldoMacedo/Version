package com.versioning;

import com.versioning.entity.Entity;

/**
 * 
 * @author Haroldo Macêdo
 *
 */
public interface VersionExecuter {
  public Entity execute(Entity entity, Class<? extends Entity> returnEntityClass) throws VersioningConfigurationException;
}
