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

import es.lnsd.videotape.core.config.ConfigLoader
import es.lnsd.videotape.core.config.Configuration
import es.lnsd.videotape.core.config.KeepStrategy
import es.lnsd.videotape.core.config.RecordingMode
import java.nio.file.Paths
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class ConfigLoaderSpec extends Specification {

  def "default config should be loaded"() {
    when:
    def conf = ConfigLoader.load(Configuration.class)

    then:
    conf.output() == Paths.get(System.getProperty("user.dir"), "video")
    conf.mode() == RecordingMode.ANNOTATED
    conf.keepStrategy() == KeepStrategy.FAILED_ONLY
    conf.videoEnabled()
    conf.fileFormat() == "mp4"
  }

  def "load configuration from file"() {
    given:
    System.setProperty('video.configurationFile', file)

    when:
    def conf = ConfigLoader.load(Configuration.class)

    then:
    conf.mode() == RecordingMode.ANNOTATED
    conf.keepStrategy() == KeepStrategy.ALL
    !conf.videoEnabled()
    conf.fileFormat() == "webm"

    where:
    file << [
            'classpath:custom-video.properties',
            "file:${System.getProperty('project.test.resourcesdir')}/custom-video.properties"
    ]
  }
}
