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

package es.lnsd.videotape.testng;

import es.lnsd.videotape.core.recorder.VideoRecorder;
import es.lnsd.videotape.testng.utils.RestUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class RemoteVideoClient {

  private final String servletUrl;

  public RemoteVideoClient(String nodeUrl) {
    String servletPath = "/extra/Video";
    this.servletUrl = nodeUrl + servletPath;
  }

  public void videoStart() {
    String folderUrl = encodeFilePath(VideoRecorder.conf().folder());
    String url = servletUrl + "/start?&folder=" + folderUrl;
    RestUtils.sendRecordingRequest(url);
  }

  public void videoStop(String testName, boolean isSuccess) {
    String url = servletUrl + "/stop?result=" + isSuccess + "&name=" + testName;
    RestUtils.sendRecordingRequest(url);
  }

  private String encodeFilePath(File file) {
    URL videoFolder = null;
    try {
      videoFolder = file.toURI().toURL();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return videoFolder != null ? videoFolder.toString().replace("file:", "") : null;
  }
}
