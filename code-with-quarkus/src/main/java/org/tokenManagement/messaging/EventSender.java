package org.tokenManagement.messaging;

public interface EventSender {

	void sendEvent(Event event) throws Exception;

}
