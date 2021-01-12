package org.tokenManagement.service;

import java.util.List;
import org.tokenManagement.model.Token;


public interface ITokenService {

    List<Token> getToken(String cprNumber);

    List<Token> getAllTokens();

    void generateToken(String cprNumber, int amount);

}