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

package videotape.testng.tests

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import es.lnsd.videotape.core.config.RecordingMode
import es.lnsd.videotape.testng.VideoListener
import javax.lang.model.element.Modifier
import org.testng.Assert
import org.testng.annotations.Test

class MultipleTestMethodsTest extends BaseSpec {

  def "failing and passing test methods"() {
    given:
    System.setProperty("video.mode", RecordingMode.ALL.toString())
    def testClass = generateTestNGTestClassWithMultipleMethods("FailWithVideoNonAnnotatedTest2") {
      def failing = MethodSpec.methodBuilder("failingTestMethod")
              .addAnnotation(Test)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .addStatement('$T.fail()', Assert)
              .build()

      def passing = MethodSpec.methodBuilder("passingTestMethod")
              .addAnnotation(Test)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .build()

      return [failing, passing]
    }

    when:
    runTestNGClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      2 * onTestStart(*_)
      1 * onTestSuccess()
      1 * onTestFailure()
    }
  }

  def "failing and skipped dependent test methods"() {
    given:
    System.setProperty("video.mode", RecordingMode.ALL.toString())
    def testClass = generateTestNGTestClassWithMultipleMethods("FailAndSkipDependentMethod") {
      def failed = MethodSpec.methodBuilder("failingTestMethod")
              .addAnnotation(Test)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .addStatement('$T.fail()', Assert)
              .build()

      def skipped = MethodSpec.methodBuilder("skippedTestMethod")
              .addAnnotation(AnnotationSpec.builder(Test)
                      .addMember("dependsOnMethods", '$L', "{\"failingTestMethod\"}")
                      .build())
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .build()

      return [failed, skipped]
    }

    when:
    runTestNGClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      1 * onTestStart("failingTestMethod", _)
      0 * onTestSuccess()
      1 * onTestFailure()
    }
  }
}
