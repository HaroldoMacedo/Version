package com.versioning.entity;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation that identify the name and version of this entity.<br>
 * Classes with this annotation must implement the {@link Entity} interface.
 * 
 * @author Haroldo Macêdo
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface VersionEntity {

  String name();
  int version();
}
