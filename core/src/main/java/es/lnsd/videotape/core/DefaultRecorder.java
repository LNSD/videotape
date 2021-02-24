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

package es.lnsd.videotape.core;

import es.lnsd.videotape.core.backend.Backend;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.exception.RecordingException;
import es.lnsd.videotape.core.utils.FileManager;
import es.lnsd.videotape.core.utils.FileNameBuilder;
import java.io.IOException;
import java.nio.file.Path;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRecorder implements Recorder {

  private static final String TEMP_FILENAME = "screen_recording";

  private final Configuration config;
  private final Backend recorder;
  private final FileManager fileManager;
  private final FileNameBuilder fileNameBuilder;

  private Path tmpFilePath;
  private Path dstFilePath;

  @Inject
  public DefaultRecorder(Configuration config, Backend backend, FileManager fileManager, FileNameBuilder fileNameBuilder) {
    this.config = config;
    this.recorder = backend;
    this.fileManager = fileManager;
    this.fileNameBuilder = fileNameBuilder;
  }

  public void startRecording(String name) {
    Path dstDir = config.output();

    try {
      fileManager.createDestinationDir(dstDir);
    } catch (IOException ex) {
      log.error("Output output could not be created", ex);
      return;
    }

    String extension = config.fileFormat();
    this.tmpFilePath = getOutputFile(dstDir, TEMP_FILENAME, extension);
    this.dstFilePath = getOutputFile(dstDir, name, extension);

    recorder.start(tmpFilePath);
  }

  public void stopRecording() {
    if (!recorder.isRecording()) {
      throw new RecordingException("Video recording was not started");
    }

    recorder.stop();
  }

  public void discardRecording() {
    try {
      fileManager.discardFile(tmpFilePath);
    } catch (IOException ex) {
      log.error("Temporary file could not be discarded");
    }
  }

  public Path keepRecording() {
    try {
      fileManager.renameFile(tmpFilePath, dstFilePath);
    } catch (IOException ex) {
      log.error("Temporary file could not be renamed", ex);
      return null;
    }

    return this.dstFilePath;
  }

  private Path getOutputFile(Path parent, String name, String extension) {
    String fileName = fileNameBuilder.fileNameWithTimestamp(name, extension);
    return parent.resolve(fileName);
  }
}
