package com.wallnet.ngork.core;

import lombok.extern.slf4j.Slf4j;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author skyli665
 */
@Slf4j
public class FormatBytes {
    static FSTConfiguration conf = FSTConfiguration.createAndroidDefaultConfiguration();


    public static ClientBean read(byte[] input) {
        log.info("反序列化数据");
        return (ClientBean) conf.asObject(input);
    }

    public static byte[] write(ClientBean toWrite) {
        log.info("序列化数据");
        return conf.asByteArray(toWrite);
    }
}
