package com.versioning.map;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used by the transformation classes.<br>
 * It defines which entity it transform and the initial and final version this entity is transformed.
 * s 
 * @author Haroldo Macêdo
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EntityVersion {
  String entityName();
  int fromVersion();
  int toVersion();
}
