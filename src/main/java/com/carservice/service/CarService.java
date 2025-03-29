package com.carservice.service;

import com.carservice.dao.CarDAO;
import com.carservice.model.Car;
import java.sql.SQLException;
import java.util.List;

public class CarService implements CrudService<Car, String> {
  private final CarDAO carDAO;
  private final ActivityService activityService;

  public CarService() {
    this.carDAO = new CarDAO();
    this.activityService = new ActivityService();
  }

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

  public List<Car> findByCustomer(Long customerId) throws ServiceException {
    try {
      return carDAO.findByCustomer(customerId);
    } catch (SQLException e) {
      throw new ServiceException("Error finding cars for customer: " + customerId, e);
    }
  }

  @Override
  public List<Car> findAll() throws ServiceException {
    try {
      return carDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all cars", e);
    }
  }

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
