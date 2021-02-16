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
 *
 */

package es.lnsd.videotape.core.backend;

import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegConfiguration;
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegRecorder;
import es.lnsd.videotape.core.backend.monte.MonteConfiguration;
import es.lnsd.videotape.core.backend.monte.MonteRecorder;
import es.lnsd.videotape.core.backend.vlc.VlcRecorder;
import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.RecorderType;
import org.apache.commons.lang3.NotImplementedException;

public class RecorderBackendFactory {

  private RecorderBackendFactory() {
    throw new IllegalStateException();
  }

  public static RecorderBackend getRecorder(RecorderType recorderType) {

    if (recorderType == RecorderType.FFMPEG || recorderType == RecorderType.FFMPEG_WRAPPER) {
      FFMpegConfiguration configuration = ConfigLoader.load(FFMpegConfiguration.class);
      return new FFMpegRecorder(configuration);
    }

    if (recorderType == RecorderType.VLC) {
      BackendConfiguration configuration = ConfigLoader.load(BackendConfiguration.class);
      return new VlcRecorder(configuration);
    }

    if (recorderType == RecorderType.MONTE) {
      BackendConfiguration configuration = ConfigLoader.load(MonteConfiguration.class);
      return new MonteRecorder(configuration);
    }

    String err = String.format("Recorder '%s' not available", recorderType);
    throw new NotImplementedException(err);
  }
}
