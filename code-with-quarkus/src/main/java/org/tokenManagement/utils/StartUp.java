package org.tokenManagement.utils;

import io.quarkus.runtime.annotations.QuarkusMain;

import org.tokenManagement.messaging.*;
import org.tokenManagement.service.TokenManager;

@QuarkusMain
public class StartUp {
    public static void main(String[] args) throws Exception {
    	new StartUp().startUp();
    }

	private void startUp() throws Exception {
		EventSender s = new RabbitMqSender();
		TokenManager service = new TokenManager(s);
		new RabbitMqListener(service).listen();
	}
}
