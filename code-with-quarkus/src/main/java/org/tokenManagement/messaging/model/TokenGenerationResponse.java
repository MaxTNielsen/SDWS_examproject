package org.tokenManagement.messaging.model;

import java.util.ArrayList;

public class TokenGenerationResponse {
    private String customerId;
    private ArrayList<String> tokens;
    
	public TokenGenerationResponse(String customerId, ArrayList<String> tokens) {
		this.customerId = customerId;
		this.tokens = tokens;
	}
	public TokenGenerationResponse() {
		
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public ArrayList<String> getTokens() {
		return tokens;
	}
	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}


}
