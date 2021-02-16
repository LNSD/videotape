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
 *
 */

package es.lnsd.videotape.junit5;

import es.lnsd.videotape.core.TestFrameworkAdapter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class VideoExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private TestFrameworkAdapter recorder;

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    if (!context.getTestMethod().isPresent()) {
      return;
    }

    Method method = context.getTestMethod().get();
    String fileName = method.getName();
    Video video = method.getAnnotation(Video.class);

    this.recorder = new TestFrameworkAdapter();
    recorder.onTestStart(fileName, convertToVideoAnnotation(video));
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    if (!context.getTestMethod().isPresent()) {
      return;
    }

    boolean success = !context.getExecutionException().isPresent();
    recorder.afterTestExecution(success);
  }

  private es.lnsd.videotape.core.Video convertToVideoAnnotation(Video video) {
    return new es.lnsd.videotape.core.Video() {

      private final Video delegate = video;

      @Override
      public boolean enable() {
        return delegate.enable();
      }

      @Override
      public String name() {
        return delegate.name();
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return es.lnsd.videotape.core.Video.class;
      }
    };
  }
}
