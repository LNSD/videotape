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
import es.lnsd.videotape.testng.VideoListener;
import java.io.File;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;


public class TestNGVideoListenerTest extends BaseTest {

  static void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  @Video
  public void shouldBeOneRecordingOnTestFail() {
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
  }

  @Test
  @Video
  public void shouldNotBeRecordingOnTestSuccess() {
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestSuccess(result);
    File file = MonteRecorder.getLastVideo();
    assertFalse(file.exists());
  }

  @Test
  @Video(name = "new_recording")
  public void shouldBeRecordingWithCustomName() {
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
    assertTrue(file.getName().contains("new_recording"));
  }

  @Test
  @Video()
  public void shouldBeRecordingForSuccessfulTestAndSaveModeAll() {
    System.setProperty("video.save.mode", "ALL");
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestSuccess(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
    assertTrue(file.getName().contains("shouldBeRecordingForSuccessfulTest"), "Wrong file name");
  }

  @Test
  @Video()
  public void shouldBeRecordingForFailedTestAndSaveModeFailOnly() {
    System.setProperty("video.save.mode", "FAILED_ONLY");
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
    assertTrue(file.getName().contains("shouldBeRecordingForFailedTestAndSaveModeFailOnly"), "Wrong file name");
  }

  @Test
  @Video()
  public void shouldNotBeRecordingForSuccessfulTestAndSaveModeFailOnly() {
    System.setProperty("video.save.mode", "FAILED_ONLY");
    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestSuccess(result);
    File file = MonteRecorder.getLastVideo();
    assertFalse(file.exists());
  }

  @Test
  @Video()
  public void shouldNotBeVideoIfDisabledAndRecordModeAll() {
    System.setProperty("video.mode", "ALL");
    System.setProperty("video.enabled", "false");

    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertNotEquals(file.getName(), "shouldNotBeVideoIfDisabledAndRecordModeAll.avi");
  }

  @Test
  @Video()
  public void shouldNotBeRecordingForSuccessTestWithFfmpegAndSaveModeFailOnly() throws InterruptedException {
    System.setProperty("video.save.mode", "FAILED_ONLY");
    System.setProperty("video.recorder.type", "FFMPEG");

    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    Thread.sleep(5000);
    listener.onTestSuccess(result);
    File file = MonteRecorder.getLastVideo();
    assertFalse(file.exists());
  }

  @Test
  @Video()
  public void shouldBeRecordingForFailTestWithFfmpegAndSaveModeFailOnly() throws InterruptedException {
    System.setProperty("video.save.mode", "FAILED_ONLY");
    System.setProperty("video.recorder.type", "FFMPEG");


    ITestResult result = prepareMock(testMethod);
    VideoListener listener = new VideoListener();
    listener.onTestStart(result);
    Thread.sleep(5000);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
  }

  @Test
  @Video()
  public void shouldBeRecordingIfCustomVideoAnnotation() {
    System.setProperty("video.save.mode", "FAILED_ONLY");
    System.setProperty("video.recorder.type", "FFMPEG");

    ITestResult result = prepareMock(TestNgCustomVideoListenerTest.class, testMethod);
    ITestListener listener = new CustomVideoListener();
    listener.onTestStart(result);
    sleep(5);
    listener.onTestFailure(result);
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
  }
}

class CustomVideoListener extends VideoListener {

}