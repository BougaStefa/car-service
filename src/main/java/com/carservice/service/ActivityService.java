package com.carservice.service;

import com.carservice.dao.ActivityDAO;
import com.carservice.model.Activity;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityService {
  private final ActivityDAO activityDAO;
  private static final int DEFAULT_RECENT_LIMIT = 20;

  public ActivityService() {
    this.activityDAO = new ActivityDAO();
  }

  public void logActivity(String type, String action, String description, String userId) {
    try {
      Activity activity =
          new Activity(null, type, action, description, LocalDateTime.now(), userId);
      activityDAO.save(activity);
    } catch (SQLException e) {
      // Log error but don't throw - we don't want activity logging to break main functionality
      e.printStackTrace();
    }
  }

  public List<Activity> getRecentActivity() throws ServiceException {
    try {
      return activityDAO.findRecent(DEFAULT_RECENT_LIMIT);
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving recent activity", e);
    }
  }
}
