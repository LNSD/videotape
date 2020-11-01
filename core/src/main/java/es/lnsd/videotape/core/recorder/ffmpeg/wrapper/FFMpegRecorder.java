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

package es.lnsd.videotape.core.recorder.ffmpeg.wrapper;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResultFuture;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.recorder.AbstractRecorder;
import java.awt.Dimension;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(fluent = true)
public class FFMpegRecorder extends AbstractRecorder {

  private FFmpegResultFuture future;

  public FFMpegRecorder(Configuration conf) {
    super(conf);
  }

  private String getScreenSize() {
    Dimension dimension = conf.screenSize();
    return dimension.width + "x" + dimension.height;
  }

  @Override
  public void startRecording(Path temporaryFile) {
    future = FFmpeg.atPath(conf.ffmpegBinary())
        .addInput(ScreenCaptureInput
            .captureDesktop(conf.ffmpegDisplay())
            .setCaptureFrameRate(conf.frameRate())
            .setCaptureCursor(true)
            .setCaptureVideoSize(getScreenSize())
            .setPixelFormat(conf.ffmpegPixelFormat())
            .addArgument("-an")
        )
        .addOutput(UrlOutput
            .toPath(temporaryFile)
            .addArguments("-preset", "ultrafast")
        )
        .setOverwriteOutput(true)
        .executeAsync();
  }

  @Override
  protected void stopRecording() {
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
