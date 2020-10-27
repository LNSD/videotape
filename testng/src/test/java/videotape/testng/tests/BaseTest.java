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

package videotape.testng.tests;

import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.RecorderType;
import es.lnsd.videotape.core.config.RecordingMode;
import es.lnsd.videotape.testng.VideoListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.internal.ConstructorOrMethod;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BaseTest {

  protected Method testMethod;

  @AfterClass
  public static void tearDown() throws Exception {
    // deleteVideoDir();
  }

  private static void deleteVideoDir() throws IOException {
    FileUtils.deleteDirectory(ConfigLoader.load().folder());
  }

  @BeforeMethod
  public void beforeMethod(Method method) throws IOException {
    this.testMethod = method;
    deleteVideoDir();
    System.setProperty("video.mode", RecordingMode.ANNOTATED.toString());
    System.setProperty("video.recorder.type", RecorderType.MONTE.toString());
  }

  protected ITestResult prepareMock(Class<?> tClass, Method method) {
    ITestResult result = mock(ITestResult.class);
    IClass clazz = mock(IClass.class);
    ITestNGMethod testNGMethod = mock(ITestNGMethod.class);
    ConstructorOrMethod cm = mock(ConstructorOrMethod.class);
    String methodName = method.getName();
    when(result.getTestClass()).thenReturn(clazz);
    when(result.getTestClass().getRealClass()).thenReturn(tClass);
    when(clazz.getName()).thenReturn(this.getClass().getName());
    when(result.getMethod()).thenReturn(testNGMethod);
    when(cm.getMethod()).thenReturn(method);
    when(result.getMethod().getConstructorOrMethod()).thenReturn(cm);
    when(testNGMethod.getMethodName()).thenReturn(methodName);
    ITestContext context = mock(ITestContext.class);
    when(result.getTestContext()).thenReturn(context);
    XmlTest xmlTest = new XmlTest();
    XmlSuite suite = new XmlSuite();
    xmlTest.setXmlSuite(suite);
    suite.setListeners(Collections.singletonList(VideoListener.class.getName()));
    when(context.getCurrentXmlTest()).thenReturn(xmlTest);
    return result;
  }

  protected ITestResult prepareMock(Method testMethod) {
    return prepareMock(TestNgListenerTest.class, testMethod);
  }
}
