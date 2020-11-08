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
import es.lnsd.videotape.core.config.RecordingMode
import es.lnsd.videotape.core.config.SavingStrategy
import java.awt.Toolkit
import java.nio.file.Paths
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class ConfigLoaderTest extends BaseSpec {

  def "default config should be loaded"() {
    when:
    def conf = ConfigLoader.load()

    then:
    conf.folder() == Paths.get(System.getProperty("user.dir"), "video")
    conf.frameRate() == 24
    conf.mode() == RecordingMode.ANNOTATED
    conf.recorderType() == RecorderType.MONTE
    conf.saveStrategy() == SavingStrategy.FAILED_ONLY
    conf.videoEnabled()
    conf.fileFormat() == "mp4"
    conf.screenSize() == Toolkit.defaultToolkit.screenSize
    !conf.isRemote()
    conf.fileName() == null
  }

  def "load configuration from file"() {
    given:
    System.setProperty('video.configurationFile', file)

    when:
    def conf = ConfigLoader.load()

    then:
    conf.frameRate() == 24
    conf.mode() == RecordingMode.ANNOTATED
    conf.recorderType() == RecorderType.MONTE
    conf.saveStrategy() == SavingStrategy.ALL
    !conf.videoEnabled()
    conf.fileFormat() == "webm"
    !conf.isRemote()
    conf.fileName() == null

    where:
    file << [
            'classpath:custom-video.properties',
            "file:${getProjFilePath("core/src/test/resources/custom-video.properties")}"
    ]
  }
}
