package org.tokenManagement.utils;

import org.tokenManagement.service.NetworkModule;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


public class TokenGenerator {
    public TokenGenerator() {

    }
    public static String createToken() {
        UUID uuid = UUID.randomUUID();
        String s = UUID.randomUUID().toString();
        return s;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        String s = createToken();
        String s2 = createToken();
        System.out.println("token1 is "+ s);
        System.out.println("token2 is "+ s2);

        NetworkModule network = new NetworkModule();
        network.runNetworkModule();

    }

}

