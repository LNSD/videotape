/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Lorenzo Delgado
 * Copyright (c) 2016-2019 Serhii Pirohov
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
 */

package videotape.testng.inttests

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import es.lnsd.videotape.core.Video
import es.lnsd.videotape.core.config.RecordingMode
import es.lnsd.videotape.core.config.KeepStrategy
import es.lnsd.videotape.testng.VideoListener
import java.nio.file.Paths
import javax.lang.model.element.Modifier
import org.apache.commons.io.FileUtils
import org.testng.Assert
import org.testng.annotations.Test
import spock.lang.Shared
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class TestNGIntegrationTest extends BaseSpec {

  @Shared
  File OUTPUT_DIR = Paths.get(System.getProperty("project.test.resultsdir"), "/video").toFile()

  def setupSpec() {
    FileUtils.deleteDirectory(OUTPUT_DIR)
  }

  def setup() {
    System.setProperty("user.dir", System.getProperty("project.test.resultsdir"))
  }

  def cleanup() {
    FileUtils.deleteDirectory(OUTPUT_DIR)
  }

  def "should be one recording on test fail"() {
    given:
    def testClass = generateTestNGTestClass("FailWithVideoTest") {
      MethodSpec.methodBuilder("failWithVideo")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .addStatement('$T.fail()', Assert)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 1
    OUTPUT_DIR.listFiles().first().getName().startsWith("failWithVideo_")
  }

  def "should not be recording on test success"() {
    given:
    def testClass = generateTestNGTestClass("SuccessTest") {
      MethodSpec.methodBuilder("successTest")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 0
  }

  def "should be recording with custom name"() {
    given:
    def testClass = generateTestNGTestClass("FailWithCustomVideoNameTest") {
      MethodSpec.methodBuilder("failWithCustomVideoName")
          .addAnnotation(Test)
          .addAnnotation(AnnotationSpec.builder(Video)
              .addMember("name", '$S', "custom_name")
              .build())
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .addStatement('$T.fail()', Assert)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 1
    OUTPUT_DIR.listFiles().first().getName().startsWith("custom_name_")
  }

  def "should be recording for successful test and save strategy ALL"() {
    given:
    System.setProperty("video.keep", KeepStrategy.ALL.toString())
    def testClass = generateTestNGTestClass("PassWithVideoTest") {
      MethodSpec.methodBuilder("passWithVideo")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 1
    OUTPUT_DIR.listFiles().first().getName().startsWith("passWithVideo_")
  }

  def "should be recording for failed test and save strategy FAILED_ONLY"() {
    given:
    System.setProperty("video.keep", KeepStrategy.FAILED_ONLY.toString())
    def testClass = generateTestNGTestClass("FailAndFailedOnlyTest") {
      MethodSpec.methodBuilder("failAndFailedOnlyTest")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .addStatement('$T.fail()', Assert)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 1
    OUTPUT_DIR.listFiles().first().getName().startsWith("failAndFailedOnlyTest_")
  }

  def "should not be recording for successful test and save strategy FAILED_ONLY"() {
    given:
    System.setProperty("video.keep", KeepStrategy.FAILED_ONLY.toString())
    def testClass = generateTestNGTestClass("PassWithoutVideoTest") {
      MethodSpec.methodBuilder("passWithoutVideo")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    OUTPUT_DIR.isDirectory()
    OUTPUT_DIR.listFiles().size() == 0
  }

  def "should not be video for method without annotation"() {
    given:
    def testClass = generateTestNGTestClass("FailWithoutVideoTest") {
      MethodSpec.methodBuilder("failWithoutVideo")
          .addAnnotation(Test)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .addStatement('$T.fail()', Assert)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    !OUTPUT_DIR.isDirectory()
  }

  def "should not be recording if disabled and record mode ALL"() {
    given:
    System.setProperty("video.enable", "false")
    System.setProperty("video.mode", RecordingMode.ALL.toString())
    def testClass = generateTestNGTestClass("FailWithVideoDisabledTest") {
      MethodSpec.methodBuilder("failWithVideoDisabled")
          .addAnnotation(Test)
          .addAnnotation(Video)
          .addModifiers(Modifier.PUBLIC)
          .returns(void)
          .addStatement('$T.fail()', Assert)
          .build()
    }

    when:
    runTestNGClass(testClass, new VideoListener())
    then:
    !OUTPUT_DIR.isDirectory()
  }
}
