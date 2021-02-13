/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2020-2021 Lorenzo Delgado
 *  Copyright (c) 2016 Serhii Pirohov
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package es.lnsd.videotape.core;

import es.lnsd.videotape.core.backend.RecorderBackend;
import es.lnsd.videotape.core.backend.RecorderBackendFactory;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.config.RecorderType;
import es.lnsd.videotape.core.exception.RecordingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class DefaultRecorder implements VideotapeRecorder {

  private static final String TEMP_FILENAME = "screen_recording";

  private final Configuration config;
  private final RecorderBackend recorder;

  private Path tmpFile;
  private Path dstFile;

  public DefaultRecorder(Configuration config, RecorderBackend backend) {
    this.config = config;
    this.recorder = backend;
  }

  public static VideotapeRecorder get(Configuration config) {
    RecorderType type = config.recorderType();
    RecorderBackend backend = RecorderBackendFactory.getRecorder(type);
    return new DefaultRecorder(config, backend);
  }

  public void startRecording(String name) {
    String fileFormat = config.fileFormat();
    Path dstDir = config.folder();

    try {
      FileUtils.forceMkdir(dstDir.toFile());
    } catch (IOException ex) {
      log.error("Videos output folder could not be created", ex);
      return;
    }

    this.tmpFile = getFilePath(dstDir, TEMP_FILENAME, fileFormat);
    this.dstFile = getFilePath(dstDir, name, fileFormat);

    recorder.start(tmpFile);
  }

  public void stopRecording() {
    if (this.tmpFile == null || !this.tmpFile.toFile().exists()) {
      throw new RecordingException("Video recording was not started");
    }

    recorder.stop();
  }

  public void discardRecording() {
    FileUtils.deleteQuietly(tmpFile.toFile());
  }

  public Path keepRecording() {
    File tmpFile = this.tmpFile.toFile();
    File dstFile = this.dstFile.toFile();

    if (!tmpFile.exists()) {
      return null;
    }

    try {
      FileUtils.moveFile(tmpFile, dstFile);
    } catch (IOException ex) {
      log.error("Failed renaming the temporary file", ex);
      return null;
    }

    return this.dstFile;
  }

  private Path getFilePath(Path parent, String name, String extension) {
    String fileName = buildFileName(name, extension);
    return parent.resolve(fileName);
  }

  private String getTimeStamp() {
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss");
    return dateFormat.format(now);
  }

  private String buildFileName(String name, String extension) {
    String timestamp = getTimeStamp();
    return name + "_" + timestamp + "." + extension;
  }
}
