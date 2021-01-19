package tokenManagement.messaging.model;

public class TokenValidationResponse {
    private String customerId;
    private boolean isValid;
    
	public TokenValidationResponse(String customerId, boolean isValid) {		
		this.customerId = customerId;
		this.isValid = isValid;
	}

	public TokenValidationResponse() {
		
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
