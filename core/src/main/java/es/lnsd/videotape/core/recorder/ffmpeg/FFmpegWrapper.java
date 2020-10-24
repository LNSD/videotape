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

package es.lnsd.videotape.core.recorder.ffmpeg;

import es.lnsd.videotape.core.DateUtils;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static es.lnsd.videotape.core.recorder.VideoRecorder.conf;

public class FFmpegWrapper {

  public static final String RECORDING_TOOL = "ffmpeg";
  private static final Logger log = LoggerFactory.getLogger(FFMpegRecorder.class);
  private static final String TEM_FILE_NAME = "temporary";
  private static final String EXTENSION = ".mp4";
  private CompletableFuture<String> future;
  private File temporaryFile;

  public void startFFmpeg(String... args) {
    File videoFolder = conf().folder();
    if (!videoFolder.exists()) {
      videoFolder.mkdirs();
    }

    temporaryFile = getTemporaryFile();
    final String[] commandsSequence = new String[]{
        FFmpegWrapper.RECORDING_TOOL,
        "-y",
        "-video_size", getScreenSize(),
        "-f", conf().ffmpegFormat(),
        "-i", conf().ffmpegDisplay(),
        "-an",
        "-framerate", String.valueOf(conf().frameRate()),
        "-pix_fmt", conf().ffmpegPixelFormat(),
        temporaryFile.getAbsolutePath()
    };

    List<String> command = new ArrayList<>();
    command.addAll(Arrays.asList(commandsSequence));
    command.addAll(Arrays.asList(args));
    this.future = CompletableFuture.supplyAsync(() -> es.lnsd.videotape.core.SystemUtils.runCommand(command));
  }

  public File stopFFmpegAndSave(String filename) {
    String killLog = killFFmpeg();
    log.info("Process kill output: " + killLog);

    File destFile = getResultFile(filename);
    this.future.whenCompleteAsync((out, errors) -> {
      temporaryFile.renameTo(destFile);
      log.debug("Recording output log: " + out + (errors != null ? "; ex: " + errors : ""));
      log.info("Recording finished to: " + destFile.getAbsolutePath());
    });
    return destFile;
  }

  private String killFFmpeg() {
    final String SEND_CTRL_C_TOOL_NAME = "SendSignalCtrlC.exe";
    return SystemUtils.IS_OS_WINDOWS ?
        es.lnsd.videotape.core.SystemUtils.runCommand(SEND_CTRL_C_TOOL_NAME, es.lnsd.videotape.core.SystemUtils.getPidOf(RECORDING_TOOL)) :
        es.lnsd.videotape.core.SystemUtils.runCommand("pkill", "-INT", RECORDING_TOOL);
  }

  public File getTemporaryFile() {
    return getFile(TEM_FILE_NAME);
  }

  public File getResultFile(String name) {
    return getFile(name);
  }

  private File getFile(final String filename) {
    File outputFolder = conf().folder();
    final String name = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return new File(outputFolder, name + EXTENSION);
  }

  private String getScreenSize() {
    Dimension dimension = conf().screenSize();
    return dimension.width + "x" + dimension.height;
  }
}
