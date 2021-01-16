package org.tokenManagement.utils;

import java.util.UUID;


public class TokenGenerator {
    public TokenGenerator() {

    }
    public static String createToken() {
        UUID uuid = UUID.randomUUID();
        String s = UUID.randomUUID().toString();
        return s;
    }


}

