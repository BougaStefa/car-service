package com.carservice.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
  private String regNo;
  private String make;
  private String model;
  private int year;
  private Long customerId;
  private List<Job> jobs;

  // Default constructor
  public Car() {
    this.jobs = new ArrayList<>();
  }

  // Constructor with fields
  public Car(String regNo, String make, String model, int year, Long customerId) {
    this.regNo = regNo;
    this.make = make;
    this.model = model;
    this.year = year;
    this.customerId = customerId;
    this.jobs = new ArrayList<>();
  }

  // Getters and Setters
  public String getRegNo() {
    return regNo;
  }

  public void setRegNo(String regNo) {
    this.regNo = regNo;
  }

  public String getMake() {
    return make;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public List<Job> getJobs() {
    return jobs;
  }

  public void setJobs(List<Job> jobs) {
    this.jobs = jobs;
  }

  // Helper method to add a job
  public void addJob(Job job) {
    jobs.add(job);
  }
}
