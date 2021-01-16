package org.tokenManagement.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseEvent {

	private String eventType;
	private String userId;
	private ArrayList<String> token;
	private boolean isValid;
	
	
	public ResponseEvent() {}


	public ResponseEvent(String eventType, String userId, ArrayList<String> token, boolean isValid) {
		this.eventType = eventType;
		this.userId = userId;
		this.token = token;
		this.isValid = isValid;
	}



	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public ArrayList<String> getToken() {
		return token;
	}


	public void setToken(ArrayList<String> token) {
		this.token = token;
	}


	public boolean isValid() {
		return isValid;
	}


	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}



	@Override
	public String toString() {
		return "ResponseEvent [eventType=" + eventType + ", userId=" + userId + "]";
	}


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}







	
	
}
