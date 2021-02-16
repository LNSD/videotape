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

package videotape.testng.inttests

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.testng.ITestNGListener
import org.testng.TestNG
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import spock.lang.Specification

import javax.lang.model.element.Modifier
import java.lang.annotation.Annotation
import java.nio.file.Path
import java.nio.file.Paths

class BaseSpec extends Specification {

  private static final Path BUILD_DIR = Paths.get(System.getProperty("project.builddir"))
  private static final Path GENERATED_DIR = BUILD_DIR.resolve("generated/sources/test-resources/java/test")
  private static final Path CLASSES_DIR = BUILD_DIR.resolve("classes/java/test")

  protected static void runTestNGClass(Class testClass, ITestNGListener... listeners = []) {
    TestNG testSuite = new TestNG()
    testSuite.setUseDefaultListeners(false)
    for (listener in listeners) {
      testSuite.addListener(listener)
    }
    testSuite.setTestClasses(testClass)
    testSuite.run()
  }

  protected Class generateTestNGTestClass(String className, Closure<MethodSpec> testMethodSpec) {
    String resPackage = "${this.class.getPackage().name}.resources"

    TypeSpec clazz = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC)
        .addMethod(configMethodSpec("setup", BeforeMethod))
        .addMethod(configMethodSpec("cleanup", AfterMethod))
        .addMethod(testMethodSpec())
        .build()

    JavaFile javaFile = JavaFile.builder(resPackage, clazz).build()

    return javaFile.compile(GENERATED_DIR.toFile(), CLASSES_DIR.toFile())
  }

  private static MethodSpec configMethodSpec(String name, Class<? extends Annotation> annotation) {
    return MethodSpec.methodBuilder(name)
        .addAnnotation(AnnotationSpec.builder(annotation)
            .addMember("alwaysRun", '$L', true)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .returns(void)
        .addException(InterruptedException)
        .addStatement('$T.sleep($L)', Thread, 500)
        .build()
  }
}