package org.tokenManagement.service;
import io.cucumber.java.bs.A;
import org.tokenManagement.model.Token;
import org.tokenManagement.utils.TokenGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TokenManager {
    static TokenManager instance;

    public Map<String, Token> tokens = new HashMap<>();



    public static TokenManager getInstance()
    {
        if (instance == null)
            instance = new TokenManager();
        return instance;
    }

    public TokenManager()
    {
        addToken(new Token("123","000000-0001"));
        addToken(new Token("456","000000-0002"));
        addToken(new Token("789","000000-0002"));
    }

    public void addToken(Token token){
        tokens.put(token.getId(),token);
    }
    public String generateToken(String cprNumber)
    {
        Token token = new Token(TokenGenerator.createToken(),cprNumber);
        tokens.put(token.getId(),token);
        return token.getId();
    }
    public boolean validateToken(String tokenId){
        boolean exist = tokens.containsKey(tokenId);
        if(!exist)
        {
            return false;
        }
        if ( !tokens.get(tokenId).isUsed()) {
            //set token as used
            tokens.get(tokenId).setUsed(true);
            return true;
        }
        else return false;
    }


    public int getAvailableTokenAmount(String cprNumber){
        int result = 0;
        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
            if (validateToken(entry.getValue().getId())&& (entry.getValue().getCprNumber().equals(cprNumber)))
                result+=1;
        }
        return result;
    }
    public String getToken(String cprNumber){
        String result = "";
        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
            if (entry.getValue().getCprNumber().equals(cprNumber) && !entry.getValue().isUsed()) {
                result=entry.getValue().getId();
                break;
            }
        }
        return result;
    }
    public void setUsed(String tokenId)
    {
        tokens.get(tokenId).setUsed(true);
    }

    public ArrayList<String> getNewTokens(String cprNumber)
    {
        ArrayList<String> tokens = new ArrayList<>();
        int numberOfAvailableTokens = this.getAvailableTokenAmount(cprNumber);
        if(numberOfAvailableTokens > 1)
        {
            return tokens;
        }
        else
        {
            for(int i = numberOfAvailableTokens; i < 5; i++)
            {
                tokens.add(generateToken(cprNumber));
            }
        }
        return tokens;
    }



}
