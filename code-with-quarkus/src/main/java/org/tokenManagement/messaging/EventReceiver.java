package org.tokenManagement.messaging;

public interface EventReceiver {
	String receiveEvent(String request) throws Exception;
}
