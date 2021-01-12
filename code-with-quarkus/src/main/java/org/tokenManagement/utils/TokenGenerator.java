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

    public static void main(String[] args) {
        String s = createToken();
        String s2 = createToken();
        System.out.println("token1 is "+ s);
        System.out.println("token2 is "+ s2);

    }

}

