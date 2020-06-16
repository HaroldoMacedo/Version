package com.versioning;

import java.util.ArrayList;
import java.util.List;

import com.versioning.entity.Entity;
import com.versioning.map.EntityVersionMapper;

/**
 * Class used by {@link MapEntityVersion} to implement the mapping transformation.<br>
 * This class stores all registered version mappers for an entity name.
 * Method {@link #add(EntityVersionMapper, int, int) add()} stores each {@link com.versioning.map.EntityVersionMapper EntityVersionMapper}.<br>
 * It also generate the list of {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} that
 * can be executed to transform an {@link com.versioning.Entity Entity} from a version to another one.
 * 
 * @author Haroldo Macêdo
 *
 */
class VersionPath {

  private static final int MAXVERSIONS = 100;
  /**
   * Stores all {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} of an entity.
   */
  private EntityVersionMapper[][] tableMap = new EntityVersionMapper[MAXVERSIONS][MAXVERSIONS];

  /**
   * Stores one {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} that maps entities from a version to another one.<br>
   * 
   * @param mapper - The mapper class
   * @param fromVersion - The {@link com.versioning.Entity Entity} initial version. 
   * @param toVersion - {@link com.versioning.Entity Entity} final version.
   */
  void add(EntityVersionMapper mapper, int fromVersion, int toVersion){
    if (notInRange(fromVersion) || notInRange(toVersion)) {
      System.out.println("Versioning " + fromVersion + " to version " + toVersion + " out of range. Version number range from 1 to 99.");
      return;
    }
    
    if (tableMap[fromVersion][toVersion] != null) {
      System.out.println("Mapping from version " + fromVersion + " to version " + toVersion + " already exists.");
      return;
    }
    
    tableMap[fromVersion][toVersion] = mapper;
  }
  
  /**
   * Create the list of {@link com.versioning.map.EntityVersionMapper EntityVersionMapper} that
   * can be executed to transform an {@link com.versioning.Entity Entity} from a version to another one.
   * @param fromVersion
   * @param toVersion
   * @return
   */
  List<EntityVersionMapper> getMappingPath(int fromVersion, int toVersion) {
    List<EntityVersionMapper> listMapper = new ArrayList<>();
    if ( ! findMapping(listMapper, fromVersion, toVersion))
      return null;
    
    return listMapper;
  }
  
  /**
   * Recursive called method to create the list with the order of listMapper's call.<br>
   * The returned list can be for a request, when parameters {@code fromVersion} < {@code toVersion}; or
   * for a response, when parameters {@code fromVersion} > {@code toVersion}.
   * 
   * @param listMapper - List of {@link EntityVersionMapper} to be executed to map from version {@code fromVersion} to {@code toVersion} 
   * @param fromVersion - {@link Entity} input version.
   * @param toVersion - {@link Entity} output version.
   * @return <ul><li>true indicates a mapping was found.</li>
   * <li>false indicates a mapping was not found.</li></ul>
   */
  private boolean findMapping(List<EntityVersionMapper> listMapper, int fromVersion, int toVersion) {

    //  If a direct version mapping exist.
    if (tableMap[fromVersion][toVersion] != null) {
      listMapper.add(tableMap[fromVersion][toVersion]);
      return true;
    }

    // Find the mapping to the most distant version first.
    int nextVersion = (fromVersion < toVersion ? -1 : 1);
    for (int tvThis = toVersion + nextVersion; (nextVersion == -1 ? tvThis > fromVersion : tvThis < fromVersion); tvThis += nextVersion) {
      if (tableMap[fromVersion][tvThis] != null) {
        // Find any versioning path from the toVersion highest.
        if (findMapping(listMapper, tvThis, toVersion)) {
          listMapper.add(0, tableMap[fromVersion][tvThis]);

          return true;
        }
      }
    }
    
    return false;
  }
  
  /**
   * Validate wether the version is within a valid range.
   * 
   * @param version Number of version.
   * @return true if is within range.
   */
  private boolean notInRange(int version) {
    return (version <= 0 || version >= MAXVERSIONS);
  }
}
