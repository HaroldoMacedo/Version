package com.versioning;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.versioning.map.EntityVersionMapper;

/**
 * Register the {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} objects in the framework.<br>
 * All entity mappers objects must be registered within the framework so they can be used. 
 *  
 * @author Haroldo Macêdo
 *
 */
public abstract class VersionConfigure {

  private static Logger logger = LogManager.getFormatterLogger(VersionConfigure.class.getName());

  public static void registerMappers(EntityVersionMapper... entityVersionMappers) {
    logger.debug("Registering %d objects of Entity Version Mappers", entityVersionMappers.length);
    MapEntityVersion.registerMappers(entityVersionMappers);
    logger.debug("Entity Version Mappers Registered");
  }  

}
