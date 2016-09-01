package com.automation.remarks.junit;

import com.automation.remarks.video.RecorderFactory;
import com.automation.remarks.video.annotations.Video;
import com.automation.remarks.video.recorder.IVideoRecorder;
import com.automation.remarks.video.recorder.monte.MonteRecorder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;

import static com.automation.remarks.video.enums.RecordingMode.ALL;
import static com.automation.remarks.video.RecordingUtils.doVideoProcessing;


/**
 * Created by sergey on 4/21/16.
 */
public class VideoRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Video video = description.getAnnotation(Video.class);
                String name = getFileName(video, description);
                if (MonteRecorder.conf().getMode().equals(ALL) || (video != null && video.enabled())) {
                    recordVideo(name, base);
                } else {
                    base.evaluate();
                }
            }

            private void recordVideo(String name, final Statement base) throws Throwable {
                boolean successful = false;
                IVideoRecorder recorder = RecorderFactory.getRecorder();
                recorder.start();
                try {
                    base.evaluate();
                    successful = true;
                } finally {
                    File video = recorder.stopAndSave(name);
                    if (description.isTest()) {
                        doVideoProcessing(successful, video);
                    }
                }
            }

            private String getFileName(Video video, Description description) {
                String methodName = description.getMethodName();
                if (video == null) {
                    return methodName;
                }
                String videoName = video.name();
                return videoName.length() > 1 ? videoName : methodName;
            }
        };
    }
}
