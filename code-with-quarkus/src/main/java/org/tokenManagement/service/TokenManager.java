package org.tokenManagement.service;
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
    public String generateToken(String userId)
    {
        Token token = new Token(TokenGenerator.createToken(),userId);
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


    public int getAvailableTokenAmount(String userId){
        int result = 0;
        for (Map.Entry<String, Token> entry : tokens.entrySet())
        {
            if (entry.getValue().getUserId().equals(userId) && !entry.getValue().isUsed())
                result+=1;
        }
        return result;
    }

    public String getToken(String userId){
        String result = "";
        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
            if (entry.getValue().getUserId().equals(userId) && !entry.getValue().isUsed()) {
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

    public ArrayList<String> getNewTokens(String cprNumber, int requestedTokenNumber)
    {
        ArrayList<String> tokens = new ArrayList<>();
        int numberOfAvailableTokens = this.getAvailableTokenAmount(cprNumber);
        if(numberOfAvailableTokens > 1 || requestedTokenNumber > 5 || requestedTokenNumber < 1)
        {
            return tokens;
        }
        else
        {
            //If the number of available tokens are 0 or 1 the system will generate the requested number of tokens if it's less or eqal than 5
            for(int i = 0; i < requestedTokenNumber; i++)
            {
                tokens.add(generateToken(cprNumber));
            }
        }
        return tokens;
    }

    public String getUserIdbyTokenId(String tokenId)
    {
        return this.tokens.get(tokenId).getUserId();
    }

}
