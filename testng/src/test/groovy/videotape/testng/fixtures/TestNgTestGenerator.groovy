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

package videotape.testng.fixtures

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.lang.annotation.Annotation
import javax.lang.model.element.Modifier
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod

import static videotape.common.JavaPoetExtension.compile

class TestNgTestGenerator {

  static Class generateTestClass(String className, Closure<MethodSpec> testMethodSpec) {
    String resPackage = "${TestNgTestGenerator.class.package.name}.resources"

    TypeSpec clazz = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(configMethodSpec("setup", BeforeMethod))
            .addMethod(configMethodSpec("cleanup", AfterMethod))
            .addMethod(testMethodSpec())
            .build()

    JavaFile javaFile = JavaFile.builder(resPackage, clazz).build()

    return compile(javaFile)
  }

  static Class generateClassWithMultipleMethods(String className, Closure<List<MethodSpec>> testMethodSpec) {
    String resPackage = "${TestNgTestGenerator.class.package.name}.resources"

    TypeSpec clazz = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(configMethodSpec("setup", BeforeMethod))
            .addMethod(configMethodSpec("cleanup", AfterMethod))
            .addMethods(testMethodSpec())
            .build()

    JavaFile javaFile = JavaFile.builder(resPackage, clazz).build()

    return compile(javaFile)
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
