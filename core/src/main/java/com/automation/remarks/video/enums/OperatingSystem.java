package com.automation.remarks.video.enums;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;


public enum OperatingSystem {
  WINDOWS, MAC, LINUX;

  public static String getOS() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return WINDOWS.name().toLowerCase();
    }
    if (SystemUtils.IS_OS_MAC) {
      return MAC.name().toLowerCase();
    }

    if (SystemUtils.IS_OS_LINUX) {
      return LINUX.name().toLowerCase();
    }

    String os = System.getProperty("os.name", "unknown");
    throw new NotImplementedException(String.format("OS '%s' not supported", os));
  }
}
