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

package es.lnsd.videotape.core.recorder.monte;

import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.utils.DateUtils;
import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

public class MonteScreenRecorder extends ScreenRecorder {

  private String currentTempExtension;

  MonteScreenRecorder(GraphicsConfiguration cfg,
                      Format fileFormat,
                      Format screenFormat,
                      Format mouseFormat,
                      Format audioFormat,
                      File folder) throws IOException, AWTException {
    super(cfg, fileFormat, screenFormat, mouseFormat, audioFormat);
    super.movieFolder = folder;
  }

  MonteScreenRecorder(GraphicsConfiguration cfg,
                      Rectangle rectangle,
                      Format fileFormat,
                      Format screenFormat,
                      Format mouseFormat,
                      Format audioFormat,
                      File folder) throws IOException, AWTException {
    super(cfg, rectangle, fileFormat, screenFormat, mouseFormat, audioFormat);
    super.movieFolder = folder;
  }

  @Override
  protected File createMovieFile(Format fileFormat) throws IOException {
    this.currentTempExtension = Registry.getInstance().getExtension(fileFormat);
    return super.createMovieFile(fileFormat);
  }

  public File saveAs(String filename) {
    this.stop();

    File tempFile = this.getCreatedMovieFiles().get(0);
    File destFile = getDestinationFile(filename);

    tempFile.renameTo(destFile);
    return destFile;
  }

  private File getDestinationFile(String filename) {
    String fileName = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return new File(this.movieFolder + File.separator + fileName + "." + this.currentTempExtension);
  }

  @Override
  public void start() {
    try {
      super.start();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }

  @Override
  public void stop() {
    try {
      super.stop();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }
}
