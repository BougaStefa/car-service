package com.carservice.service;

import java.util.List;

public interface CrudService<T, ID> {
  T findById(ID id) throws ServiceException;

  List<T> findAll() throws ServiceException;

  ID save(T entity) throws ServiceException;

  boolean update(T entity) throws ServiceException;

  boolean delete(ID id) throws ServiceException;
}
