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

package es.lnsd.videotape.core.config;

import es.lnsd.videotape.core.utils.OSUtils;
import java.util.HashMap;
import java.util.Map;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

public final class ConfigLoader {

  private ConfigLoader() {
    throw new IllegalStateException();
  }

  public static <T extends Config> T load(Class<T> configClass, String configurationFile,
                                          Map<?, ?>... imports) {
    String file = System.getProperty("video.configurationFile", "classpath:video.properties");

    // Allow overriding the configuration file
    if (StringUtils.isNotBlank(configurationFile)) {
      file = configurationFile;
    }

    ConfigFactory.setProperty("conf.file", file);
    ConfigFactory.setProperty("os.type", OSUtils.getOsType());

    return ConfigFactory.create(configClass, imports);
  }

  public static <T extends Config> T load(Class<T> configClass, Map<?, ?>... imports) {
    return load(configClass, null, imports);
  }

  public static Configuration load() {
    return load(new HashMap<>());
  }

  public static Configuration load(Map<?, ?>... conf) {
    return load(Configuration.class, null, conf);
  }
}
