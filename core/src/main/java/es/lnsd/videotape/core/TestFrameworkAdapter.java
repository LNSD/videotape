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

import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.exception.RecordingException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TestFrameworkAdapter {

  private final Configuration config;
  private final VideotapeRecorder recorder;

  public TestFrameworkAdapter(Configuration config) {
    this.config = config;
    this.recorder = DefaultRecorder.get(config);
  }

  public TestFrameworkAdapter() {
    this(ConfigLoader.load());
  }

  private static String getVideoName(Video video, String testName) {
    return Optional.ofNullable(video)
        .map(Video::name)
        .filter(StringUtils::isNotBlank)
        .orElse(testName);
  }

  public void onTestStart(String name, Video annotation) {
    if (!shouldRecord(annotation)) {
      return;
    }

    String fName = getVideoName(annotation, name);
    recorder.startRecording(fName);
  }

  public void onTestSuccess() {
    try {
      recorder.stopRecording();
    } catch (RecordingException ex) {
      log.error("Impossible to stop recording", ex);
      return;
    }

    if (config.isKeepAllStrategyEnabled()) {
      recorder.keepRecording();
    } else {
      recorder.discardRecording();
    }
  }

  public void onTestFailure() {
    try {
      recorder.stopRecording();
    } catch (RecordingException ex) {
      log.error("Impossible to stop recording", ex);
      return;
    }

    recorder.keepRecording();
  }

  private boolean shouldRecord(Video annotation) {
    if (config.videoDisabled()) {
      return false;
    }

    if (config.isRecordingAllModeEnabled()) {
      return true;
    }

    if (annotation == null) {
      return false;
    }

    return annotation.enable();
  }
}
