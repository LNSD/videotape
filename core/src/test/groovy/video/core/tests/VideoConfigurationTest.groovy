package video.core.tests

import com.automation.remarks.video.SystemUtils
import com.automation.remarks.video.enums.RecorderType
import com.automation.remarks.video.enums.RecordingMode
import com.automation.remarks.video.enums.VideoSaveMode
import com.automation.remarks.video.recorder.VideoRecorder
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

/**
 * Created by sergey on 09.10.16.
 */
@Unroll
class VideoConfigurationTest extends BaseSpec {

  @RestoreSystemProperties
  def "default config should be loaded"() {
    given:
    System.setProperty('user.dir', '.')

    when:
    def conf = VideoRecorder.conf()

    then:
    conf.folder() == new File("./video")
    conf.frameRate() == 24
    conf.mode() == RecordingMode.ANNOTATED
    conf.recorderType() == RecorderType.MONTE
    conf.saveMode() == VideoSaveMode.FAILED_ONLY
    conf.videoEnabled()
    conf.screenSize() == SystemUtils.systemScreenDimension
    !conf.isRemote()
    conf.fileName() == null
  }
}
