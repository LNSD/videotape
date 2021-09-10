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

package es.lnsd.videotape.backend.ffmpeg;

import com.github.kokorin.jaffree.OS;
import com.github.kokorin.jaffree.ffmpeg.CaptureInput;
import es.lnsd.videotape.core.exception.ConfigurationException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ScreenCaptureInput {

  public static CaptureInput<?> captureDesktop(String display) {
    CaptureInput<?> result = null;

    if (OS.IS_LINUX) {
      String pattern = "^(?<host>\\w*):?(?<display>\\d+)\\.?(?<screen>\\d+)$";
      Matcher matcher = Pattern.compile(pattern).matcher(display);
      if (!matcher.find()) {
        String err = "Invalid value format for 'video.ffmpeg.display' configuration parameter";
        throw new ConfigurationException(err);
      }

      result = CaptureInput.LinuxX11Grab.captureHostDisplayAndScreen(
          matcher.group("host"),
          Integer.parseInt(matcher.group("display")),
          Integer.parseInt(matcher.group("screen"))
      );
    }

    if (OS.IS_MAC) {
      String pattern = "^(?<video>\\w+):?(?<audio>\\w+)?$";
      Matcher matcher = Pattern.compile(pattern).matcher(display);
      if (!matcher.find()) {
        String err = "Invalid value format for 'video.ffmpeg.display' configuration parameter";
        throw new ConfigurationException(err);
      }

      result = CaptureInput.MacOsAvFoundation.captureVideoAndAudio(
          matcher.group("video"),
          Optional.ofNullable(matcher.group("audio")).orElse("none")
      );
    }

    if (OS.IS_WINDOWS) {
      result = CaptureInput.WindowsGdiGrab.captureDesktop();
    }

    return result;
  }
}
