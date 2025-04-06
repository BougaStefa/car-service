package com.carservice.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic interface for basic CRUD (Create, Read, Update, Delete) operations.
 *
 * @param <T> the type of entity being managed
 * @param <ID> the type of the entity's identifier
 */
public interface CrudDAO<T, ID> {
  /**
   * Retrieves an entity by its ID.
   *
   * @param id the identifier of the entity to retrieve
   * @return the entity with the specified ID, or null if not found
   * @throws SQLException if a database access error occurs
   */
  T findById(ID id) throws SQLException;

  /**
   * Retrieves all entities.
   *
   * @return a list of all entities
   * @throws SQLException if a database access error occurs
   */
  List<T> findAll() throws SQLException;

  /**
   * Saves a new entity.
   *
   * @param entity the entity to save
   * @return the identifier of the saved entity
   * @throws SQLException if a database access error occurs
   */
  ID save(T entity) throws SQLException;

  /**
   * Updates an existing entity.
   *
   * @param entity the entity to update
   * @return true if the entity was updated successfully, false otherwise
   * @throws SQLException if a database access error occurs
   */
  boolean update(T entity) throws SQLException;

  /**
   * Deletes an entity by its ID.
   *
   * @param id the identifier of the entity to delete
   * @return true if the entity was deleted successfully, false otherwise
   * @throws SQLException if a database access error occurs
   */
  boolean delete(ID id) throws SQLException;
}
