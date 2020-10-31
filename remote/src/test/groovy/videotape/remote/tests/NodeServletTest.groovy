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

import es.lnsd.videotape.core.config.ConfigLoader
import es.lnsd.videotape.core.recorder.AbstractRecorder
import es.lnsd.videotape.remote.StartGrid
import es.lnsd.videotape.remote.client.Client
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class NodeServletTest extends Specification {

  def setupSpec() {
    System.setProperty("user.dir", System.getProperty('project.test.resultsdir'))
    StartGrid.main([] as String[])
  }

  @Shared
  def client = Client.get("http://localhost:5555/extra/Video")
  @Shared
  def OUTPUT_DIR = System.getProperty('project.test.resultsdir')

  def "shouldBeOkMessageOnStartWithoutParameters"() {
    when:
    def message = client.start()
    then:
    message == "recording started"

    cleanup:
    client.stop()
  }

  def "shouldBeOkMessageOnStartWithParameters"() {
    when:
    def message = client.sendRequest("/start?name=video")
    then:
    message == "recording started"

    cleanup:
    client.stop()
  }

  def "shouldBeFileNameInMessageOnStop"() {
    given:
    client.sendRequest("/start?name=video")

    when:
    def message = client.stop()
    then:
    message.startsWith "recording stopped"
    message.contains "${OUTPUT_DIR}/video/video"
  }

  def "shouldBeDefaultFileName"() {
    given:
    client.start()

    when:
    def message = client.stop()
    then:
    message.startsWith "recording stopped"
    message.contains "${OUTPUT_DIR}/video/video"
  }

  @IgnoreIf({ os.windows })
  def "shouldBeFileNameAsNameRequestParameter"() {
    given:
    def name = "video"
    client.sendRequest("/start?name=$name")

    when:
    def message = client.stop()
    then:
    message.startsWith "recording stopped"
    message.contains "${OUTPUT_DIR}/video/video"
    ConfigLoader.load().folder().toFile().listFiles().first().name =~ name
  }

  @IgnoreIf({ os.windows })
  def "shouldNotCreateVideoFileIfSuccessTestKeyPassed"() {
    given:
    client.start()

    when:
    client.sendRequest("/stop?result=true")
    then:
    !AbstractRecorder.lastVideo().exists()
  }

  @IgnoreIf({ os.windows })
  def "shouldCreateVideoFileIfFailTestKeyPassed"() {
    given:
    client.start()

    when:
    client.sendRequest("/stop?result=false")
    then:
    AbstractRecorder.lastVideo().exists()
  }

  def "shouldBeCustomFolderForVideo"() {
    given:
    def folderName = "${OUTPUT_DIR}/recordings"
    client.sendRequest("/start?name=video&folder=${folderName}")

    when:
    def message = client.sendRequest("/stop?result=false")
    then:
    message.startsWith "recording stopped"
    message.contains "${OUTPUT_DIR}/recordings/video"
  }
}

