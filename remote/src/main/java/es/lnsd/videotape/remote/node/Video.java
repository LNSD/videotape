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

package es.lnsd.videotape.remote.node;

import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.RecorderType;
import es.lnsd.videotape.core.recorder.IRecorder;
import es.lnsd.videotape.core.recorder.RecorderFactory;
import es.lnsd.videotape.remote.utils.RestUtils;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;

import static es.lnsd.videotape.core.RecordingUtils.doVideoProcessing;

public class Video extends HttpServlet {

  private IRecorder videoRecorder;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    process(req, resp);
  }

  private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String path = req.getPathInfo();

    try {
      switch (path) {
        case "/start":
          String folder = req.getParameter("folder");
          if (folder != null) {
            System.setProperty("video.folder", folder);
          }
          videoRecorder = RecorderFactory.getRecorder(RecorderType.MONTE, ConfigLoader.load());
          videoRecorder.start();
          RestUtils.updateResponse(resp, HttpStatus.SC_OK, "recording started");
          break;
        case "/stop":
          if (videoRecorder == null) {
            RestUtils.updateResponse(resp, HttpStatus.SC_METHOD_NOT_ALLOWED, "Wrong Action! First, start recording");
            break;
          }
          String fileName = getFileName(req);
          File video = videoRecorder.stopAndSave(fileName);
          String filePath = doVideoProcessing(isSuccess(req), video);
          RestUtils.updateResponse(resp, HttpStatus.SC_OK, "recording stopped " + filePath);
          break;
        default:
          RestUtils.updateResponse(resp, HttpStatus.SC_NOT_FOUND,
              "Wrong Action! Method not supported");
      }
    } catch (Exception ex) {
      RestUtils.updateResponse(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
          "Internal server error occurred while trying to start / stop recording: " + ex);
    }
  }

  private String getFileName(HttpServletRequest req) {
    String name = req.getParameter("name");
    if (name == null || "null".equalsIgnoreCase(name)) {
      return "video";
    }
    return name;
  }

  private boolean isSuccess(HttpServletRequest req) {
    String result = req.getParameter("result");
    if (result == null) {
      return false;
    }
    return Boolean.parseBoolean(result);
  }
}
