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

package es.lnsd.videotape.junit5;

import es.lnsd.videotape.core.recorder.IRecorder;
import es.lnsd.videotape.core.recorder.Recorder;
import es.lnsd.videotape.core.recorder.RecorderFactory;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;

import static es.lnsd.videotape.core.RecordingUtils.doVideoProcessing;
import static es.lnsd.videotape.core.RecordingUtils.videoEnabled;

public class VideoExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private IRecorder recorder;

  private static String getVideoFileName(Video annotation, String methodName) {
    if (annotation == null) {
      return methodName;
    }
    String name = annotation.name();
    return name.length() > 1 ? name : methodName;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    if (videoDisabled(context.getTestMethod().get())) {
      return;
    }
    recorder = RecorderFactory.getRecorder(Recorder.conf().recorderType());
    recorder.start();
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    if (videoDisabled(context.getTestMethod().get())) {
      return;
    }

    String fileName = getFileName(context.getTestMethod().get());
    File video = stopRecording(fileName);
    doVideoProcessing(!context.getExecutionException().isPresent(), video);
  }

  private boolean videoDisabled(Method testMethod) {
    Optional<es.lnsd.videotape.core.annotations.Video> video = AnnotationUtils.findAnnotation(testMethod, es.lnsd.videotape.core.annotations.Video.class);

    return video.map(v -> !videoEnabled(v))
        .orElseGet(() -> true);

    //return !video.isPresent() && !videoEnabled(video.get());
  }

  private String getFileName(Method testMethod) {
    String methodName = testMethod.getName();
    Video video = testMethod.getAnnotation(Video.class);
    return getVideoFileName(video, methodName);
  }

  private File stopRecording(String filename) {
    if (recorder != null) {
      return recorder.stopAndSave(filename);
    }
    return null;
  }
}
