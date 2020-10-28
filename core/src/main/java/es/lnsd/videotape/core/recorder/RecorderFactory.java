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

package es.lnsd.videotape.core.recorder;

import es.lnsd.videotape.core.config.RecorderType;
import es.lnsd.videotape.core.config.VideotapeConfiguration;
import es.lnsd.videotape.core.recorder.ffmpeg.wrapper.FFMpegRecorder;
import es.lnsd.videotape.core.recorder.monte.MonteRecorder;
import es.lnsd.videotape.core.recorder.vlc.VlcRecorder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecorderFactory {

  public static IRecorder getRecorder(VideotapeConfiguration conf) {
    return getRecorder(conf.recorderType(), conf);
  }

  public static IRecorder getRecorder(RecorderType recorderType, VideotapeConfiguration conf) {

    if (recorderType == RecorderType.FFMPEG || recorderType == RecorderType.FFMPEG_WRAPPER) {
      return new FFMpegRecorder(conf);
    }

    if (recorderType == RecorderType.FFMPEG_LEGACY) {
      return new es.lnsd.videotape.core.recorder.ffmpeg.legacy.FFMpegRecorder(conf);
    }

    if (recorderType == RecorderType.VLC) {
      return new VlcRecorder(conf);
    }

    if (recorderType == RecorderType.MONTE) {
      return new MonteRecorder(conf);
    }

    String err = String.format("Recorder '%s' not available", recorderType);
    throw new NotImplementedException(err);
  }
}
