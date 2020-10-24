package com.automation.remarks.video.enums;

import java.lang.reflect.Method;

/**
 * Created by sepi on 19.07.16.
 */
public enum RecorderType {
  MONTE, FFMPEG;

  public static class Converter implements org.aeonbits.owner.Converter<RecorderType> {
    @Override
    public RecorderType convert(Method method, String input) {
      return valueOf(input.toUpperCase());
    }
  }
}
