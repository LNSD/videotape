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

package es.lnsd.videotape.testng;

import es.lnsd.videotape.core.TestFrameworkAdapter;
import es.lnsd.videotape.core.Video;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class VideoListener implements ITestListener {

  private final TestFrameworkAdapter adapter;

  public VideoListener() {
    this.adapter = TestFrameworkAdapter.getInstance();
  }

  public VideoListener(TestFrameworkAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public void onTestStart(ITestResult result) {
    // Do not start recording if test case is already failed
    if (result.getThrowable() != null) {
      return;
    }

    String name = result.getMethod().getMethodName();
    Video annotation = TestNgUtils.getVideoAnnotation(result);

    adapter.onTestStart(name, annotation);
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    adapter.onTestSuccess();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    adapter.onTestFailure();
  }
}
