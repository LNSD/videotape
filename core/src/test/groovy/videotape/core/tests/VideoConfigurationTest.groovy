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

import es.lnsd.videotape.core.SystemUtils
import es.lnsd.videotape.core.enums.RecorderType
import es.lnsd.videotape.core.enums.RecordingMode
import es.lnsd.videotape.core.enums.VideoSaveMode
import es.lnsd.videotape.core.recorder.VideoRecorder
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

@Unroll
class VideoConfigurationTest extends BaseSpec {

  @RestoreSystemProperties
  def "default config should be loaded"() {
    given:
    System.setProperty('user.dir', '.')

    when:
    def conf = VideoRecorder.conf()

    then:
    conf.folder() == new File("./video")
    conf.frameRate() == 24
    conf.mode() == RecordingMode.ANNOTATED
    conf.recorderType() == RecorderType.MONTE
    conf.saveMode() == VideoSaveMode.FAILED_ONLY
    conf.videoEnabled()
    conf.screenSize() == SystemUtils.systemScreenDimension
    !conf.isRemote()
    conf.fileName() == null
  }
}