package com.carservice.service;

import com.carservice.dao.CarDAO;
import com.carservice.model.Car;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing cars. Provides CRUD operations and additional methods for car-related
 * functionality.
 */
public class CarService implements CrudService<Car, String> {
  private final CarDAO carDAO;
  private final ActivityService activityService;

  /** Constructs a CarService with default DAO and ActivityService instances. */
  public CarService() {
    this.carDAO = new CarDAO();
    this.activityService = new ActivityService();
  }

  /**
   * Finds a car by its registration number.
   *
   * @param regNo the registration number of the car.
   * @return the car with the specified registration number.
   * @throws ServiceException if the car is not found or an error occurs.
   */
  @Override
  public Car findById(String regNo) throws ServiceException {
    try {
      Car car = carDAO.findById(regNo);
      if (car == null) {
        throw new ServiceException("Car not found with registration number: " + regNo);
      }
      return car;
    } catch (SQLException e) {
      throw new ServiceException("Error finding car with registration number: " + regNo, e);
    }
  }

  /**
   * Finds cars associated with a specific customer.
   *
   * @param customerId the ID of the customer.
   * @return a list of cars associated with the customer.
   * @throws ServiceException if an error occurs while retrieving cars.
   */
  public List<Car> findByCustomer(Long customerId) throws ServiceException {
    try {
      return carDAO.findByCustomer(customerId);
    } catch (SQLException e) {
      throw new ServiceException("Error finding cars for customer: " + customerId, e);
    }
  }

  /**
   * Retrieves all cars.
   *
   * @return a list of all cars.
   * @throws ServiceException if an error occurs while retrieving cars.
   */
  @Override
  public List<Car> findAll() throws ServiceException {
    try {
      return carDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all cars", e);
    }
  }

  /**
   * Saves a new car.
   *
   * @param car the car to save.
   * @return the ID of the saved car.
   * @throws ServiceException if validation fails or an error occurs while saving.
   */
  @Override
  public String save(Car car) throws ServiceException {
    try {
      validateCar(car);
      String id = carDAO.save(car);
      // Log the activity
      activityService.logActivity(
          "CAR", "CREATE", "New car created: " + car.getRegNo(), "BougaStefa");
      return id;
    } catch (SQLException e) {
      throw new ServiceException("Error saving car", e);
    }
  }

  /**
   * Updates an existing car.
   *
   * @param car the car to update.
   * @return true if the car was updated successfully, false otherwise.
   * @throws ServiceException if validation fails or an error occurs while updating.
   */
  @Override
  public boolean update(Car car) throws ServiceException {
    try {
      validateCar(car);
      boolean updated = carDAO.update(car);
      if (updated) {
        // Log the activity
        activityService.logActivity(
            "CAR", "UPDATE", "Car updated: " + car.getRegNo(), "BougaStefa");
      }
      return updated;
    } catch (SQLException e) {
      throw new ServiceException("Error updating car", e);
    }
  }

  /**
   * Deletes a car by its registration number.
   *
   * @param regNo the registration number of the car to delete.
   * @return true if the car was deleted successfully, false otherwise.
   * @throws ServiceException if an error occurs while deleting the car.
   */
  @Override
  public boolean delete(String regNo) throws ServiceException {
    try {
      boolean deleted = carDAO.delete(regNo);
      if (deleted) {
        // Log the activity
        activityService.logActivity(
            "CAR", "DELETE", "Car deleted with RegNo: " + regNo, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting car", e);
    }
  }

  /**
   * Validates the car object to ensure it meets the required criteria.
   *
   * @param car the car to validate.
   * @throws ServiceException if validation fails.
   */
  private void validateCar(Car car) throws ServiceException {
    if (car.getRegNo() == null || !car.getRegNo().matches("[A-Z0-9]{1,7}")) {
      throw new ServiceException("Invalid registration number format");
    }
    if (car.getMake() == null || car.getMake().trim().isEmpty()) {
      throw new ServiceException("Car make cannot be empty");
    }
    if (car.getModel() == null || car.getModel().trim().isEmpty()) {
      throw new ServiceException("Car model cannot be empty");
    }
    if (car.getYear() < 1900 || car.getYear() > 2025) {
      throw new ServiceException("Invalid car year");
    }
  }
}
