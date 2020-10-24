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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import org.monte.media.Format;

public class MonteScreenRecorderBuilder {

  private GraphicsConfiguration cfg;
  private Format fileFormat;
  private Format screenFormat;
  private Format mouseFormat;
  private Format audioFormat;
  private File folder;
  private Rectangle rectangle;
  private String fileName;

  public static Builder builder() {
    return new MonteScreenRecorderBuilder().new Builder();
  }

  public class Builder {

    public Builder setGraphicConfig(GraphicsConfiguration cfg) {
      MonteScreenRecorderBuilder.this.cfg = cfg;
      return this;
    }

    public Builder setFileFormat(Format fileFormat) {
      MonteScreenRecorderBuilder.this.fileFormat = fileFormat;
      return this;
    }

    public Builder setScreenFormat(Format screenFormat) {
      MonteScreenRecorderBuilder.this.screenFormat = screenFormat;
      return this;
    }

    public Builder setMouseFormat(Format mouseFormat) {
      MonteScreenRecorderBuilder.this.mouseFormat = mouseFormat;
      return this;
    }

    public Builder setAudioFormat(Format audioFormat) {
      MonteScreenRecorderBuilder.this.audioFormat = audioFormat;
      return this;
    }

    public Builder setFolder(File folder) {
      MonteScreenRecorderBuilder.this.folder = folder;
      return this;
    }

    public Builder setFileName(String fileName) {
      MonteScreenRecorderBuilder.this.fileName = fileName;
      return this;
    }

    public Builder setRectangle(Rectangle rectangle) {
      MonteScreenRecorderBuilder.this.rectangle = rectangle;
      return this;
    }

    public MonteScreenRecorder build() {
      try {
        return new MonteScreenRecorder(cfg,
            rectangle,
            fileFormat,
            screenFormat,
            mouseFormat,
            audioFormat,
            folder);
      } catch (IOException | AWTException e) {
        throw new RecordingException(e);
      }
    }

  }
}
