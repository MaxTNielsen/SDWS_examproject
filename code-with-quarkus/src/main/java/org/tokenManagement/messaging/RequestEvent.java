package org.tokenManagement.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestEvent {

	private String eventType;
	private String userId;
	private String token;
	private int numberOfToken;
	
	
	public RequestEvent() {}


	public RequestEvent(String eventType, String userId, String token, int numberOfToken) {
		this.eventType = eventType;
		this.userId = userId;
		this.token = token;
		this.numberOfToken = numberOfToken;
	}


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public int getNumberOfToken() {
		return numberOfToken;
	}


	public void setNumberOfToken(int numberOfToken) {
		this.numberOfToken = numberOfToken;
	}


	@Override
	public String toString() {
		return "RequestEvent [eventType=" + eventType + ", userId=" + userId + ", token=" + token + ", numberOfToken="
				+ numberOfToken + "]";
	};
	
	
	
}
