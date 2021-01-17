package org.tokenManagement.messaging.model;

public interface EventReceiver {
	String receiveEvent(String request) throws Exception;
}
