package com.versioning;

import com.versioning.map.EntityVersionMapper;

public abstract class VersionConfigure {

  public static void registerMappers(EntityVersionMapper... entityVersionMappers) {
    MapEntityVersion.registerMappers(entityVersionMappers);
  }  

}
