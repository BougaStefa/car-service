package com.carservice.model;

import java.time.LocalDateTime;

public class Job {
  private Long jobId;
  private Long garageId;
  private LocalDateTime dateIn;
  private LocalDateTime dateOut;
  private String regNo;
  private Double cost;

  // Default constructor
  public Job() {}

  // Constructor with fields
  public Job(
      Long jobId,
      Long garageId,
      LocalDateTime dateIn,
      LocalDateTime dateOut,
      String regNo,
      Double cost) {
    this.jobId = jobId;
    this.garageId = garageId;
    this.dateIn = dateIn;
    this.dateOut = dateOut;
    this.regNo = regNo;
    this.cost = cost;
  }

  // Getters and Setters
  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public Long getGarageId() {
    return garageId;
  }

  public void setGarageId(Long garageId) {
    this.garageId = garageId;
  }

  public LocalDateTime getDateIn() {
    return dateIn;
  }

  public void setDateIn(LocalDateTime dateIn) {
    this.dateIn = dateIn;
  }

  public LocalDateTime getDateOut() {
    return dateOut;
  }

  public void setDateOut(LocalDateTime dateOut) {
    this.dateOut = dateOut;
  }

  public String getRegNo() {
    return regNo;
  }

  public void setRegNo(String regNo) {
    this.regNo = regNo;
  }

  public Double getCost() {
    return cost;
  }

  public void setCost(Double cost) {
    this.cost = cost;
  }
}
