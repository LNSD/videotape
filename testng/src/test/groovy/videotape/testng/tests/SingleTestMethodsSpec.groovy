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
import es.lnsd.videotape.core.Video
import es.lnsd.videotape.testng.VideoListener
import javax.lang.model.element.Modifier
import org.testng.Assert
import org.testng.annotations.Test

import static videotape.testng.fixtures.TestNgRunner.runClass
import static videotape.testng.fixtures.TestNgTestGenerator.generateTestClass

class SingleTestMethodsSpec extends BaseSpec {

  def "single failing test method with @Video annotation"() {
    given:
    def testClass = generateTestClass("FailWithVideoTest") {
      MethodSpec.methodBuilder("failingTestMethod")
              .addAnnotation(Test)
              .addAnnotation(Video)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .addStatement('$T.fail()', Assert)
              .build()
    }

    when:
    runClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      1 * onTestStart("failingTestMethod", !null)
      0 * onTestSuccess()
      1 * onTestFailure()
    }
  }

  def "single passing test method with @Video annotation"() {
    given:
    def testClass = generateTestClass("SuccessTest") {
      MethodSpec.methodBuilder("passingTestMethod")
              .addAnnotation(Test)
              .addAnnotation(Video)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .build()
    }

    when:
    runClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      1 * onTestStart("passingTestMethod", !null)
      1 * onTestSuccess()
      0 * onTestFailure()
    }
  }

  def "single failing test method with @Video annotation and custom name"() {
    given:
    def testClass = generateTestClass("FailWithCustomVideoNameTest") {
      MethodSpec.methodBuilder("failingTestMethod")
              .addAnnotation(Test)
              .addAnnotation(AnnotationSpec.builder(Video)
                      .addMember("name", '$S', "custom_name")
                      .build())
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .addStatement('$T.fail()', Assert)
              .build()
    }

    when:
    runClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      1 * onTestStart("failingTestMethod", !null)
      0 * onTestSuccess()
      1 * onTestFailure()
    }
  }

  def "single failing test method with no @Video annotation"() {
    given:
    def testClass = generateTestClass("FailWithoutVideoTest") {
      MethodSpec.methodBuilder("failingTestMethod")
              .addAnnotation(Test)
              .addModifiers(Modifier.PUBLIC)
              .returns(void)
              .addStatement('$T.fail()', Assert)
              .build()
    }

    when:
    runClass(testClass, new VideoListener(adapter))
    then:
    with(adapter) {
      1 * onTestStart("failingTestMethod", null)
      0 * onTestSuccess()
      1 * onTestFailure()
    }
  }
}
