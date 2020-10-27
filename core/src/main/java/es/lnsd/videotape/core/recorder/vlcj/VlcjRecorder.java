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

package es.lnsd.videotape.core.recorder.vlcj;

import es.lnsd.videotape.core.config.VideotapeConfiguration;
import es.lnsd.videotape.core.recorder.Recorder;
import es.lnsd.videotape.core.utils.DateUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

public class VlcjRecorder extends Recorder {

  private static final String[] OPTIONS = {
      "--quiet",
      "--quiet-synchro",
      "--intf",
      "dummy"
  };

  private static final String MRL = "screen://";
  private static final String SOUT = ":sout=#transcode{vcodec=h264,vb=%d}:duplicate{dst=file{dst=%s}}";
  private static final String FPS = ":screen-fps=%d";
  private static final String CACHING = ":screen-caching=%d";

  private static final int caching = 500;
  private static final int bits = 4096;
  private static final String TEM_FILE_NAME = "temporary";
  private final MediaPlayerFactory mediaPlayerFactory;
  private final MediaPlayer mediaPlayer;
  private Path temporaryFile;

  public VlcjRecorder(VideotapeConfiguration conf) {
    super(conf);
    mediaPlayerFactory = new MediaPlayerFactory(OPTIONS);
    mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
  }

  @Override
  public void start() {
    // Create destination folder if does not exist
    if (!conf.folder().isDirectory()) {
      File videoFolder = conf.folder();
      if (!videoFolder.exists()) {
        videoFolder.mkdirs();
      }
    }
    temporaryFile = getTemporaryFile();
    mediaPlayer.media().play(MRL, getMediaOptions(temporaryFile));
  }

  @Override
  public File stopAndSave(String filename) {
    mediaPlayer.controls().stop();
    mediaPlayer.release();
    mediaPlayerFactory.release();

    Path dstFile = getResultFile(filename);

    try {
      Files.move(temporaryFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }

    lastVideo(dstFile.toFile());
    return dstFile.toFile();
  }

  public Path getTemporaryFile() {
    return getFile(TEM_FILE_NAME);
  }

  public Path getResultFile(String name) {
    return getFile(name);
  }

  private Path getFile(final String filename) {
    String outputFolder = conf.folder().getAbsolutePath();
    final String name = filename + "_recording_" + DateUtils.formatDate(new Date(), "yyyy_dd_MM_HH_mm_ss");
    return Paths.get(outputFolder, name + "." + conf.fileFormat());
  }

  private String[] getMediaOptions(Path destination) {
    return new String[]{
        String.format(SOUT, bits, destination),
        String.format(FPS, conf.frameRate()),
        String.format(CACHING, caching)
    };
  }
}
