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

package es.lnsd.videotape.junit4;

import es.lnsd.videotape.core.TestFrameworkAdapter;
import es.lnsd.videotape.core.Video;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class VideoRule extends TestWatcher {

  private TestFrameworkAdapter recorder;

  @Override
  protected void starting(Description description) {
    String fileName = description.getMethodName();
    Video annotation = description.getAnnotation(Video.class);

    this.recorder = TestFrameworkAdapter.getInstance();
    recorder.onTestStart(fileName, annotation);
  }

  @Override
  protected void succeeded(Description description) {
    recorder.onTestSuccess();
  }

  @Override
  protected void failed(Throwable e, Description description) {
    recorder.onTestFailure();
  }

  @Override
  protected void skipped(AssumptionViolatedException e, Description description) {
    failed(e, description);
  }
}
