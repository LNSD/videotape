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

package es.lnsd.videotape.testng;

import es.lnsd.videotape.core.RecorderFactory;
import es.lnsd.videotape.core.recorder.IVideoRecorder;
import es.lnsd.videotape.core.recorder.VideoRecorder;
import es.lnsd.videotape.testng.utils.MethodUtils;
import java.io.File;
import java.util.List;
import org.testng.ITestResult;

import static es.lnsd.videotape.core.RecordingUtils.doVideoProcessing;
import static es.lnsd.videotape.core.RecordingUtils.videoEnabled;
import static es.lnsd.videotape.testng.utils.ListenerUtils.getFileName;


public class VideoListener extends TestNgListener {

  private IVideoRecorder recorder;

  @Override
  public void onTestStart(ITestResult result) {
    if (videoDisabled(result) || !shouldIntercept(result)) {
      return;
    }
    recorder = RecorderFactory.getRecorder(VideoRecorder.conf().recorderType());
    recorder.start();
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    String fileName = getFileName(result);
    File video = stopRecording(fileName);
    doVideoProcessing(true, video);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    if (videoDisabled(result) || !shouldIntercept(result)) {
      return;
    }
    String fileName = getFileName(result);
    File file = stopRecording(fileName);
    doVideoProcessing(false, file);
  }

  private boolean videoDisabled(ITestResult result) {
    return !videoEnabled(MethodUtils.getVideoAnnotation(result));
  }

  public boolean shouldIntercept(ITestResult result) {
    List<String> listeners = result.getTestContext().getCurrentXmlTest().getSuite().getListeners();
    return listeners.contains(this.getClass().getName()) || shouldIntercept(result.getTestClass().getRealClass(), this.getClass());
  }

  private File stopRecording(String filename) {
    if (recorder != null) {
      return recorder.stopAndSave(filename);
    }
    return null;
  }
}
