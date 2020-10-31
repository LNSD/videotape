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

package es.lnsd.videotape.junit;

import es.lnsd.videotape.core.RecordingUtils;
import es.lnsd.videotape.core.annotations.Video;
import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.recorder.Recorder;
import es.lnsd.videotape.core.recorder.RecorderFactory;
import java.io.File;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static es.lnsd.videotape.core.RecordingUtils.doVideoProcessing;
import static es.lnsd.videotape.core.RecordingUtils.videoEnabled;

public class VideoRule extends TestWatcher {

  private Recorder recorder;

  @Override
  protected void starting(Description description) {
    if (videoDisabled(description)) {
      return;
    }

    recorder = RecorderFactory.getRecorder(ConfigLoader.load());
    recorder.start();
  }

  @Override
  protected void succeeded(Description description) {
    String fileName = getFileName(description);
    File video = stopRecording(fileName);
    doVideoProcessing(true, video);
  }

  @Override
  protected void failed(Throwable e, Description description) {
    if (videoDisabled(description)) {
      return;
    }
    String fileName = getFileName(description);
    File file = stopRecording(fileName);
    doVideoProcessing(false, file);
  }

  @Override
  protected void skipped(AssumptionViolatedException e, Description description) {
    failed(e, description);
  }

  private boolean videoDisabled(Description description) {
    return !videoEnabled(description.getAnnotation(Video.class));
  }

  protected String getFileName(Description description) {
    String methodName = description.getMethodName();
    Video video = description.getAnnotation(Video.class);
    return RecordingUtils.getVideoFileName(video, methodName);
  }

  private File stopRecording(String filename) {
    if (recorder == null) {
      return null;
    }
    return recorder.stopAndSave(filename);
  }
}
