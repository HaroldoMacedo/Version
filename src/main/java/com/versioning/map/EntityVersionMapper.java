package com.versioning.map;

import com.versioning.entity.Entity;

/**
 * Interface for transforming {@link com.versioning.entity.Entity Entity} versions by mapping entity's attributes from one
 * version to another.<BR>
 * 
 * Method {@link #map(Entity)} does the mapping transformation and is the only
 * method that is implemented by the mapping classes.
 * 
 * @author Haroldo Macêdo
 *
 */
public interface EntityVersionMapper {

  /**
   * Transform entity passed as a parameter and return the transformed entity in a newer version.<BR>
   * 
   * @param entity Entity of a version to be transformed.
   * @return Entity transformed to a new version.
   */
  public Entity map(Entity entity);
}
