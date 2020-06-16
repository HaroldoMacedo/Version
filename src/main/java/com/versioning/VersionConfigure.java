package com.versioning;

import com.versioning.map.EntityVersionMapper;

/**
 * Register the {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} objects in the framework.<br>
 * All entity mappers objects must be registered within the framework so they can be used. 
 *  
 * @author Haroldo Macêdo
 *
 */
public abstract class VersionConfigure {

  public static void registerMappers(EntityVersionMapper... entityVersionMappers) {
    MapEntityVersion.registerMappers(entityVersionMappers);
  }  

}
