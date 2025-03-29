package com.carservice.service;

import com.carservice.dao.GarageDAO;
import com.carservice.model.Garage;
import java.sql.SQLException;
import java.util.List;

public class GarageService implements CrudService<Garage, Long> {
  private final GarageDAO garageDAO;
  private final ActivityService activityService;

  public GarageService() {
    this.garageDAO = new GarageDAO();
    this.activityService = new ActivityService();
  }

  @Override
  public Garage findById(Long id) throws ServiceException {
    try {
      Garage garage = garageDAO.findById(id);
      if (garage == null) {
        throw new ServiceException("Garage not found with ID: " + id);
      }
      return garage;
    } catch (SQLException e) {
      throw new ServiceException("Error finding garage with ID: " + id, e);
    }
  }

  @Override
  public List<Garage> findAll() throws ServiceException {
    try {
      return garageDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all garages", e);
    }
  }

  @Override
  public Long save(Garage garage) throws ServiceException {
    try {
      validateGarage(garage);
      Long id = garageDAO.save(garage);
      // Log the activity
      activityService.logActivity(
          "GARAGE", "CREATE", "New garage created: " + garage.getGarageName(), "BougaStefa");
      return id;
    } catch (SQLException e) {
      throw new ServiceException("Error saving garage", e);
    }
  }

  @Override
  public boolean update(Garage garage) throws ServiceException {
    try {
      validateGarage(garage);
      boolean updated = garageDAO.update(garage);
      if (updated) {
        // Log the activity
        activityService.logActivity(
            "GARAGE", "UPDATE", "Garage updated: " + garage.getGarageName(), "BougaStefa");
      }
      return updated;
    } catch (SQLException e) {
      throw new ServiceException("Error updating garage", e);
    }
  }

  @Override
  public boolean delete(Long garageId) throws ServiceException {
    try {
      boolean deleted = garageDAO.delete(garageId);
      if (deleted) {
        // Log the activity
        activityService.logActivity(
            "GARAGE", "DELETE", "Garage deleted with ID: " + garageId, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting garage", e);
    }
  }

  public List<Garage> findByName(String name) throws ServiceException {
    try {
      return garageDAO.findByName(name);
    } catch (SQLException e) {
      throw new ServiceException("Error finding garages with name containing: " + name, e);
    }
  }

  private void validateGarage(Garage garage) throws ServiceException {
    if (garage.getGarageName() == null || garage.getGarageName().trim().isEmpty()) {
      throw new ServiceException("Garage name cannot be empty");
    }
    if (garage.getPhoneNo() == null || !garage.getPhoneNo().matches("\\d{10}")) {
      throw new ServiceException("Invalid phone number format");
    }
    if (garage.getPostCode() == null || !garage.getPostCode().matches("[A-Z0-9]{5,7}")) {
      throw new ServiceException("Invalid post code format");
    }
  }
}
