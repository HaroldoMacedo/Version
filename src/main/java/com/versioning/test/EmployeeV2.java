package com.versioning.test;

import java.util.ArrayList;
import java.util.List;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;

@VersionEntity(name="Employee", version=2)
public class EmployeeV2 implements Entity {
  class RelationShip {
    private String manager;
    private List<String> teamMember = new ArrayList<>();
  }

  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private int status;
  private String  salaryStatement;

  private RelationShip relationShip = new RelationShip();
  
  public EmployeeV2() {}
  
  public EmployeeV2(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getManager() {
    return relationShip.manager;
  }

  public void setManager(String manager) {
    relationShip.manager = manager;
  }

  public List<String> getTeamMember() {
    return relationShip.teamMember;
  }

  public void addTeamMember(String teamMember) {
    relationShip.teamMember.add(teamMember);
  }

  public String getSalaryStatement() {
    return salaryStatement;
  }

  public void setSalaryStatement(String salaryStatement) {
    this.salaryStatement = salaryStatement;
  }

}
