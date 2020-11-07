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

package videotape.junit.tests

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import es.lnsd.videotape.junit.VideoRule
import java.nio.file.Path
import java.nio.file.Paths
import javax.lang.model.element.Modifier
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import spock.lang.Specification

class BaseSpec extends Specification {

  private static final Path BUILD_DIR = Paths.get(System.getProperty("project.builddir"))
  private static final Path GENERATED_DIR = BUILD_DIR.resolve("generated/sources/test-resources/java/test")
  private static final Path CLASSES_DIR = BUILD_DIR.resolve("classes/java/test")

  protected static void runJUnitClasses(Class... classes) {
    JUnitCore junit = new JUnitCore()
    junit.addListener(new TextListener(System.out))
    junit.run(classes)
  }

  protected Class generateJUnitTestClass(String className, Closure<MethodSpec> spec) {
    String resPackage = "${this.class.getPackage().name}.resources"

    FieldSpec videoRule = FieldSpec.builder(VideoRule, "videoRule")
            .addAnnotation(Rule)
            .addModifiers(Modifier.PUBLIC)
            .initializer('new $T()', VideoRule)
            .build()

    MethodSpec setup = MethodSpec.methodBuilder("setup")
            .addAnnotation(Before)
            .addModifiers(Modifier.PUBLIC)
            .returns(void)
            .addException(InterruptedException)
            .addStatement('$T.sleep($L)', Thread, 500)
            .build()

    MethodSpec cleanup = MethodSpec.methodBuilder("cleanup")
            .addAnnotation(After)
            .addModifiers(Modifier.PUBLIC)
            .returns(void)
            .addException(InterruptedException)
            .addStatement('$T.sleep($L)', Thread, 500)
            .build()

    TypeSpec clazz = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addField(videoRule)
            .addMethod(setup)
            .addMethod(cleanup)
            .addMethod(spec())
            .build()

    JavaFile javaFile = JavaFile.builder(resPackage, clazz).build()

    return javaFile.compile()
  }
}