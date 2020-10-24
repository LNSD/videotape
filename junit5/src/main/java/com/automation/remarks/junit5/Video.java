package com.automation.remarks.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created by sergey on 12.02.17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@ExtendWith(VideoExtension.class)
@com.automation.remarks.video.annotations.Video
public @interface Video {
  String name() default "";
}
