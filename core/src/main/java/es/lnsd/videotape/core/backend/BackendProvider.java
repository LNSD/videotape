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

package es.lnsd.videotape.core.backend;

import com.google.inject.Provider;
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegWrapperRecorder;
import es.lnsd.videotape.core.backend.monte.MonteRecorder;
import es.lnsd.videotape.core.backend.vlc.VlcRecorder;
import es.lnsd.videotape.core.config.BackendType;
import es.lnsd.videotape.core.config.Configuration;
import javax.inject.Inject;
import org.apache.commons.lang3.NotImplementedException;

public class BackendProvider implements Provider<Backend> {

  private final Configuration configuration;

  @Inject
  private Provider<FFMpegWrapperRecorder> ffMpegWrapperRecorderProvider;
  @Inject
  private Provider<VlcRecorder> vlcRecorderProvider;
  @Inject
  private Provider<MonteRecorder> monteRecorderProvider;

  @Inject
  public BackendProvider(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Backend get() {
    BackendType type = configuration.backend();

    switch (type) {
      case MONTE:
        return monteRecorderProvider.get();
      case FFMPEG:
      case FFMPEG_WRAPPER:
        return ffMpegWrapperRecorderProvider.get();
      case VLC:
        return vlcRecorderProvider.get();
      default:
        String err = String.format("Recorder '%s' not available", type);
        throw new NotImplementedException(err);
    }
  }
}
