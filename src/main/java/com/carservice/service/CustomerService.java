package com.carservice.service;

import com.carservice.dao.CustomerDAO;
import com.carservice.model.Customer;
import java.sql.SQLException;
import java.util.List;

public class CustomerService implements CrudService<Customer, Long> {
  private final CustomerDAO customerDAO;
  private final ActivityService activityService;

  public CustomerService() {
    this.customerDAO = new CustomerDAO();
    this.activityService = new ActivityService();
  }

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

  public List<Customer> findBySurname(String surname) throws ServiceException {
    try {
      return customerDAO.findBySurname(surname);
    } catch (SQLException e) {
      throw new ServiceException("Error finding customers with surname: " + surname, e);
    }
  }

  @Override
  public List<Customer> findAll() throws ServiceException {
    try {
      return customerDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all customers", e);
    }
  }

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

  @Override
  public boolean update(Customer customer) throws ServiceException {
    try {
      validateCustomer(customer);
      boolean updated = customerDAO.update(customer);
      if (updated) {
        // Log the activity
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

  @Override
  public boolean delete(Long customerId) throws ServiceException {
    try {
      boolean deleted = customerDAO.delete(customerId);
      if (deleted) {
        // Log the activity
        activityService.logActivity(
            "CUSTOMER", "DELETE", "Customer deleted with ID: " + customerId, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting customer", e);
    }
  }

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
