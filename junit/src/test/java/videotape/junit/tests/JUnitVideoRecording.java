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

package videotape.junit.tests;

import es.lnsd.videotape.core.annotations.Video;
import es.lnsd.videotape.core.recorder.monte.MonteRecorder;
import es.lnsd.videotape.junit.VideoRule;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import videotape.junit.tests.util.TestUtils;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class JUnitVideoRecording {

  @Video
  public void failWithVideo() throws Exception {
    fail();
  }

  @Video(name = "custom_name")
  public void failWithCustomVideoName() throws Exception {
    fail();
  }

  @Video
  public void successWithVideo() {
  }

  @Test
  public void shouldPassOnMethodWithVideoAnnotation() {
    String methodName = "failWithVideo";
    VideoRule videoRule = new VideoRule();
    TestUtils.runRule(videoRule, this, methodName);
    verifyVideoFileExistsWithName(methodName);
  }

  @Test
  public void shouldPassOnMethodWithVideoAnnotationAndCustomName() {
    String methodName = "failWithCustomVideoName";
    String video_name = "custom_name";
    VideoRule videoRule = new VideoRule();
    TestUtils.runRule(videoRule, this, methodName);
    verifyVideoFileExistsWithName(video_name);
  }

  @Test
  public void shouldPassOnSuccessTestWithVideoIfSaveModeAll() {
    String methodName = "successWithVideo";

    System.setProperty("video.save.mode", "ALL");

    VideoRule videoRule = new VideoRule();
    TestUtils.runRule(videoRule, this, methodName);
    verifyVideoFileExistsWithName(methodName);
  }

  @Test
  public void shouldPassOnSuccessTestWithoutVideoIfSaveModeFailedOnly() {
    String methodName = "successWithVideo";
    System.setProperty("video.save.mode", "FAILED_ONLY");
    VideoRule videoRule = new VideoRule();
    TestUtils.runRule(videoRule, this, methodName);
    verifyVideoFileNotExists();
  }

  @Before
  public void setUp() throws IOException {
    FileUtils.deleteDirectory(MonteRecorder.conf().folder());
  }

  @After
  public void tearDown() throws Exception {
    setUp();
  }

  private void verifyVideoFileExistsWithName(String fileName) {
    File file = MonteRecorder.getLastVideo();
    assertTrue(file.exists());
    assertThat(file.getName(), startsWith(fileName));
  }

  private void verifyVideoFileNotExists() {
    assertFalse(MonteRecorder.getLastVideo().exists());
  }


}
