package com.home.helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class ConfigProvider {

  static String getBaseUrl() {
    Properties properties = new Properties();
    String filePath = System.getProperty("user.dir") + "/src/main/resources/apiData.properties";
    try(InputStream inputStream = new FileInputStream(filePath)) {
      properties.load(inputStream);
    } catch (IOException io) {
      throw new IllegalArgumentException("Incorrect properties file path " + filePath);
    }
    return properties.getProperty("base.url");
  }

}
