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

import es.lnsd.videotape.core.utils.DateUtils;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import static es.lnsd.videotape.core.config.ConfigLoader.load;

@Slf4j
public class FFmpegWrapper {

  public static final String RECORDING_TOOL = "ffmpeg";
  private static final String TEM_FILE_NAME = "temporary";
  private static final String EXTENSION = ".mp4";
  private CompletableFuture<String> future;
  private File temporaryFile;

  public void startFFmpeg(String... args) {
    File videoFolder = load().folder();
    if (!videoFolder.exists()) {
      videoFolder.mkdirs();
    }

    temporaryFile = getTemporaryFile();
    final String[] commandsSequence = new String[]{
        FFmpegWrapper.RECORDING_TOOL,
        "-y",
        "-video_size", getScreenSize(),
        "-f", load().ffmpegFormat(),
        "-i", load().ffmpegDisplay(),
        "-an",
        "-framerate", String.valueOf(load().frameRate()),
        "-pix_fmt", load().ffmpegPixelFormat(),
        temporaryFile.getAbsolutePath()
    };

    List<String> command = new ArrayList<>();
    command.addAll(Arrays.asList(commandsSequence));
    command.addAll(Arrays.asList(args));
    this.future = CompletableFuture.supplyAsync(() -> WrapperUtils.runCommand(command));
  }

  public File stopFFmpegAndSave(String filename) {
    String killLog = killFFmpeg();
    log.info("Process kill output: {}", killLog);

    File destFile = getResultFile(filename);
    this.future.whenCompleteAsync((out, errors) -> {
      temporaryFile.renameTo(destFile);
      log.debug("Recording output log: {}", out + (errors != null ? "; ex: " + errors : ""));
      log.info("Recording finished to: {}", destFile.getAbsolutePath());
    });
    return destFile;
  }

  private String killFFmpeg() {
    final String SEND_CTRL_C_TOOL_NAME = "SendSignalCtrlC.exe";
    return SystemUtils.IS_OS_WINDOWS ?
        WrapperUtils.runCommand(SEND_CTRL_C_TOOL_NAME, WrapperUtils.getPidOf(RECORDING_TOOL)) :
        WrapperUtils.runCommand("pkill", "-INT", RECORDING_TOOL);
  }

  public File getTemporaryFile() {
    return getFile(TEM_FILE_NAME);
  }

  public File getResultFile(String name) {
    return getFile(name);
  }

  private File getFile(final String filename) {
    File outputFolder = load().folder();
    final String name = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return new File(outputFolder, name + EXTENSION);
  }

  private String getScreenSize() {
    Dimension dimension = load().screenSize();
    return dimension.width + "x" + dimension.height;
  }
}
