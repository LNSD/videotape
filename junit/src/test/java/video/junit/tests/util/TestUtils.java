package video.junit.tests.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

/**
 * Created by sergey on 25.06.16.
 */
@Slf4j
public class TestUtils {

  private static Method getMethod(Class<?> tClass, String methodName) {
    try {
      return tClass.getDeclaredMethod(methodName);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static void runRule(TestRule rule, Object target, String methodName) {
    Class<?> clazz = target.getClass();
    Method method = TestUtils.getMethod(clazz, methodName);
    Description description = Description.createTestDescription(clazz, method.getName(), method.getDeclaredAnnotations());
    try {
      InvokeMethod invokeMethod = new InvokeMethod(new FrameworkMethod(method), target);
      rule.apply(invokeMethod, description).evaluate();
    } catch (Throwable throwable) {
      log.warn(Arrays.toString(throwable.getStackTrace()));
    }
  }
}
