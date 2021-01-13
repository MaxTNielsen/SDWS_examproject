package org.tokenManagement.service;
import io.cucumber.core.gherkin.messages.internal.gherkin.internal.com.eclipsesource.json.JsonArray;
import org.tokenManagement.model.Token;
import org.tokenManagement.utils.TokenGenerator;

import java.util.HashMap;
import java.util.List;
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
        if ( !tokens.get(tokenId).isUsed())
            return true;
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

//    public boolean generateToken(String cprNumber, int amount){
//        if (getAvailableTokenAmount(cprNumber) <= 1){
//            for (int i=0; i<amount; i++){
//                Token token = new Token(TokenGenerator.createToken(), cprNumber);
//                tokens.put(token.getId(),token);
//            }
//            return true;
//        }
//        else
//            return false;
//    }
    //    public List<Token> getAvailableTokens(String cprNumber){
//        List<Token> result = null;
//        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
//            if (validateToken(entry.getValue().getId(),entry.getValue().getCprNumber()))
//                    result.add(entry.getValue());
//        }
//        return result;
//
//    }
//    public List<Token> getAllTokens(){
//        List<Token> result = null;
//        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
//            if (validateToken(entry.getValue().getId(),entry.getValue().getCprNumber()))
//                result.add(entry.getValue());
//        }
//        return result;
//
//    }



}
