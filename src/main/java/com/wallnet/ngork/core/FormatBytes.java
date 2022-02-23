package com.wallnet.ngork.core;

import org.nustaq.serialization.FSTConfiguration;

/**
 * @author skyli665
 */
public class FormatBytes {
    static FSTConfiguration conf = FSTConfiguration.createAndroidDefaultConfiguration();


    public static Text myreadMethod(byte[] input) {
        return (Text) conf.asObject(input);
    }

    public static byte[] mywriteMethod(Text toWrite) {
        return conf.asByteArray(toWrite);
    }
}
