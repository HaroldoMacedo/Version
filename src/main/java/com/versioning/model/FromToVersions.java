package com.versioning.model;

import com.versioning.entity.Entity;

/**
 * 
 * @author Haroldo Macêdo
 *
 */
class FromToVersions {
  int fromVersion;
  int toVersion;
  private int hash;

  FromToVersions(int fromVersion, int toVersion) {
    this.fromVersion = fromVersion;
    this.toVersion = toVersion;
    hash = fromVersion * 100 + toVersion;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FromToVersions))
      return false;

    FromToVersions emv = (FromToVersions) obj;
    return emv.hash == this.hash;
  }
  
  class InOutEntityVersions {
    int version;
    private int hash;

    InOutEntityVersions(Entity entity, int version) {
      this.version = version;
      hash = version * 100 + toVersion;
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FromToVersions))
        return false;

      FromToVersions emv = (FromToVersions) obj;
      return emv.hash == this.hash;
    }
  }

}
