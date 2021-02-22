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

package videotape.core.inttests


import es.lnsd.videotape.core.DefaultRecorder
import es.lnsd.videotape.core.Recorder
import es.lnsd.videotape.core.backend.RecorderBackend
import es.lnsd.videotape.core.backend.RecorderBackendFactory
import es.lnsd.videotape.core.config.ConfigLoader
import es.lnsd.videotape.core.config.Configuration
import es.lnsd.videotape.core.config.RecorderType
import es.lnsd.videotape.core.di.ConfigurationModule
import es.lnsd.videotape.core.di.RecorderModule
import es.lnsd.videotape.core.utils.FileManager
import es.lnsd.videotape.core.utils.FileNameBuilder
import java.nio.file.Path
import javax.inject.Inject
import spock.guice.UseModules
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

import static org.apache.commons.io.FileUtils.ONE_KB

@Unroll
@RestoreSystemProperties
@UseModules([ConfigurationModule, RecorderModule])
class RecorderBackendSpec extends Specification {

  @Shared
  Path VIDEO_FOLDER = Path.of(System.getProperty("project.test.resultsdir")).resolve("video")

  static final Long TEN_KB = 10 * ONE_KB
  static final String VIDEO_FILE_NAME = "video_test"

  @Inject
  FileManager fileManager
  @Inject
  FileNameBuilder fileNameBuilder

  static Path recordAFewSecondsVideo(Recorder recorder) {
    recorder.startRecording(VIDEO_FILE_NAME)
    sleep(5)
    recorder.stopRecording()

    return recorder.keepRecording()
  }

  static void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000)
    } catch (InterruptedException e) {
      e.printStackTrace()
    }
  }

  def "should be video in folder with #type recorder"() {
    given:
    def configuration = ConfigLoader.load(Configuration, [
            'video.folder': VIDEO_FOLDER.toString(),
    ])
    RecorderBackend backend = RecorderBackendFactory.getRecorder(type as RecorderType)
    Recorder recorder = new DefaultRecorder(configuration, backend, fileManager, fileNameBuilder)

    when:
    File video = recordAFewSecondsVideo(recorder).toFile()

    then:
    verifyAll {
      video.exists()
      video.parentFile.toPath() == VIDEO_FOLDER
      video.name.startsWith(VIDEO_FILE_NAME)
      video.length() >= TEN_KB
    }

    where:
    type << RecorderType.values()
  }

  @Requires({ os.macOs })
  def "ffmpeg should record video with custom pixel format for #type"() {
    given:
    def configuration = ConfigLoader.load(Configuration, [
            'video.folder': VIDEO_FOLDER.toString()
    ])
    RecorderBackend backend = RecorderBackendFactory.getRecorder(type as RecorderType)
    Recorder recorder = new DefaultRecorder(configuration, backend, fileManager, fileNameBuilder)

    // TODO Move to DI
    System.setProperty("video.ffmpeg.pixelFormat", "yuv444p")

    when:
    File video = recordAFewSecondsVideo(recorder).toFile()
    then:
    verifyAll {
      video.exists()
      video.parentFile.toPath() == VIDEO_FOLDER
      video.name.startsWith(VIDEO_FILE_NAME)
      video.length() >= TEN_KB
    }

    where:
    type << [RecorderType.FFMPEG, RecorderType.FFMPEG_WRAPPER]
  }
}
