package com.automation.remarks.video.enums;

import java.lang.reflect.Method;

/**
 * Created by sergey on 18.08.16.
 */
public enum VideoSaveMode {
    FAILED_ONLY, ALL;

    public static class Converter implements org.aeonbits.owner.Converter<VideoSaveMode> {
        @Override
        public VideoSaveMode convert(Method method, String input) {
            return valueOf(input.toUpperCase());
        }
    }
}
