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

package es.lnsd.videotape.core.recorder.vlc;

import es.lnsd.videotape.core.config.Configuration;
import es.lnsd.videotape.core.recorder.AbstractRecorder;
import java.nio.file.Path;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

public class VlcRecorder extends AbstractRecorder {

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
  private MediaPlayerFactory mediaPlayerFactory;
  private MediaPlayer mediaPlayer;

  public VlcRecorder(Configuration conf) {
    super(conf);
  }

  @Override
  public void startRecording(Path temporaryFile) {
    mediaPlayerFactory = new MediaPlayerFactory(OPTIONS);
    mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();

    mediaPlayer.media().play(MRL, getMediaOptions(temporaryFile));
  }

  @Override
  protected void stopRecording() {
    mediaPlayer.controls().stop();
    mediaPlayer.release();
    mediaPlayerFactory.release();
  }

  private String[] getMediaOptions(Path destination) {
    return new String[]{
        String.format(SOUT, bits, destination),
        String.format(FPS, conf.frameRate()),
        String.format(CACHING, caching)
    };
  }
}
