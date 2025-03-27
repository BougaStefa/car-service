package com.carservice.service;

import com.carservice.dao.GarageDAO;
import com.carservice.model.Garage;
import java.sql.SQLException;
import java.util.List;

public class GarageService implements CrudService<Garage, Long> {
  private final GarageDAO garageDAO;

  public GarageService() {
    this.garageDAO = new GarageDAO();
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
      return garageDAO.save(garage);
    } catch (SQLException e) {
      throw new ServiceException("Error saving garage", e);
    }
  }

  @Override
  public boolean update(Garage garage) throws ServiceException {
    try {
      validateGarage(garage);
      return garageDAO.update(garage);
    } catch (SQLException e) {
      throw new ServiceException("Error updating garage with ID: " + garage.getGarageId(), e);
    }
  }

  @Override
  public boolean delete(Long id) throws ServiceException {
    try {
      return garageDAO.delete(id);
    } catch (SQLException e) {
      throw new ServiceException("Error deleting garage with ID: " + id, e);
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
