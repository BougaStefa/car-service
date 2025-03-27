package com.carservice.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T, ID> {
  T findById(ID id) throws SQLException;

  List<T> findAll() throws SQLException;

  ID save(T entity) throws SQLException;

  boolean update(T entity) throws SQLException;

  boolean delete(ID id) throws SQLException;
}
