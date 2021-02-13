/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2020-2021 Lorenzo Delgado
 *  Copyright (c) 2016 Serhii Pirohov
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package videotape.core.tests

import es.lnsd.videotape.core.DefaultRecorder
import es.lnsd.videotape.core.VideotapeRecorder
import es.lnsd.videotape.core.backend.RecorderBackendFactory
import es.lnsd.videotape.core.config.ConfigLoader
import es.lnsd.videotape.core.config.RecorderType
import spock.lang.Specification

import java.nio.file.Paths

class BaseSpec extends Specification {

  public static final String VIDEO_FILE_NAME = "video_test"

  protected static VideotapeRecorder getRecorder(RecorderType type) {
    def config = ConfigLoader.load()
    def backend = RecorderBackendFactory.getRecorder(type)
    return new DefaultRecorder(config, backend)
  }

  protected static File recordVideo(RecorderType type) {
    def recorder = getRecorder(type)

    recorder.startRecording(VIDEO_FILE_NAME)
    sleep(5)
    recorder.stopRecording()

    return recorder.keepRecording().toFile()
  }

  private static void sleep(int sec) {
    try {
      Thread.sleep(sec * 1000)
    } catch (InterruptedException e) {
      e.printStackTrace()
    }
  }

  def getProjFilePath(path) {
    return Paths.get(System.getProperty('root.path'), path).toAbsolutePath().toString()
  }
}
