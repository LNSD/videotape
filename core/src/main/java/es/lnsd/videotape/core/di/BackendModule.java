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

package es.lnsd.videotape.core.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import es.lnsd.videotape.core.backend.Backend;
import es.lnsd.videotape.core.backend.BackendConfiguration;
import es.lnsd.videotape.core.backend.BackendProvider;
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegConfiguration;
import es.lnsd.videotape.core.backend.ffmpeg.wrapper.FFMpegWrapperRecorder;
import es.lnsd.videotape.core.backend.monte.MonteConfiguration;
import es.lnsd.videotape.core.backend.monte.MonteRecorder;
import es.lnsd.videotape.core.backend.vlc.VlcRecorder;

public class BackendModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Backend.class).toProvider(BackendProvider.class);
  }

  @Provides
  private MonteRecorder provideMonteBackend(MonteConfiguration configuration) {
    return new MonteRecorder(configuration);
  }

  @Provides
  private FFMpegWrapperRecorder provideFFMpegWrapperBackend(FFMpegConfiguration configuration) {
    return new FFMpegWrapperRecorder(configuration);
  }

  @Provides
  private VlcRecorder provideVlcBackend(BackendConfiguration configuration) {
    return new VlcRecorder(configuration);
  }
}
