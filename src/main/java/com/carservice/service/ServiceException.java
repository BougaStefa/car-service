package com.carservice.service;

/**
 * Custom exception class for service layer errors. Used to encapsulate exceptions and provide
 * meaningful error messages.
 */
public class ServiceException extends Exception {

  /**
   * Constructs a ServiceException with the specified detail message.
   *
   * @param message the detail message.
   */
  public ServiceException(String message) {
    super(message);
  }

  /**
   * Constructs a ServiceException with the specified detail message and cause.
   *
   * @param message the detail message.
   * @param cause the cause of the exception.
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
