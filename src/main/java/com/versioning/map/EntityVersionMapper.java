package com.versioning.map;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

import com.versioning.entity.Entity;
import com.versioning.model.VersionMapExecutor;
import com.versioning.model.VersioningConfigurationException;

public abstract class EntityVersionMapper {

  public abstract Entity map(Entity entity);
  
  protected EntityVersionMapper() throws VersioningConfigurationException {
    VersionMapExecutor.addVersionMapper(this, getMethodAnnotations(this.getClass()));
  }
  
  private VersionMapper getMethodAnnotations(Class<? extends EntityVersionMapper> mapperClass) throws VersioningConfigurationException {
    VersionMapper versionMapper;
    try {
      Method method = mapperClass.getMethod("map", new Class[] { Entity.class });
      versionMapper = (VersionMapper)Arrays
          .asList(method.getAnnotations())
          .stream()
          .filter(p -> p instanceof VersionMapper)
          .findFirst()
          .get();
    } catch (NoSuchMethodException e) {
      throw new VersioningConfigurationException("Annotation for method 'execute()' of " + mapperClass.getName() + " is not defined.");
    } catch (NoSuchElementException e) {
      throw new VersioningConfigurationException("Annotation for method 'execute()' of " + mapperClass.getName() + " is not defined.");
    }
    
    
    return versionMapper;
  }

  
}
