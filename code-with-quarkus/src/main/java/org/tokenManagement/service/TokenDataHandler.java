package org.tokenManagement.service;

import org.tokenManagement.model.Token;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TokenDataHandler {
    private static final TokenDataHandler data = new TokenDataHandler();
    private HashMap<String, Token> allTokens;
    public static TokenDataHandler getInstance(){
        return data;
    }

    public TokenDataHandler() {
        allTokens = new HashMap<String, Token>();
        Token initalToken1 = new Token("123","000000-0001");
        Token initalToken2 = new Token("456","000000-0002");
        Token initalToken3 = new Token("789","000000-0002");
        allTokens.put(initalToken1.getId(),initalToken1);
        allTokens.put(initalToken2.getId(),initalToken2);
        allTokens.put(initalToken3.getId(),initalToken3);


    }
    public void addToken(Token token) {
        allTokens.put(token.getId(), token);
    }

    public List<Token> getAllTokens() {
        Iterator iter1=allTokens.entrySet().iterator();
        System.out.println("allTokens.size: "+allTokens.size());
        List<Token> result = null;
        while(iter1.hasNext()){
            Map.Entry<String,Token> entry=(Map.Entry<String,Token>)iter1.next();
            System.out.println("Item: "+entry.getKey());

            result.add(entry.getValue());

        }
        return result;
    }

}
