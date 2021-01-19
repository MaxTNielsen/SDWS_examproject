package payment;

import java.util.ArrayList;

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

    public boolean isValid() {
        return isValid;
    }


}
