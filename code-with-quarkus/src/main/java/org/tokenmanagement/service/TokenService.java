package org.tokenmanagement.service;

import org.tokenmanagement.model.Token;

public class TokenService {
	private TokenDataHandler data;
    //private Token token;

	public TokenService() {
		data = TokenDataHandler.getInstance();		
	}
	
	public void generateToken(String cprNumber) {
		Token token = new Token("random number",cprNumber);
        data.addToken(token);
	}
//	public Token getToken(String cprNumber) {
//		return data.getToken(cprNumber);
//	}
	public String getAllTokens() {
		return data.getAllTokens();	

	}

	public boolean getToken(String cprNumber) {
		// TODO Auto-generated method stub
		return true;
	}

}
