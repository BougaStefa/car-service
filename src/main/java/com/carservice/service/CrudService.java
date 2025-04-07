package com.carservice.service;

import java.util.List;

/**
 * Generic interface for CRUD (Create, Read, Update, Delete) operations.
 *
 * @param <T> the type of the entity.
 * @param <ID> the type of the entity's identifier.
 */
public interface CrudService<T, ID> {

  /**
   * Finds an entity by its identifier.
   *
   * @param id the identifier of the entity.
   * @return the entity with the specified identifier.
   * @throws ServiceException if the entity is not found or an error occurs.
   */
  T findById(ID id) throws ServiceException;

  /**
   * Retrieves all entities.
   *
   * @return a list of all entities.
   * @throws ServiceException if an error occurs while retrieving entities.
   */
  List<T> findAll() throws ServiceException;

  /**
   * Saves a new entity.
   *
   * @param entity the entity to save.
   * @return the identifier of the saved entity.
   * @throws ServiceException if an error occurs while saving the entity.
   */
  ID save(T entity) throws ServiceException;

  /**
   * Updates an existing entity.
   *
   * @param entity the entity to update.
   * @return true if the entity was updated successfully, false otherwise.
   * @throws ServiceException if an error occurs while updating the entity.
   */
  boolean update(T entity) throws ServiceException;

  /**
   * Deletes an entity by its identifier.
   *
   * @param id the identifier of the entity to delete.
   * @return true if the entity was deleted successfully, false otherwise.
   * @throws ServiceException if an error occurs while deleting the entity.
   */
  boolean delete(ID id) throws ServiceException;
}
