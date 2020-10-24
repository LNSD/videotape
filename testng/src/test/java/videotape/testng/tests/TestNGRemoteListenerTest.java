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

import es.lnsd.videotape.core.annotations.Video;
import es.lnsd.videotape.core.recorder.monte.MonteRecorder;
import es.lnsd.videotape.testng.RemoteVideoListener;
import java.io.File;
import org.openqa.grid.selenium.GridLauncherV3;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.openqa.selenium.net.PortProber.findFreePort;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TestNGRemoteListenerTest extends BaseTest {

  @BeforeClass
  public static void runGrid() throws Exception {
    int port = findFreePort();
    startGrid(findFreePort(), port);
    System.setProperty("video.remote.hub", "http://localhost:" + port);
  }

  private static void startGrid(int hubPort, int nodePort) throws Exception {
    String[] hub = {"-port", "" + hubPort,
        "-host", "localhost",
        "-role", "hub"};

    GridLauncherV3.main(hub);

    String[] node = {"-port", "" + nodePort,
        "-host", "localhost",
        "-role", "node",
        "-hub", "http://localhost:" + hubPort + "/grid/register",
        "-servlets", "es.lnsd.videotape.remote.node.Video"};
    GridLauncherV3.main(node);
    Thread.sleep(1000);
  }

  @BeforeMethod
  @AfterMethod
  public void resetConfig() {
    System.clearProperty("video.folder");
  }

  @Test
  @Video
  public void shouldBeOneRecordingOnTestFail() {
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
  }

  @Test
  @Video
  public void shouldNotBeRecordingOnTestSuccess() {
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestSuccess(result);
    File file = MonteRecorder.getLastVideo();
    assertFalse(file.exists());
  }

  @Test
  @Video(name = "custom_name")
  public void shouldBeRecordingWithCustomNameOnTestFail() {
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists(), "File " + file.getName());
    assertThat(file.getName(), startsWith("custom_name"));
  }

  @Test
  @Video
  public void shouldPassIfGridConfiguredWithCustomPorts() throws Exception {
    int port = findFreePort();
    startGrid(findFreePort(), port);
    System.setProperty("video.remote.hub", "http://localhost:" + port);
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists(), "File " + file.getName());
    assertThat(file.getName(), startsWith("shouldPassIfGridConfiguredWithCustomPorts"));
  }

  @Test
  public void shouldBeVideoForMethodWithoutAnnotationIfModeAll() {
    System.setProperty("video.mode", "ALL");
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists(), "File " + file.getName());
  }

  @Test
  @Video
  public void shouldBeDefaultFolderForVideo() {
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertThat(file.getParentFile().getName(), equalTo("video"));
  }

  @Test
  @Video
  public void shouldBeCustomFolderForVideo() {
    System.setProperty("video.folder", System.getProperty("user.dir") + "/custom_folder");
    ITestResult result = prepareMock(testMethod);
    RemoteVideoListener listener = new RemoteVideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertThat(file.getParentFile().getName(), equalTo("custom_folder"));
  }
}