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

import es.lnsd.videotape.core.backend.Backend
import es.lnsd.videotape.core.backend.BackendConfiguration
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegConfiguration
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegWrapperRecorder
import es.lnsd.videotape.core.backend.monte.MonteConfiguration
import es.lnsd.videotape.core.backend.monte.MonteRecorder
import es.lnsd.videotape.core.backend.vlc.VlcRecorder
import es.lnsd.videotape.core.config.ConfigLoader
import java.nio.file.Path
import org.apache.commons.io.FileUtils
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

import static org.apache.commons.io.FileUtils.ONE_KB

@Unroll
@RestoreSystemProperties
class RecorderBackendSpec extends Specification {

  static final String VIDEO_FILE_NAME = "video_test"

  @Shared
  Path OUTPUT_DIR = Path.of(System.getProperty("project.test.resultsdir")).resolve("video")

  def setupSpec() {
    try {
      FileUtils.forceMkdir(OUTPUT_DIR.toFile())
    } catch (ex) {
      ex.printStackTrace()
    }
  }

  def cleanupSpec() {
    try {
      FileUtils.deleteDirectory(OUTPUT_DIR.toFile())
    } catch (ex) {
      ex.printStackTrace()
    }
  }

  static void recordAFewSecondsVideo(Backend recorder, File file) {
    recorder.start(file.absoluteFile.toPath())
    sleep(5)
    recorder.stop()
  }

  static void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000)
    } catch (InterruptedException e) {
      e.printStackTrace()
    }
  }

  /**
   * Monte recorder
   */

  def "Record video with MONTE backend default configuration"() {
    given:
    def video = OUTPUT_DIR.resolve(VIDEO_FILE_NAME + ".avi").toFile()

    MonteConfiguration config = ConfigLoader.load(MonteConfiguration.class)
    Backend backend = new MonteRecorder(config)

    when:
    recordAFewSecondsVideo(backend, video)

    then:
    with(video) {
      verifyAll {
        exists()
        parentFile.toPath() == OUTPUT_DIR
        name.startsWith(VIDEO_FILE_NAME)
        length() >= 10 * ONE_KB
      }
    }
  }

  /**
   * FFMpeg recorder
   */

  def "Record video with FFMPEG_WRAPPER backend default configuration"() {
    given:
    def video = OUTPUT_DIR.resolve(VIDEO_FILE_NAME + ".mp4").toFile()

    FFMpegConfiguration config = ConfigLoader.load(FFMpegConfiguration)
    Backend backend = new FFMpegWrapperRecorder(config)

    when:
    recordAFewSecondsVideo(backend, video)

    then:
    with(video) {
      verifyAll {
        exists()
        parentFile.toPath() == OUTPUT_DIR
        name.startsWith(VIDEO_FILE_NAME)
        length() >= 10 * ONE_KB
      }
    }
  }

  @Requires({ os.macOs })
  def "Record video with FFMPEG_WRAPPER backend with custom pixel format"() {
    given:
    def video = OUTPUT_DIR.resolve(VIDEO_FILE_NAME + ".mp4").toFile()

    FFMpegConfiguration config = ConfigLoader.load(FFMpegConfiguration, [
            "video.ffmpeg.pixelFormat": "yuyv422"
    ])
    Backend backend = new FFMpegWrapperRecorder(config)

    when:
    recordAFewSecondsVideo(backend, video)

    then:
    with(video) {
      verifyAll {
        exists()
        parentFile.toPath() == OUTPUT_DIR
        name.startsWith(VIDEO_FILE_NAME)
        length() >= 10 * ONE_KB
      }
    }
  }

  /**
   * VLC recorder
   */

  def "Record video with VLC backend default configuration"() {
    given:
    def video = OUTPUT_DIR.resolve(VIDEO_FILE_NAME + ".mp4").toFile()

    BackendConfiguration config = ConfigLoader.load(BackendConfiguration)
    Backend backend = new VlcRecorder(config)

    when:
    recordAFewSecondsVideo(backend, video)

    then:
    with(video) {
      verifyAll {
        exists()
        parentFile.toPath() == OUTPUT_DIR
        name.startsWith(VIDEO_FILE_NAME)
        length() >= 10 * ONE_KB
      }
    }
  }
}
