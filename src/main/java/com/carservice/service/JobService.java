package com.carservice.service;

import com.carservice.dao.JobDAO;
import com.carservice.model.Job;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class JobService implements CrudService<Job, Long> {
  private final JobDAO jobDAO;
  private final ActivityService activityService;

  public JobService() {
    this.jobDAO = new JobDAO();
    this.activityService = new ActivityService();
  }

  @Override
  public Job findById(Long id) throws ServiceException {
    try {
      Job job = jobDAO.findById(id);
      if (job == null) {
        throw new ServiceException("Job not found with ID: " + id);
      }
      return job;
    } catch (SQLException e) {
      throw new ServiceException("Error finding job with ID: " + id, e);
    }
  }

  public List<Job> findByCar(String regNo) throws ServiceException {
    try {
      return jobDAO.findByCar(regNo);
    } catch (SQLException e) {
      throw new ServiceException("Error finding jobs for car: " + regNo, e);
    }
  }

  public List<Job> findByGarage(Long garageId) throws ServiceException {
    try {
      return jobDAO.findByGarage(garageId);
    } catch (SQLException e) {
      throw new ServiceException("Error finding jobs for garage: " + garageId, e);
    }
  }

  @Override
  public List<Job> findAll() throws ServiceException {
    try {
      return jobDAO.findAll();
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving all jobs", e);
    }
  }

  @Override
  public Long save(Job job) throws ServiceException {
    try {
      validateJob(job);
      Long id = jobDAO.save(job);
      // Log the activity
      activityService.logActivity(
          "JOB", "CREATE", "New job created for car: " + job.getRegNo(), "BougaStefa");
      return id;
    } catch (SQLException e) {
      throw new ServiceException("Error saving job", e);
    }
  }

  @Override
  public boolean update(Job job) throws ServiceException {
    try {
      validateJob(job);
      boolean updated = jobDAO.update(job);
      if (updated) {
        // Log the activity
        activityService.logActivity(
            "JOB", "UPDATE", "Job updated for car: " + job.getRegNo(), "BougaStefa");
      }
      return updated;
    } catch (SQLException e) {
      throw new ServiceException("Error updating job", e);
    }
  }

  @Override
  public boolean delete(Long jobId) throws ServiceException {
    try {
      boolean deleted = jobDAO.delete(jobId);
      if (deleted) {
        // Log the activity
        activityService.logActivity("JOB", "DELETE", "Job deleted with ID: " + jobId, "BougaStefa");
      }
      return deleted;
    } catch (SQLException e) {
      throw new ServiceException("Error deleting job", e);
    }
  }

  private void validateJob(Job job) throws ServiceException {
    if (job.getDateIn() == null) {
      throw new ServiceException("Job date in cannot be null");
    }
    if (job.getDateOut() != null && job.getDateOut().isBefore(job.getDateIn())) {
      throw new ServiceException("Job date out cannot be before date in");
    }
    if (job.getCost() != null && job.getCost() < 0) {
      throw new ServiceException("Job cost cannot be negative");
    }
    LocalDateTime now = LocalDateTime.now();
    if (job.getDateIn().isAfter(now)) {
      throw new ServiceException("Job date in cannot be in the future");
    }
  }
}
