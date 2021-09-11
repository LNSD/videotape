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

package videotape.testng.inttests

import java.nio.file.Path
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class BaseSpec extends Specification {

  private static final Path BUILD_DIR = Paths.get(System.getProperty("project.builddir"))
  private static final Path GENERATED_DIR = BUILD_DIR.resolve("generated/sources/test-resources/java/test")
  private static final Path CLASSES_DIR = BUILD_DIR.resolve("classes/java/test")

  @Shared
  File OUTPUT_DIR = Paths.get(System.getProperty("project.test.resultsdir"), "/video").toFile()

  def setupSpec() {
    FileUtils.deleteDirectory(OUTPUT_DIR)
  }

  def setup() {
    System.setProperty("user.dir", System.getProperty("project.test.resultsdir"))
  }

  def cleanup() {
    FileUtils.deleteDirectory(OUTPUT_DIR)
  }
}