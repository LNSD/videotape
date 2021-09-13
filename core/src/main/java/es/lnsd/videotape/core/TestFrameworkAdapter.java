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

import com.google.inject.Guice;
import com.google.inject.Injector;
import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.config.KeepStrategy;
import es.lnsd.videotape.core.config.RecordingMode;
import es.lnsd.videotape.core.di.ConfigurationModule;
import es.lnsd.videotape.core.di.RecorderModule;
import es.lnsd.videotape.core.exception.RecordingException;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TestFrameworkAdapter {

  private final Configuration config;
  private final Recorder recorder;

  @Inject
  public TestFrameworkAdapter(Configuration config, Recorder recorder) {
    this.config = config;
    this.recorder = recorder;
  }

  public static TestFrameworkAdapter getInstance() {
    Injector injector = Guice.createInjector(
        new ConfigurationModule(),
        new RecorderModule()
    );

    return new TestFrameworkAdapter(
        injector.getInstance(Configuration.class),
        injector.getInstance(Recorder.class)
    );
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

    String filename = getVideoName(annotation, name);
    recorder.startRecording(filename);
  }

  public void onTestSuccess() {
    try {
      recorder.stopRecording();
    } catch (RecordingException ex) {
      return;
    }

    if (config.keepStrategy().equals(KeepStrategy.ALL)) {
      recorder.keepRecording();
    } else {
      recorder.discardRecording();
    }
  }

  public void onTestFailure() {
    try {
      recorder.stopRecording();
    } catch (RecordingException ex) {
      return;
    }

    recorder.keepRecording();
  }

  public void afterTestExecution(boolean result) {
    if (result) {
      onTestSuccess();
    } else {
      onTestFailure();
    }
  }

  private boolean shouldRecord(Video annotation) {
    if (config.videoEnabled().equals(false)) {
      return false;
    }

    if (config.mode().equals(RecordingMode.ALL)) {
      if (annotation != null) {
        return annotation.enable();
      }
      return true;
    }

    // mode = RecordingMode.ANNOTATED
    return annotation != null && annotation.enable();
  }
}
