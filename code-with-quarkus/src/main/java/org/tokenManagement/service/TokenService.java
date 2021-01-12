package org.tokenManagement.service;

import org.tokenManagement.model.Token;
import org.tokenManagement.utils.TokenGenerator;

import java.util.List;


public class TokenService implements ITokenService {
	private TokenDataHandler data;

	public TokenService() {
		data = TokenDataHandler.getInstance();
	}

	public void generateToken(String cprNumber, int amount) {
		//if the customer has 0 or 1 token left, do:
		for (int i=0;i<amount;i++) {
			Token token = new Token(TokenGenerator.createToken(),cprNumber);
			data.addToken(token);
		}
	}

	//get all unused tokens of a customer
	public List<Token> getToken(String cprNumber) {
		List<Token> allTokens = data.getAllTokens();
		List<Token> result = null;
		for (Token item : allTokens) {
			if (item.getCprNumber() == cprNumber && !item.isUsed())
				result.add(item);
		}
		return result;
	}


	public List<Token> getAllTokens() {
		return data.getAllTokens();

	}



}
