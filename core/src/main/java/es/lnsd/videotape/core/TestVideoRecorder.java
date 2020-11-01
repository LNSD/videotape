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

import es.lnsd.videotape.core.annotations.Video;
import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.recorder.Recorder;
import es.lnsd.videotape.core.recorder.RecorderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TestVideoRecorder {

  private final Configuration conf;
  @Getter(lazy = true)
  private final Recorder recorder = RecorderFactory.getRecorder(conf);
  @Getter
  private boolean recordingStarted = false;

  private TestVideoRecorder() {
    this.conf = ConfigLoader.load();
  }

  public static TestVideoRecorder get() {
    return new TestVideoRecorder();
  }

  public boolean isVideoEnabled(Video annotation) {
    return conf.videoEnabled() && (conf.isRecordingAllModeEnabled() || annotation != null);
  }

  public void startRecording(Video annotation) {
    if (!isVideoEnabled(annotation)) return;

    getRecorder().start();
    this.recordingStarted = true;
  }

  public String stopAndSaveRecording(String filename, boolean successfulTest) {
    if (!recordingStarted) return "";

    File recording = getRecorder().stopAndSave(filename);

    String filePath = Optional.ofNullable(recording).map(File::getAbsolutePath).orElse("");

    if (!successfulTest || conf.isSaveAllStrategyEnabled()) {
      log.info("Video recording: {}", filePath);
      return filePath;
    } else if (recording != null && recording.isFile()) {
      try {
        Files.deleteIfExists(recording.toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
      log.info("No video on success test");
    }
    return "";
  }
}
