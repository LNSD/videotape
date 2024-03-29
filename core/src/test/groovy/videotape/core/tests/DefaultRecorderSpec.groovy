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

import es.lnsd.videotape.core.DefaultRecorder
import es.lnsd.videotape.core.Recorder
import es.lnsd.videotape.core.backend.Backend
import es.lnsd.videotape.core.config.Configuration
import es.lnsd.videotape.core.exception.RecordingException
import es.lnsd.videotape.core.utils.FileManager
import es.lnsd.videotape.core.utils.FileNameBuilder
import java.nio.file.Path
import javax.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import videotape.core.di.MockConfigurationModule
import videotape.core.di.MockRecorderModule

@UseModules([MockConfigurationModule, MockRecorderModule])
class DefaultRecorderSpec extends Specification {

  // Class under test
  private Recorder recorder

  // Dependencies
  @Inject
  private Configuration configuration
  @Inject
  private Backend backend
  @Inject
  private FileManager fileManager
  @Inject
  private FileNameBuilder fileNameBuilder

  void setup() {
    recorder = new DefaultRecorder(configuration, backend, fileManager, fileNameBuilder)
  }

  /**
   * Recorder backend management
   */

  def "should create destination directory and start backend recording"() {
    given:
    def dstDir = Path.of("/video/")
    def extension = "webm"
    def fileName = "temp_2015_10_21_19_28.$extension"
    configuration.output() >> dstDir
    configuration.fileFormat() >> extension
    fileNameBuilder.withTimestamp(*_) >> fileName

    when:
    recorder.startRecording("testCaseName")

    then:
    1 * fileManager.createDestinationDir(dstDir)
    1 * backend.start(dstDir.resolve(fileName))
  }

  def "should stop backend recording"() {
    when:
    recorder.stopRecording()

    then:
    1 * backend.isRecording() >> true
    1 * backend.stop()
  }

  def "should raise an exception if recording was not started"() {
    given:
    backend.isRecording() >> false

    when:
    recorder.stopRecording()

    then:
    1 * backend.isRecording()
    def ex = thrown(RecordingException)
    ex.message == "Video recording was not started"
  }

  def "should rename file to destination file on keep recording action"() {
    given:
    def testCaseName = "testCaseMethodName"
    def dstDir = Path.of("/video/")
    def extension = "mp4"
    configuration.output() >> dstDir
    configuration.fileFormat() >> extension
    fileNameBuilder.withTimestamp(*_) >> { String name, String ext -> "${name}_2015_10_21_19_28.$ext" }

    when:
    recorder.startRecording(testCaseName)
    recorder.stopRecording()
    def videoFilePath = recorder.keepRecording()

    then:
    1 * backend.isRecording() >> true
    1 * fileManager.renameFile(
            { it.fileName.toString().startsWith("temp") },
            { it.fileName.toString().startsWith(testCaseName) }
    )
    videoFilePath == dstDir.resolve("${testCaseName}_2015_10_21_19_28.$extension")
  }

  def "should discard temporary file on discard recording action"() {
    when:
    recorder.discardRecording()

    then:
    1 * fileManager.discardFile(_)
  }
}
