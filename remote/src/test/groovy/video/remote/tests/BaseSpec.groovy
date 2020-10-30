package video.remote.tests

import com.automation.remarks.remote.StartGrid
import com.automation.remarks.video.recorder.monte.MonteRecorder
import spock.lang.Specification

import static java.lang.Thread.sleep

/**
 * Created by sergey on 5/14/16.
 */
abstract class BaseSpec extends Specification {

  final String NODE_SERVLET_URL = "http://localhost:5555/extra/Video"

  def setupSpec() {
    runGrid()
  }

  def runGrid() {
    String[] args = []
    StartGrid.main(args)
    sleep(1000)
  }

  def getVideoFiles() {
    MonteRecorder.conf().folder().listFiles()
  }

}
