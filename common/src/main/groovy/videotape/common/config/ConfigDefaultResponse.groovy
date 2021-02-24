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

package videotape.common.config

import java.lang.reflect.Method
import org.aeonbits.owner.Config
import org.spockframework.mock.IDefaultResponse
import org.spockframework.mock.IMockInvocation

class ConfigDefaultResponse implements IDefaultResponse {

  private Class<? extends Config> klass

  ConfigDefaultResponse(Class<? extends Config> configInterface) {
    this.klass = configInterface
  }

  @Override
  Object respond(IMockInvocation invocation) {
    String mockedMethodName = invocation.getMethod().getName()
    Method configMethod = klass.getMethod(mockedMethodName)

    def keyAnnotation = configMethod.getAnnotation(Config.Key)
    if (keyAnnotation == null) {
      return null
    }

    // Get default value
    def defaultValueAnnotation = configMethod.getAnnotation(Config.DefaultValue)
    if (defaultValueAnnotation == null) {
      return null
    }

    String defaultValue = defaultValueAnnotation.value()

    // If ConverterClass is defined, use it
    def convAnnotation = configMethod.getAnnotation(Config.ConverterClass)
    if (convAnnotation != null) {
      def converter = convAnnotation.value().getDeclaredConstructor().newInstance()
      return converter.convert(configMethod, defaultValue)
    }

    return defaultValue
  }
}
