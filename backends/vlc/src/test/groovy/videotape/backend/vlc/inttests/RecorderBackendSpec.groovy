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

package videotape.backend.vlc.inttests

import es.lnsd.videotape.backend.vlc.VlcModule
import es.lnsd.videotape.backend.vlc.VlcRecorderBackend
import es.lnsd.videotape.core.backend.Backend
import es.lnsd.videotape.core.backend.BackendConfiguration
import java.nio.file.Path
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification

import static org.apache.commons.io.FileUtils.ONE_KB

@UseModules([VlcModule])
class RecorderBackendSpec extends Specification {

  static final Long TEN_KB = 10 * ONE_KB
  static final String VIDEO_FILE_NAME = "video_test"

  @Shared
  Path OUTPUT_DIR = Path.of(System.getProperty("project.test.resultsdir")).resolve("video")
  @Shared
  Path VIDEO_FILE = OUTPUT_DIR.resolve(VIDEO_FILE_NAME + ".mp4")

  @Inject
  private BackendConfiguration configuration

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

  static void recordAFewSecondsVideo(Backend recorder, Path file) {
    recorder.start(file)
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

  def "should be video in folder with vlc backend"() {
    given:
    Backend backend = new VlcRecorderBackend(configuration)

    when:
    recordAFewSecondsVideo(backend, VIDEO_FILE)

    then:
    with(VIDEO_FILE.toFile()) {
      verifyAll {
        exists()
        parentFile.toPath() == OUTPUT_DIR
        name.startsWith(VIDEO_FILE_NAME)
        length() >= TEN_KB
      }
    }
  }
}
