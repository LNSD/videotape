package com.automation.remarks.video;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Created by sepi on 15.07.16.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

  public static String formatDate(Date date, String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(date);
  }
}
