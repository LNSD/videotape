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

package es.lnsd.videotape.core.config;

import es.lnsd.videotape.core.config.utils.LowerCaseConverter;
import es.lnsd.videotape.core.config.utils.PathConverter;
import java.nio.file.Path;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "${conf.file}",
    "classpath:video.properties"
})
public interface Configuration extends Config {

  @Key("video.enable")
  @DefaultValue("true")
  Boolean videoEnabled();

  @Key("video.mode")
  @ConverterClass(RecordingMode.Converter.class)
  @DefaultValue("ANNOTATED")
  RecordingMode mode();

  @Key("video.keep")
  @ConverterClass(KeepStrategy.Converter.class)
  @DefaultValue("FAILED_ONLY")
  KeepStrategy keepStrategy();

  @Key("video.output")
  @ConverterClass(PathConverter.class)
  @DefaultValue("${user.dir}/video")
  Path output();

  @Key("video.format")
  @ConverterClass(LowerCaseConverter.class)
  @DefaultValue("mp4")
  String fileFormat();
}
