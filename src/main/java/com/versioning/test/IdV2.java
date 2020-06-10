package com.versioning.test;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;

/**
 * Id to be returned by POST and PUT methods.
 * 
 * @author Haroldo Macêdo
 *
 */
@VersionEntity(name="Id", version=2)
public class IdV2 implements Entity {

  private String id;

  public IdV2(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
