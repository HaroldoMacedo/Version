package com.versioning.test;

import com.versioning.entity.Entity;
import com.versioning.model.ExecuteVersion;
import com.versioning.model.MethodVersionExecuter;

public class GetEmployeePerIdMock_V2 implements MethodVersionExecuter {

  @Override
  @ExecuteVersion(inputEntity="Id", inputVersion=2, outputEntity="Employee", outputVersion=2)
  public Entity execute(Entity entity) {
    IdV2 id = (IdV2)entity;
    EmployeeV2 employee = new EmployeeV2(id.getId());
    employee.setFirstName("Haroldo");
    employee.setLastName("Macedo");
    employee.setEmail("email@email.com");
    employee.setPhone("123457689");

    return employee;
  }
  
//  //  TODO: Remove main() method.
//  public static void main(String[] args) {
//    System.out.println("Starting...");
//    GetEmployeePerIdMock_V1 runGetEmployee = new GetEmployeePerIdMock_V1();
//
//    System.out.println("Calling map");
//    runGetEmployee.execute(new IdV1(1));
//    System.out.println("Mapped!\n\n");
//  }
//
}
