package com.carservice.model;

import java.time.LocalDateTime;

public class Activity {
  private Long activityId;
  private String type; // "CUSTOMER", "CAR", "JOB", "GARAGE"
  private String action; // "CREATE", "UPDATE", "DELETE"
  private String description;
  private LocalDateTime timestamp;
  private String userId;

  public Activity(
      Long activityId,
      String type,
      String action,
      String description,
      LocalDateTime timestamp,
      String userId) {
    this.activityId = activityId;
    this.type = type;
    this.action = action;
    this.description = description;
    this.timestamp = timestamp;
    this.userId = userId;
  }

  // Getters and setters
  public Long getActivityId() {
    return activityId;
  }

  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
