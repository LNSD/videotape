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

package videotape.core.tests

import es.lnsd.videotape.core.config.ConfigLoader
import es.lnsd.videotape.core.config.RecorderType
import es.lnsd.videotape.core.exception.RecordingException
import es.lnsd.videotape.core.recorder.RecorderFactory
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

import static org.apache.commons.io.FileUtils.ONE_KB

@Unroll
@RestoreSystemProperties
class RecorderTest extends BaseSpec {

  def setup() {
    System.setProperty("user.dir", System.getProperty("project.test.output"))
  }

  def "should be video file in folder with #type"() {
    when:
    File video = recordVideo(type)
    then:
    video.exists()
    video.length() >= 10 * ONE_KB

    where:
    type << RecorderType.values()
  }

  def "should be video in #name folder with #type"() {
    when:
    File video = recordVideo(type)
    then:
    video.parentFile.name == name

    where:
    name = "video"
    type << RecorderType.values()
  }

  def "should be absolute recording path with #type"() {
    given:
    def conf = ConfigLoader.load()

    when:
    File video = recordVideo(type)
    then:
    video.absolutePath.startsWith(conf.folder().getAbsolutePath() + File.separator + VIDEO_FILE_NAME)

    where:
    type << RecorderType.values()
  }

  def "should be exact video file name for #type"() {
    when:
    File video = recordVideo(type)
    then:
    video.exists()
    video.getName().startsWith(VIDEO_FILE_NAME)

    where:
    type << RecorderType.values()
  }

  // TODO Improve test case
  def "should be recording exception for Monte if recording was not started"() {
    given:
    def conf = ConfigLoader.load()

    when:
    RecorderFactory.getRecorder(RecorderType.MONTE, conf).stopAndSave(VIDEO_FILE_NAME)
    then:
    RecordingException ex = thrown()
    // Alternative syntax: def ex = thrown(InvalidDeviceException)
    ex.message == "Video recording was not started"
  }

  def "ffmpeg should record video with custom pixel format for #type"() {
    given:
    System.setProperty("video.ffmpeg.pixelFormat", "yuv444p")

    when:
    File video = recordVideo(type)
    then:
    video.exists()

    where:
    type << [RecorderType.FFMPEG, RecorderType.FFMPEG_LEGACY, RecorderType.FFMPEG_WRAPPER]
  }
}