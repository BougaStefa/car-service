package com.carservice.service;

import com.carservice.dao.CustomerDAO;
import com.carservice.model.Customer;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing customers. Provides CRUD operations and additional methods for
 * customer-related functionality.
 */
public class CustomerService implements CrudService<Customer, Long> {
  private final CustomerDAO customerDAO;
  private final ActivityService activityService;

  /** Constructs a CustomerService with default DAO and ActivityService instances. */
  public CustomerService() {
    this.customerDAO = new CustomerDAO();
    this.activityService = new ActivityService();
  }

  /**
   * Finds a customer by their ID.
   *
   * @param id the ID of the customer.
   * @return the customer with the specified ID.
   * @throws ServiceException if the customer is not found or an error occurs.
   */
  @Override
  public Customer findById(Long id) throws ServiceException {
    try {
      Customer customer = customerDAO.findById(id);
      if (customer == null) {
        throw new ServiceException("Customer not found with ID: " + id);
      }
      return customer;
    } catch (SQLException e) {
      throw new ServiceException("Error finding customer with ID: " + id, e);
    }
  }

  /**
   * Finds customers by their surname.
   *
   * @param surname the surname of the customers.
   * @return a list of customers with the specified surname.
   * @throws ServiceException if an error occurs while retrieving customers.
   */
  public List<Customer> findBySurname(String surname) throws ServiceException {
    try {
      return customerDAO.findBySurname(surname);
    } catch (SQLException e) {
      throw new ServiceException("Error finding customers with surname: " + surname, e);
    }
  }

  /**
   * Retrieves all customers.
   *
   * @return a list of all customers.
   * @throws ServiceException if an error occurs while retrieving customers.
   */
  @Override
  public List<Customer> findAll() throws ServiceException {
    try {
      return customerDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all customers", e);
    }
  }

  /**
   * Saves a new customer.
   *
   * @param customer the customer to save.
   * @return the ID of the saved customer.
   * @throws ServiceException if validation fails or an error occurs while saving.
   */
  @Override
  public Long save(Customer customer) throws ServiceException {
    try {
      validateCustomer(customer);
      Long id = customerDAO.save(customer);
      activityService.logActivity(
          "CUSTOMER",
          "CREATE",
          "New customer created: " + customer.getForename() + " " + customer.getSurname(),
          "BougaStefa");
      return id;
    } catch (SQLException e) {
      throw new ServiceException("Error saving customer", e);
    }
  }

  /**
   * Updates an existing customer.
   *
   * @param customer the customer to update.
   * @return true if the customer was updated successfully, false otherwise.
   * @throws ServiceException if validation fails or an error occurs while updating.
   */
  @Override
  public boolean update(Customer customer) throws ServiceException {
    try {
      validateCustomer(customer);
      boolean updated = customerDAO.update(customer);
      if (updated) {
        activityService.logActivity(
            "CUSTOMER",
            "UPDATE",
            "Customer updated: " + customer.getForename() + " " + customer.getSurname(),
            "BougaStefa");
      }
      return updated;
    } catch (SQLException e) {
      throw new ServiceException("Error updating customer", e);
    }
  }

  /**
   * Deletes a customer by their ID.
   *
   * @param customerId the ID of the customer to delete.
   * @return true if the customer was deleted successfully, false otherwise.
   * @throws ServiceException if an error occurs while deleting the customer.
   */
  @Override
  public boolean delete(Long customerId) throws ServiceException {
    try {
      boolean deleted = customerDAO.delete(customerId);
      if (deleted) {
        activityService.logActivity(
            "CUSTOMER", "DELETE", "Customer deleted with ID: " + customerId, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting customer", e);
    }
  }

  /**
   * Validates the customer object to ensure it meets the required criteria.
   *
   * @param customer the customer to validate.
   * @throws ServiceException if validation fails.
   */
  private void validateCustomer(Customer customer) throws ServiceException {
    if (customer.getForename() == null || customer.getForename().trim().isEmpty()) {
      throw new ServiceException("Customer forename cannot be empty");
    }
    if (customer.getSurname() == null || customer.getSurname().trim().isEmpty()) {
      throw new ServiceException("Customer surname cannot be empty");
    }
    if (customer.getPhoneNo() == null || !customer.getPhoneNo().matches("\\d{10}")) {
      throw new ServiceException("Invalid phone number format");
    }
    if (customer.getPostCode() == null || !customer.getPostCode().matches("[A-Z0-9]{5,7}")) {
      throw new ServiceException("Invalid post code format");
    }
  }
}
