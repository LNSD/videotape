package video.junit5.tests;

import com.automation.remarks.junit5.Video;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Created by sergey on 12.02.17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Video
@Tag("Flaky")
@Test
public @interface FlakyTest {
}
