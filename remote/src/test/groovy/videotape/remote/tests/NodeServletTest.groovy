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

package videotape.remote.tests

import es.lnsd.videotape.core.recorder.monte.MonteRecorder
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Stepwise

import static es.lnsd.videotape.remote.utils.RestUtils.sendRecordingRequest


@Stepwise
class NodeServletTest extends BaseSpec {

  @Shared
  String VIDEO_FOLDER = System.getProperty('user.dir')

  def setup() {
    MonteRecorder.conf().folder().deleteDir()
  }

  private static boolean isOsWindows() {
    System.properties['os.name'] == 'windows'
  }

  def "shouldBeOkMessageOnStartWithoutParameters"() {
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/start")
    then:
    message == "recording started"
  }

  def "shouldBeOkMessageOnStartWitParameters"() {
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/start?name=video")
    then:
    message == "recording started"
  }

  def "shouldBeFileNameInMessageOnStop"() {
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/stop")
    then:
    message.startsWith "recording stopped ${VIDEO_FOLDER}${File.separator}video${File.separator}video_recording"
  }

  def "shouldBeDefaultFileName"() {
    given:
    sendRecordingRequest(NODE_SERVLET_URL + "/start")
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/stop")
    then:
    message.startsWith "recording stopped ${VIDEO_FOLDER}${File.separator}video${File.separator}video_recording"
  }

  @IgnoreIf({ os.windows })
  def "shouldBeFileNameAsNameRequestParameter"() {
    def name = "video"
    given:
    sendRecordingRequest(NODE_SERVLET_URL + "/start?name=$name")
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/stop")
    then:
    message.startsWith "recording stopped ${VIDEO_FOLDER}${File.separator}video${File.separator}video_recording"
    getVideoFiles().first().name =~ name
  }

  @IgnoreIf({ os.windows })
  def "shouldNotCreateVideoFileIfSuccessTestKeyPassed"() {
    given:
    sendRecordingRequest(NODE_SERVLET_URL + "/start")
    when:
    sendRecordingRequest(NODE_SERVLET_URL + "/stop?result=true")
    then:
    getVideoFiles().size() == 0
  }

  @IgnoreIf({ os.windows })
  def "shouldCreateVideoFileIfFailTestKeyPassed"() {
    given:
    sendRecordingRequest(NODE_SERVLET_URL + "/start")
    when:
    sendRecordingRequest(NODE_SERVLET_URL + "/stop?result=false")
    then:
    getVideoFiles().size() == 1
  }


  def "shouldBeCustomFolderForVideo"() {
    def folderName = "video"
    given:
    sendRecordingRequest(NODE_SERVLET_URL + "/start?name=video&folder=${folderName}")
    when:
    def message = sendRecordingRequest(NODE_SERVLET_URL + "/stop?result=false")
    then:
    message.startsWith "recording stopped ${VIDEO_FOLDER}${File.separator}${folderName}${File.separator}video_recording"
  }
}

