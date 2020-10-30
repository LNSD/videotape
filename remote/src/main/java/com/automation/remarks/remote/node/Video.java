package com.automation.remarks.remote.node;

import com.automation.remarks.video.recorder.monte.MonteRecorder;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;

import static com.automation.remarks.remote.utils.RestUtils.updateResponse;
import static com.automation.remarks.video.RecordingUtils.doVideoProcessing;

/**
 * Created by Serhii_Pirohov on 10.05.2016.
 */
public class Video extends HttpServlet {

  private MonteRecorder videoRecorder;

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
    MonteRecorder.conf().folder();
    try {
      switch (path) {
        case "/start":
          String folder = req.getParameter("folder");
          if (folder != null) {
            System.setProperty("video.folder", folder);
          }
          videoRecorder = new MonteRecorder();
          videoRecorder.start();
          updateResponse(resp, HttpStatus.SC_OK, "recording started");
          break;
        case "/stop":
          if (videoRecorder == null) {
            updateResponse(resp, HttpStatus.SC_METHOD_NOT_ALLOWED, "Wrong Action! First, start recording");
            break;
          }
          String fileName = getFileName(req);
          File video = videoRecorder.stopAndSave(fileName);
          String filePath = doVideoProcessing(isSuccess(req), video);
          updateResponse(resp, HttpStatus.SC_OK, "recording stopped " + filePath);
          break;
        default:
          updateResponse(resp, HttpStatus.SC_NOT_FOUND,
              "Wrong Action! Method doesn't support");
      }
    } catch (Exception ex) {
      updateResponse(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
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
    return Boolean.valueOf(result);
  }
}
