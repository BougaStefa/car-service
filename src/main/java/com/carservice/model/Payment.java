package com.carservice.model;

import java.time.LocalDateTime;

public class Payment {
  private Long paymentId;
  private Long jobId;
  private Double amount;
  private LocalDateTime paymentDate;
  private String paymentMethod;
  private String paymentStatus;

  // Default constructor
  public Payment() {}

  // Constructor with fields
  public Payment(
      Long paymentId,
      Long jobId,
      Double amount,
      LocalDateTime paymentDate,
      String paymentMethod,
      String paymentStatus) {
    this.paymentId = paymentId;
    this.jobId = jobId;
    this.amount = amount;
    this.paymentDate = paymentDate;
    this.paymentMethod = paymentMethod;
    this.paymentStatus = paymentStatus;
  }

  // Getters and Setters
  public Long getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Long paymentId) {
    this.paymentId = paymentId;
  }

  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }
}
