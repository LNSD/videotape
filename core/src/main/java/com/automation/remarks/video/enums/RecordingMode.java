package com.automation.remarks.video.enums;

import java.lang.reflect.Method;

/**
 * Created by sergey on 5/7/16.
 */
public enum RecordingMode {
  ALL, ANNOTATED;

  public static class Converter implements org.aeonbits.owner.Converter<RecordingMode> {
    @Override
    public RecordingMode convert(Method method, String input) {
      return valueOf(input.toUpperCase());
    }
  }
}
