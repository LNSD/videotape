package com.automation.remarks.testng.utils;

import com.automation.remarks.video.annotations.Video;
import java.lang.annotation.Annotation;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * Created by Serhii_Pirohov on 13.05.2016.
 */
public class MethodUtils {

  public static Video getVideoAnnotation(ITestResult result) {
    ITestNGMethod method = result.getMethod();
    Annotation[] declaredAnnotations = method.getConstructorOrMethod().getMethod().getDeclaredAnnotations();
    for (Annotation declaredAnnotation : declaredAnnotations) {
      if (declaredAnnotation.annotationType().equals(Video.class)) {
        return (Video) declaredAnnotation;
      }
    }
    return null;
  }

}
