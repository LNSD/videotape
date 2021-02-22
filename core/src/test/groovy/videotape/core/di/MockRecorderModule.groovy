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

package videotape.core.di


import es.lnsd.videotape.core.Recorder
import es.lnsd.videotape.core.backend.RecorderBackend
import es.lnsd.videotape.core.di.RecorderModule
import es.lnsd.videotape.core.utils.FileManager
import es.lnsd.videotape.core.utils.FileNameBuilder
import spock.mock.DetachedMockFactory

class MockRecorderModule extends RecorderModule {

  @Override
  protected void configure() {
    DetachedMockFactory factory = new DetachedMockFactory()
    bind(Recorder).toInstance(factory.Mock(Recorder))
    bind(RecorderBackend).toInstance(factory.Mock(RecorderBackend))
    bind(FileManager).toInstance(factory.Mock(FileManager))
    bind(FileNameBuilder).toInstance(factory.Stub(FileNameBuilder))
  }
}
