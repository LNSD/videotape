package com.automation.remarks.video.recorder.monte;

import com.automation.remarks.video.DateUtils;
import com.automation.remarks.video.exception.RecordingException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

/**
 * Created by sergey on 13.04.16.
 */
public class MonteScreenRecorder extends ScreenRecorder {

  private String currentTempExtension;

  MonteScreenRecorder(GraphicsConfiguration cfg,
                      Format fileFormat,
                      Format screenFormat,
                      Format mouseFormat,
                      Format audioFormat,
                      File folder) throws IOException, AWTException {
    super(cfg, fileFormat, screenFormat, mouseFormat, audioFormat);
    super.movieFolder = folder;
  }

  MonteScreenRecorder(GraphicsConfiguration cfg,
                      Rectangle rectangle,
                      Format fileFormat,
                      Format screenFormat,
                      Format mouseFormat,
                      Format audioFormat,
                      File folder) throws IOException, AWTException {
    super(cfg, rectangle, fileFormat, screenFormat, mouseFormat, audioFormat);
    super.movieFolder = folder;
  }

  @Override
  protected File createMovieFile(Format fileFormat) throws IOException {
    this.currentTempExtension = Registry.getInstance().getExtension(fileFormat);
    return super.createMovieFile(fileFormat);
  }

  public File saveAs(String filename) {
    this.stop();

    File tempFile = this.getCreatedMovieFiles().get(0);
    File destFile = getDestinationFile(filename);

    tempFile.renameTo(destFile);
    return destFile;
  }

  private File getDestinationFile(String filename) {
    String fileName = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return new File(this.movieFolder + File.separator + fileName + "." + this.currentTempExtension);
  }

  @Override
  public void start() {
    try {
      super.start();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }

  @Override
  public void stop() {
    try {
      super.stop();
    } catch (IOException e) {
      throw new RecordingException(e);
    }
  }
}
