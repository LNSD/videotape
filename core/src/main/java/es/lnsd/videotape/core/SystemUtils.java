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

package es.lnsd.videotape.core;

import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.recorder.ffmpeg.FFMpegRecorder;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;

public class SystemUtils {

  private static final Logger log = LoggerFactory.getLogger(FFMpegRecorder.class);

  public static String runCommand(final List<String> args) {
    log.info("Trying to execute the following command: {}", args);
    try {
      return new ProcessExecutor()
          .command(args)
          .readOutput(true)
          .execute()
          .outputUTF8();
    } catch (IOException | InterruptedException | TimeoutException e) {
      log.warn("Unable to execute command", e);
      throw new RecordingException(e);
    }
  }

  public static String runCommand(final String... args) {
    log.info("Trying to execute the following command: {}", Arrays.asList(args));
    try {
      return new ProcessExecutor()
          .command(args)
          .readOutput(true)
          .execute()
          .outputUTF8();
    } catch (IOException | InterruptedException | TimeoutException e) {
      log.warn("Unable to execute command", e);
      throw new RecordingException(e);
    }
  }

  public static String getPidOf(final String processName) {
    return runCommand("cmd", "/c", "for /f \"tokens=2\" %i in ('tasklist ^| findstr \"" + processName +
        "\"') do @echo %i").trim();
  }

  public static Dimension getSystemScreenDimension() {
    return Toolkit.getDefaultToolkit().getScreenSize();
  }
}
