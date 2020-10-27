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

package es.lnsd.videotape.core;

import es.lnsd.videotape.core.annotations.Video;
import es.lnsd.videotape.core.config.ConfigLoader;
import es.lnsd.videotape.core.config.RecordingMode;
import es.lnsd.videotape.core.config.SavingStrategy;
import java.io.File;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecordingUtils {

  public static String doVideoProcessing(boolean successfulTest, File video) {
    String filePath = formatVideoFilePath(video);
    if (!successfulTest || isSaveAllModeEnable()) {
      log.info("Video recording: {}", filePath);
      return filePath;
    } else if (video != null && video.isFile()) {
      if (!video.delete()) {
        log.info("Video didn't deleted");
        return "Video didn't deleted";
      }
      log.info("No video on success test");
    }
    return "";
  }

  private static boolean isSaveAllModeEnable() {
    return ConfigLoader.load().saveStrategy().equals(SavingStrategy.ALL);
  }

  public static boolean videoEnabled(Video video) {
    return ConfigLoader.load().videoEnabled()
        && (isRecordingAllModeEnable() || video != null);
  }

  private static boolean isRecordingAllModeEnable() {
    return ConfigLoader.load().mode().equals(RecordingMode.ALL);
  }

  public static String getVideoFileName(Video annotation, String methodName) {
    if (useNameFromVideoAnnotation(annotation) && ConfigLoader.load().fileName() != null) {
      return ConfigLoader.load().fileName();
    } else if (annotation == null) {
      return methodName;
    }
    String name = annotation.name();
    return name.length() > 1 ? name : methodName;
  }

  private static boolean useNameFromVideoAnnotation(Video annotation) {
    return annotation == null || annotation.name().isEmpty();
  }

  // TODO Review this
  private static String formatVideoFilePath(File video) {
    if (video == null) {
      return "";
    }
    String jenkinsReportsUrl = getJenkinsReportsUrl();
    if (!isBlank(jenkinsReportsUrl)) {
      return jenkinsReportsUrl + video.getName();
    }
    return video.getAbsolutePath();
  }

  // TODO Review this
  private static String getJenkinsReportsUrl() {
    String jenkinsUrl = System.getProperty("jenkinsUrl");
    if (!isBlank(jenkinsUrl)) {
      return jenkinsUrl;
    } else {
      log.info("No jenkinsUrl variable found.");
      return "";
    }
  }
}
