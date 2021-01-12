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
		System.out.println("call generate token");
		//if the customer has 0 or 1 token left, do:
		if (getRemainingTokenSize(cprNumber) <=1) {
			for (int i = 0; i < amount; i++) {
				Token token = new Token(TokenGenerator.createToken(), cprNumber);
				data.addToken(token);
			}
		}
	}

	//get all unused tokens of a customer
	public int getRemainingTokenSize(String cprNumber) {
		List<Token> allTokens = data.getAllTokens();
		int result = 0;
		for (Token item : allTokens) {
			if (item.getCprNumber() == cprNumber && !item.isUsed())
				result += 1;
		}
		return result;
	}

	public List<Token> getAllTokens() {
		return data.getAllTokens();

	}



}
