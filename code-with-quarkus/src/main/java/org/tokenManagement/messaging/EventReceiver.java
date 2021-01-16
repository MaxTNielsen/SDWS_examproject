package org.tokenManagement.messaging;

public interface EventReceiver {
	void receiveEvent(Event event) throws Exception;
}
