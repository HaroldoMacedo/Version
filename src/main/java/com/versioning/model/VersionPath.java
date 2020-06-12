package com.versioning.model;

import java.util.ArrayList;
import java.util.List;

import com.versioning.map.EntityVersionMapper;

public class VersionPath {

//  private List<List<Mapper>> table = new ArrayList<List<Mapper>>();
  private static final int MAXVERSIONS = 100;
  private EntityVersionMapper[][] tableMap = new EntityVersionMapper[MAXVERSIONS][MAXVERSIONS];

  public void add(EntityVersionMapper mapper, int fromVersion, int toVersion){
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
  
  public List<EntityVersionMapper> getMappingPath(int fromVersion, int toVersion) {
    List<EntityVersionMapper> listMapper = new ArrayList<>();
    if ( ! findMapping(listMapper, fromVersion, toVersion))
      return null;
    
    return listMapper;
  }
  
  private boolean findMapping(List<EntityVersionMapper> listMapper, int fromVersion, int toVersion) {

    //  If a direct version mapping exist.
    if (tableMap[fromVersion][toVersion] != null) {
      listMapper.add(tableMap[fromVersion][toVersion]);
      return true;
    }

    // Find the mapping to the most distant version.
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
  
  private boolean notInRange(int version) {
    return (version <= 0 || version >= MAXVERSIONS);
  }
}
