package com.carservice.service;

import com.carservice.dao.PaymentDAO;
import com.carservice.model.Job;
import com.carservice.model.Payment;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Service class for managing payments. Provides methods to process and verify payments for jobs.
 */
public class PaymentService {
  private final PaymentDAO paymentDAO;
  private final JobService jobService;

  /** Constructs a PaymentService with default DAO and JobService instances. */
  public PaymentService() {
    this.paymentDAO = new PaymentDAO();
    this.jobService = new JobService();
  }

  /**
   * Processes a payment for a specific job.
   *
   * @param jobId the ID of the job for which the payment is being processed.
   * @param paymentMethod the method of payment (e.g., "CREDIT_CARD", "CASH").
   * @return the processed Payment object.
   * @throws ServiceException if the job is not found, incomplete, already paid, or an error occurs.
   */
  public Payment processJobPayment(Long jobId, String paymentMethod) throws ServiceException {
    try {
      // Get the job details
      Job job = jobService.findById(jobId);
      if (job == null) {
        throw new ServiceException("Job not found with ID: " + jobId);
      }

      // Verify the job is completed (has a dateOut)
      if (job.getDateOut() == null) {
        throw new ServiceException("Cannot process payment for incomplete job");
      }

      // Verify the job hasn't been paid already
      Payment existingPayment = paymentDAO.findByJob(jobId);
      if (existingPayment != null && "PAID".equals(existingPayment.getPaymentStatus())) {
        throw new ServiceException("Payment already processed for this job");
      }

      // Create new payment
      Payment payment = new Payment();
      payment.setJobId(jobId);
      payment.setAmount(job.getCost());
      payment.setPaymentDate(LocalDateTime.now());
      payment.setPaymentMethod(paymentMethod);
      payment.setPaymentStatus("PAID");

      // Save the payment
      Long paymentId = paymentDAO.save(payment);
      payment.setPaymentId(paymentId);

      return payment;
    } catch (SQLException e) {
      throw new ServiceException("Error processing payment: " + e.getMessage(), e);
    }
  }

  /**
   * Verifies if a payment has been made for a specific job.
   *
   * @param jobId the ID of the job to verify payment for.
   * @return true if the payment has been made and is marked as "PAID", false otherwise.
   * @throws ServiceException if an error occurs while verifying the payment.
   */
  public boolean verifyPayment(Long jobId) throws ServiceException {
    try {
      Payment payment = paymentDAO.findByJob(jobId);
      return payment != null && "PAID".equals(payment.getPaymentStatus());
    } catch (SQLException e) {
      throw new ServiceException("Error verifying payment: " + e.getMessage(), e);
    }
  }
}
