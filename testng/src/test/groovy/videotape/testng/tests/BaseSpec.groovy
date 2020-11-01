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

package videotape.testng.tests

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import es.lnsd.videotape.testng.UniversalVideoListener
import java.nio.file.Path
import java.nio.file.Paths
import javax.lang.model.element.Modifier
import org.testng.TestNG
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Listeners
import spock.lang.Specification

class BaseSpec extends Specification {

  private static final Path BUILD_DIR = Paths.get(System.getProperty("project.builddir"))
  private static final Path GENERATED_DIR = BUILD_DIR.resolve("generated/sources/test-resources/java/test")
  private static final Path CLASSES_DIR = BUILD_DIR.resolve("classes/java/test")

  protected static void runTestNGClasses(Class... testClasses) {
    TestNG testSuite = new TestNG()
    testSuite.setTestClasses(testClasses)
    testSuite.useDefaultListeners = false
    testSuite.run()
  }

  protected Class generateTestNGTestClass(String className, Closure<MethodSpec> spec) {
    String resPackage = "${this.class.getPackage().name}.resources"

    MethodSpec setup = MethodSpec.methodBuilder("setup")
            .addAnnotation(BeforeMethod)
            .addModifiers(Modifier.PUBLIC)
            .returns(void)
            .addException(InterruptedException)
            .addStatement('$T.sleep($L)', Thread, 500)
            .build()

    MethodSpec cleanup = MethodSpec.methodBuilder("cleanup")
            .addAnnotation(AnnotationSpec.builder(AfterMethod)
                    .addMember("alwaysRun", '$L', true)
                    .build())
            .addModifiers(Modifier.PUBLIC)
            .returns(void)
            .addException(InterruptedException)
            .addStatement('$T.sleep($L)', Thread, 500)
            .build()

    TypeSpec clazz = TypeSpec.classBuilder(className)
            .addAnnotation(AnnotationSpec.builder(Listeners)
                    .addMember("value", '$T.class', UniversalVideoListener)
                    .build())
            .addModifiers(Modifier.PUBLIC)
            .addMethod(setup)
            .addMethod(cleanup)
            .addMethod(spec())
            .build()

    JavaFile javaFile = JavaFile.builder(resPackage, clazz).build()

    return javaFile.compile()
  }

}