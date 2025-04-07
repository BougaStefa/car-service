package com.carservice.service;

import com.carservice.dao.GarageDAO;
import com.carservice.model.Garage;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing garages. Provides CRUD operations and additional methods for
 * garage-related functionality.
 */
public class GarageService implements CrudService<Garage, Long> {
  private final GarageDAO garageDAO;
  private final ActivityService activityService;

  /** Constructs a GarageService with default DAO and ActivityService instances. */
  public GarageService() {
    this.garageDAO = new GarageDAO();
    this.activityService = new ActivityService();
  }

  /**
   * Finds a garage by its ID.
   *
   * @param id the ID of the garage.
   * @return the garage with the specified ID.
   * @throws ServiceException if the garage is not found or an error occurs.
   */
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

  /**
   * Retrieves all garages.
   *
   * @return a list of all garages.
   * @throws ServiceException if an error occurs while retrieving garages.
   */
  @Override
  public List<Garage> findAll() throws ServiceException {
    try {
      return garageDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all garages", e);
    }
  }

  /**
   * Saves a new garage.
   *
   * @param garage the garage to save.
   * @return the ID of the saved garage.
   * @throws ServiceException if validation fails or an error occurs while saving.
   */
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

  /**
   * Updates an existing garage.
   *
   * @param garage the garage to update.
   * @return true if the garage was updated successfully, false otherwise.
   * @throws ServiceException if validation fails or an error occurs while updating.
   */
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

  /**
   * Deletes a garage by its ID.
   *
   * @param garageId the ID of the garage to delete.
   * @return true if the garage was deleted successfully, false otherwise.
   * @throws ServiceException if an error occurs while deleting the garage.
   */
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

  /**
   * Finds garages by their name.
   *
   * @param name the name of the garages.
   * @return a list of garages with names containing the specified string.
   * @throws ServiceException if an error occurs while retrieving garages.
   */
  public List<Garage> findByName(String name) throws ServiceException {
    try {
      return garageDAO.findByName(name);
    } catch (SQLException e) {
      throw new ServiceException("Error finding garages with name containing: " + name, e);
    }
  }

  /**
   * Validates the garage object to ensure it meets the required criteria.
   *
   * @param garage the garage to validate.
   * @throws ServiceException if validation fails.
   */
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
