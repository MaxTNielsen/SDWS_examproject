package org.tokenManagement.messaging;

public class TokenGenerationRequest {

    private String customerId;
    private int numberOfTokens;

    public TokenGenerationRequest(String customerId, int numberOfTokens) {
        this.customerId = customerId;
        this.numberOfTokens = numberOfTokens;
    }

	public TokenGenerationRequest() {
		
	}
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getNumberOfTokens() {
        return numberOfTokens;
    }

    public void setNumberOfTokens(int numberOfTokens) {
        this.numberOfTokens = numberOfTokens;
    }
}
