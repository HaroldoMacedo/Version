package com.versioning.test;


import com.versioning.entity.Entity;
import com.versioning.map.EntityVersionMapper;
import com.versioning.map.VersionMapper;
import com.versioning.model.VersioningConfigurationException;

public class EmployeeMap_V2 extends EntityVersionMapper {

  public EmployeeMap_V2() throws VersioningConfigurationException {
  }

  /**
   * Map V1 to V2.
   */
  @Override
  @VersionMapper(entityName = "Employee", fromVersion = 1, toVersion = 2)
  public Entity map(Entity entity) {
    EmployeeV1 employeeFrom = (EmployeeV1) entity;
    EmployeeV2 employeeTo = new EmployeeV2();

    employeeTo.setId(employeeFrom.getId() + "");
    employeeTo.setFirstName(firstName(employeeFrom.getFullName()));
    employeeTo.setLastName(lastName(employeeFrom.getFullName()));

    return employeeTo;
  }


  /**
   * Implements the approved way to transform a full name into a first name.
   * 
   * @param fullName
   * @return
   */
  private String firstName(String fullName) {
    return fullName.substring(0, 5);
  }

  /**
   * Implements the approved way to transform a full name into a last name.
   * 
   * @param fullName
   * @return
   */
  private String lastName(String fullName) {
    return fullName.substring(5);
  }

  //  TODO: Remove main() method.
  public static void main(String[] args) throws Exception{
    System.out.println("Starting...");
    EmployeeMap_V2 employeeMap = new EmployeeMap_V2();

    System.out.println("Calling map");
    employeeMap.map(new EmployeeV1(1));
    System.out.println("Mapped!\n\n");
  }
}
