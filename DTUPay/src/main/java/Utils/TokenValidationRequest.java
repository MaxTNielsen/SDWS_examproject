package Utils;

public class TokenValidationRequest {
    private String token;

	public TokenValidationRequest(String token) {
		this.token = token;
	}

	public TokenValidationRequest() {
		
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
    

}
