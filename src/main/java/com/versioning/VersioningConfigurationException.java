package com.versioning;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VersioningConfigurationException extends Exception {

  private static final long serialVersionUID = -5153980222590000587L;
  private static Logger logger = LogManager.getFormatterLogger(VersioningConfigurationException.class.getName());

  
  public VersioningConfigurationException(String message) {
    super(message);
    logger.debug(message);
  }
}
