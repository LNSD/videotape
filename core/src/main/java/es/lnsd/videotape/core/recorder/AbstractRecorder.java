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

package es.lnsd.videotape.core.recorder;

import es.lnsd.videotape.core.config.VConfig;
import es.lnsd.videotape.core.exception.RecordingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Accessors(fluent = true)
public abstract class AbstractRecorder implements Recorder {

  private static final Logger log = LoggerFactory.getLogger(Recorder.class);

  private static final String TEMP_FILENAME = "screen_recording";

  @Getter
  @Setter
  private static File lastVideo;
  protected final VConfig conf;
  protected Path tempFile;
  protected Date tempFileTimestamp;

  public AbstractRecorder(VConfig conf) {
    this.conf = conf;
  }

  private Path getFilePath(String name) {
    String ext = conf.fileFormat().toLowerCase();
    return Paths.get(conf.folder().toString(), buildFileName(name, ext));
  }

  private Path getFilePath(String name, String extension) {
    return Paths.get(conf.folder().toString(), buildFileName(name, extension));
  }

  private String buildFileName(String name, String extension) {
    Date timestamp = Optional.ofNullable(tempFileTimestamp).orElse(new Date());
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss");
    return name + "_" + dateFormat.format(timestamp) + "." + extension;
  }

  @Override
  public void start() {
    // Create output folder if it does not exist
    File outputDir = conf.folder().toFile();
    if (!outputDir.exists() && !outputDir.mkdirs()) {
      log.error("Videos output folder could not be created");
    }

    this.tempFileTimestamp = new Date();
    this.tempFile = getFilePath(TEMP_FILENAME);
    startRecording(tempFile);

    log.debug("{}: Recording started", this.getClass().getName());
  }

  protected abstract void startRecording(Path tmpFile);

  @Override
  public File stopAndSave(String filename) {

    if (tempFile == null) {
      throw new RecordingException("Video recording was not started");
    }

    log.debug("{}: Stopping recorder", this.getClass().getName());
    stopRecording();

    if (!tempFile.toFile().exists()) {
      lastVideo(null);
      tempFile = null;
      return null;
    }

    String tmpFileExt = FilenameUtils.getExtension(tempFile.toString());
    Path dstFile = getFilePath(filename, tmpFileExt);
    try {
      Files.move(tempFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error("Cannot rename temporary file: {}", tempFile, e);

      lastVideo(null);
      tempFile = null;
      return null;
    }

    lastVideo(dstFile.toFile());
    tempFile = null;

    log.info("Screen recording saved to: {}", dstFile.toFile().getAbsolutePath());
    return lastVideo();
  }

  protected abstract void stopRecording();
}
