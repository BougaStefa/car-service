package com.bougastefa.app;

public class Job {
  private String jobID;
  private String carRegNo;
  private String garageID;
  private String dateIn;
  private String dateOut;
  private double cost;

  public Job(String jobID, String carRegNo, String garageID, String dateIn, String dateOut, double cost) {
    this.jobID = jobID;
    this.carRegNo = carRegNo;
    this.garageID = garageID;
    this.dateIn = dateIn;
    this.dateOut = dateOut;
    this.cost = cost;
  }

  public void setJobID(String jobID) {
    this.jobID = jobID;
  }

  public void setCarRegNo(String carRegNo) {
    this.carRegNo = carRegNo;
  }

  public void setGarageID(String garageID) {
    this.garageID = garageID;
  }

  public void setDateIn(String dateIn) {
    this.dateIn = dateIn;
  }

  public void setdateOut(String dateOut) {
    this.dateOut = dateOut;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public String getJobID() {
    return jobID;
  }

  public String getCarRegNo() {
    return carRegNo;
  }

  public String getGarageID() {
    return garageID;
  }

  public String getDateIn() {
    return dateIn;
  }

  public String getDateOut() {
    return dateOut;
  }

  public double getCost() {
    return cost;
  }

  public void addJob() {
    // add job to the database
  }

  public void editJob() {
    // edit job in the database
  }

  public void deleteJob() {
    // delete job from the database
  }

  public void searchJob() {
    // search for job in the database
  }
}
