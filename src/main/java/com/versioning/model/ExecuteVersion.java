package com.versioning.model;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used by the classes implement the actual operation execution.<br>  
 * It describes its input and output {@link com.versioning.entity.Entity Entity}
 * name and version when executing a business solution.
 * 
 * @author Haroldo Macêdo
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExecuteVersion {

  String inputEntity();
  int inputVersion();
  String outputEntity();
  int outputVersion();
}
