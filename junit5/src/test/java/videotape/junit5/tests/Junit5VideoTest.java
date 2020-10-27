/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Lorenzo Delgado
 * Copyright (c) 2016 Serhii Pirohov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package videotape.junit5.tests;

import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.junit5.Video;
import es.lnsd.videotape.junit5.VideoExtension;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.fail;


public class Junit5VideoTest extends BaseTest {


  @BeforeEach
  public void setUp() throws IOException {
    FileUtils.deleteDirectory(ConfigLoader.load().folder());
  }

  @Video
  public void testVideo() throws InterruptedException {
    Thread.sleep(5000);
    fail("");
  }

  public void testWithoutAnnotation() throws InterruptedException {
    Thread.sleep(5000);
    fail("");
  }

  @Video
  public void successTest() throws InterruptedException {
    Thread.sleep(5000);
  }

  @Test
  void shouldBeVideoForAnnotatedMethod() throws Exception {
    ExtensionContext context = prepareMock("testVideo", new AssertionError());

    VideoExtension ext = new VideoExtension();
    ext.beforeTestExecution(context);
    ext.afterTestExecution(context);
    verifyVideoFileExistsWithName("testVideo");
  }

  @Test
  void shouldNotBeVideoForMethodWithoutAnnotation() throws Exception {
    ExtensionContext context = prepareMock("testWithoutAnnotation", new AssertionError());

    VideoExtension ext = new VideoExtension();
    ext.beforeTestExecution(context);
    ext.afterTestExecution(context);
    verifyVideoFileNotExists();
  }

  @Test
  void shouldNotBeVideoForSuccessTest() throws Exception {
    ExtensionContext context = prepareMock("successTest");

    VideoExtension ext = new VideoExtension();
    ext.beforeTestExecution(context);
    ext.afterTestExecution(context);
    verifyVideoFileNotExists();
  }

  private ExtensionContext prepareMock(String testMethodName, Throwable ex) throws NoSuchMethodException {
    Method method = this.getClass().getMethod(testMethodName);
    ExtensionContext context = Mockito.mock(ExtensionContext.class);

    Mockito.when(context.getTestMethod()).thenReturn(Optional.ofNullable(method));
    Mockito.when(context.getExecutionException()).thenReturn(Optional.ofNullable(ex));
    return context;
  }

  private ExtensionContext prepareMock(String testMethodName) throws NoSuchMethodException {
    return prepareMock(testMethodName, null);
  }
}


