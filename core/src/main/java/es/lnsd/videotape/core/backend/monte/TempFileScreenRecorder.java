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

package es.lnsd.videotape.core.backend.monte;

import es.lnsd.videotape.core.exception.RecordingException;
import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import lombok.Builder;
import org.monte.media.Format;
import org.monte.screenrecorder.ScreenRecorder;

public class TempFileScreenRecorder extends ScreenRecorder {

  private File tempFile;

  @Builder
  public TempFileScreenRecorder(GraphicsConfiguration graphicsConfiguration,
                                Rectangle captureArea,
                                Format fileFormat,
                                Format screenFormat,
                                Format mouseFormat,
                                Format audioFormat) throws IOException, AWTException {
    super(graphicsConfiguration, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, null);
  }

  @Override
  protected File createMovieFile(Format fileFormat) {
    return tempFile;
  }

  public void startRecording(File tempFile) {
    this.tempFile = tempFile;
    try {
      super.start();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }

  public void stopRecording() {
    try {
      super.stop();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }
}
