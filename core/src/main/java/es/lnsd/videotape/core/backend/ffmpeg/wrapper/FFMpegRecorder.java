/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2020-2021 Lorenzo Delgado
 *  Copyright (c) 2016 Serhii Pirohov
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package es.lnsd.videotape.core.backend.ffmpeg.wrapper;

import com.github.kokorin.jaffree.ffmpeg.CaptureInput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResultFuture;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import es.lnsd.videotape.core.backend.RecorderBackend;
import es.lnsd.videotape.core.exception.RecordingException;
import java.awt.Dimension;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
@Accessors(fluent = true)
public class FFMpegRecorder implements RecorderBackend {

  private final FFMpegConfiguration config;
  private FFmpegResultFuture future;

  public FFMpegRecorder(FFMpegConfiguration config) {
    this.config = config;
  }

  private String getScreenSize() {
    Dimension dimension = config.screenSize();
    return dimension.width + "x" + dimension.height;
  }

  @Override
  public void start(Path tmpFile) {
    CaptureInput<?> input = ScreenCaptureInput
        .captureDesktop(config.ffmpegDisplay())
        .setCaptureFrameRate(config.frameRate())
        .setCaptureCursor(true)
        .setCaptureVideoSize(getScreenSize())
        .addArgument("-an");

    if (SystemUtils.IS_OS_MAC) {
      input.setPixelFormat(config.ffmpegPixelFormat());
      input.addArguments("-vsync", "2");
    }

    future = FFmpeg.atPath(config.ffmpegBinary())
        .addInput(input)
        .addOutput(UrlOutput
            .toPath(tmpFile)
            .addArguments("-preset", "ultrafast")
        )
        .setOverwriteOutput(true)
        .executeAsync();
  }

  @Override
  public void stop() {
    future.graceStop();

    try {
      future.get();
    } catch (InterruptedException e) {
      log.warn("FFMPEG recorder subprocess has been interrupted", e);
    } catch (ExecutionException e) {
      throw new RecordingException("An error occurred during FFMPEG subprocess execution", e);
    }
  }
}