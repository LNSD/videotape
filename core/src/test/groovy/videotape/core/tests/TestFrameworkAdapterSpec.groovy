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

package videotape.core.tests

import es.lnsd.videotape.core.Recorder
import es.lnsd.videotape.core.TestFrameworkAdapter
import es.lnsd.videotape.core.Video
import es.lnsd.videotape.core.config.Configuration
import es.lnsd.videotape.core.config.RecordingMode
import es.lnsd.videotape.core.config.KeepStrategy
import es.lnsd.videotape.core.exception.RecordingException
import javax.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import spock.lang.Unroll
import videotape.core.di.MockRecorderModule
import videotape.core.di.MockConfigurationModule

@Unroll
@UseModules([MockConfigurationModule, MockRecorderModule])
class TestFrameworkAdapterSpec extends Specification {

  @Inject
  private Configuration configuration
  @Inject
  private Recorder recorder

  // Class under test
  private TestFrameworkAdapter tfAdapter

  void setup() {
    tfAdapter = new TestFrameworkAdapter(configuration, recorder)
  }

  /**
   * Skip recording
   */

  def "no recording when disabled from configuration"() {
    given:
    configuration.videoEnabled() >> false
    Video videoAnnotation = Stub { enable() >> true }

    when:
    tfAdapter.onTestStart("testName", videoAnnotation)

    then:
    0 * recorder.startRecording(_)
  }

  def "no recording on non annotated test case"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ANNOTATED
    }

    when:
    tfAdapter.onTestStart("testName", null)

    then:
    0 * recorder.startRecording(_)
  }

  def "no recording on video disabled annotated test case while record all mode"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
    }
    Video videoAnnotation = Stub { enable() >> false }

    when:
    tfAdapter.onTestStart("testName", videoAnnotation)

    then:
    0 * recorder.startRecording(_)
  }

/**
 * Recording file naming
 */

  def "recording name should named after test case name"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ANNOTATED
    }
    Video videoAnnotation = Stub {
      enable() >> true
    }

    when:
    tfAdapter.onTestStart("testName", videoAnnotation)

    then:
    with(recorder) {
      1 * startRecording("testName")
    }
  }

  def "recording name should named after test case name when no video annotation"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
    }

    when:
    tfAdapter.onTestStart("testName", null)

    then:
    with(recorder) {
      1 * startRecording("testName")
    }
  }

  def "recording name should be overridden by video annotation's name parameter"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ANNOTATED
    }
    Video videoAnnotation = Stub {
      enable() >> true
      name() >> "customTestCaseName"
    }

    when:
    tfAdapter.onTestStart("testName", videoAnnotation)

    then:
    with(recorder) {
      1 * startRecording(videoAnnotation.name())
    }
  }

  /**
   * Keep/discard video
   */

  def "should keep the recording on test failure"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
      keepStrategy() >> KeepStrategy.FAILED_ONLY
    }

    when:
    tfAdapter.onTestStart("testName", null)
    tfAdapter.onTestFailure()

    then:
    with(recorder) {
      1 * startRecording(_)
      1 * stopRecording()
      1 * keepRecording()
      0 * discardRecording()
    }
  }

  def "should discard the recording on test success"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
      keepStrategy() >> KeepStrategy.FAILED_ONLY
    }

    when:
    tfAdapter.onTestStart("testName", null)
    tfAdapter.onTestSuccess()

    then:
    with(recorder) {
      1 * startRecording(_)
      1 * stopRecording()
      0 * keepRecording()
      1 * discardRecording()
    }
  }

  def "should keep the recording independently of the test case result"() {
    given:
    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
      keepStrategy() >> KeepStrategy.ALL
    }

    when:
    tfAdapter.onTestStart("testName", null)
    tfAdapter.afterTestExecution(testResult)

    then:
    with(recorder) {
      1 * startRecording(_)
      1 * stopRecording()
      1 * keepRecording()
      0 * discardRecording()
    }

    where:
    testResult << [true, false]
  }

  def "should skip discard or keep when recording has not been started"() {

    with(configuration) {
      videoEnabled() >> true
      mode() >> RecordingMode.ALL
      keepStrategy() >> KeepStrategy.FAILED_ONLY
    }

    when:
    tfAdapter.afterTestExecution(testResult)

    then:
    with(recorder) {
      1 * stopRecording() >> { throw new RecordingException() }
      0 * keepRecording()
      0 * discardRecording()
    }

    where:
    testResult << [true, false]
  }
}
