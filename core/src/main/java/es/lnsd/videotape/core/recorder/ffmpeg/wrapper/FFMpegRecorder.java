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
import es.lnsd.videotape.core.config.VideotapeConfiguration;
import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.recorder.Recorder;
import es.lnsd.videotape.core.utils.DateUtils;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(fluent = true)
public class FFMpegRecorder extends Recorder {

  private static final String TEM_FILE_NAME = "temporary";
  private Path temporaryFile;
  private FFmpegResultFuture future;

  public FFMpegRecorder(VideotapeConfiguration conf) {
    super(conf);
  }

  public Path getTemporaryFile() {
    return getFile(TEM_FILE_NAME);
  }

  public Path getResultFile(String name) {
    return getFile(name);
  }

  private Path getFile(final String filename) {
    String outputFolder = conf.folder().getAbsolutePath();
    final String name = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return Paths.get(outputFolder, name + "." + conf.fileFormat());
  }

  private String getScreenSize() {
    Dimension dimension = conf.screenSize();
    return dimension.width + "x" + dimension.height;
  }

  @Override
  public void start() {
    // Create destination folder if does not exist
    if (!conf.folder().isDirectory()) {
      File videoFolder = conf.folder();
      if (!videoFolder.exists()) {
        videoFolder.mkdirs();
      }
    }

    temporaryFile = getTemporaryFile();
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
  public File stopAndSave(final String filename) {
    future.graceStop();

    try {
      future.get();
    } catch (InterruptedException e) {
      log.warn("FFMPEG recorder subprocess has been interrupted", e);
    } catch (ExecutionException e) {
      throw new RecordingException("An error occurred during FFMPEG subprocess execution", e);
    }

    Path dstFile = getResultFile(filename);

    try {
      Files.move(temporaryFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }

    lastVideo(dstFile.toFile());
    return dstFile.toFile();
  }
}
