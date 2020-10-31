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
import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.junit.VideoRule;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;
import videotape.junit.tests.util.TestUtils;

import static junit.framework.Assert.fail;

public class JUnitVideoRecordingTest extends BaseTestSuite {

  @Rule
  public final TestRule restoreSystemProperties = new RestoreSystemProperties();

  @Before
  public void setUp() throws IOException {
    Path outputDir = Paths.get(System.getProperty("project.test.resultsdir"), "video");
    System.setProperty("video.folder", outputDir.toString());
    FileUtils.deleteDirectory(ConfigLoader.load().folder().toFile());
  }

  @After
  public void tearDown() throws Exception {
    setUp();
  }

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
}
