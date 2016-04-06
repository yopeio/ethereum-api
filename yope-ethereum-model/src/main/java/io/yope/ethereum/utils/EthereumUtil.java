package io.yope.ethereum.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.stream.Collectors;

public class EthereumUtil {

    public static String removeLineBreaks(final String content) {
        return content.replace("\n", "").replace("\r", "");
    }

    public static String removeLineBreaksFromFile(final String file) {
        InputStream stream = EthereumUtil.class.getClass().getResourceAsStream(file);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        String collect = buffer.lines().collect(Collectors.joining("\n"));
        return collect.replace("\n", "").replace("\r", "");
    }

    public static Long decryptQuantity(String quantity) {
        BigInteger latestBalance = new BigInteger(
                "00" + quantity.substring(2), 16);
        return latestBalance.longValue();
    }

}
