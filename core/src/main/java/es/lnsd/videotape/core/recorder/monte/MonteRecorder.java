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

import es.lnsd.videotape.core.config.VideotapeConfiguration;
import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.recorder.Recorder;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MediaType;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

@Slf4j
public class MonteRecorder extends Recorder {

  private final MonteScreenRecorder screenRecorder;

  public MonteRecorder(VideotapeConfiguration conf) {
    super(conf);
    this.screenRecorder = getScreenRecorder();
  }

  public void start() {
    screenRecorder.start();
    log.info("Recording started");
  }

  public File stopAndSave(String filename) {
    File video = writeVideo(filename);
    lastVideo(video);
    log.info("Recording finished to {}", video.getAbsolutePath());
    return video;
  }

  private File writeVideo(String filename) {
    try {
      return screenRecorder.saveAs(filename);
    } catch (IndexOutOfBoundsException ex) {
      throw new RecordingException("Video recording was not started");
    }
  }

  private GraphicsConfiguration getGraphicConfig() {
    return GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration();
  }

  private MonteScreenRecorder getScreenRecorder() {
    int frameRate = conf.frameRate();

    Format fileFormat = new Format(
        MediaTypeKey, MediaType.VIDEO,
        MimeTypeKey, FormatKeys.MIME_AVI
    );
    Format screenFormat = new Format(
        MediaTypeKey, MediaType.VIDEO,
        EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
        DepthKey, 24,
        FrameRateKey, Rational.valueOf(frameRate),
        QualityKey, 1.0f,
        KeyFrameIntervalKey, 15 * 60
    );
    Format mouseFormat = new Format(
        MediaTypeKey, MediaType.VIDEO,
        EncodingKey, "black",
        FrameRateKey, Rational.valueOf(frameRate)
    );

    Dimension screenSize = conf.screenSize();
    Rectangle captureSize = new Rectangle(0, 0, screenSize.width, screenSize.height);

    try {
      return MonteScreenRecorder.builder()
          .graphicsConfiguration(getGraphicConfig())
          .captureArea(captureSize)
          .fileFormat(fileFormat)
          .screenFormat(screenFormat)
          .mouseFormat(mouseFormat)
          .outputFolder(conf.folder())
          .build();
    } catch (IOException | AWTException e) {
      throw new RecordingException(e);
    }
  }
}
