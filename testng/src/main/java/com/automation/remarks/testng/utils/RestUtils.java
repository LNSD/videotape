package com.automation.remarks.testng.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Created by Serhii_Pirohov on 11.05.2016.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestUtils {

  public static String sendRecordingRequest(final String url) {
    CloseableHttpResponse response = null;
    try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
      final HttpGet get = new HttpGet(url);
      response = client.execute(get);
      HttpEntity content = response.getEntity();
      String message = EntityUtils.toString(content);
      log.info("Response: {}", message);
      return message;
    } catch (Exception ex) {
      log.error("Request", ex);
    } finally {
      HttpClientUtils.closeQuietly(response);
    }
    return "";
  }
}
