package org.tokenManagement.service;
import com.google.gson.Gson;

import org.tokenManagement.messaging.model.*;
import org.tokenManagement.model.Token;
import org.tokenManagement.utils.TokenGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.tokenManagement.messaging.*;


public class TokenManager {

    static TokenManager instance;
    public Map<String, Token> tokens = new HashMap<>();
    public TokenManager()
    {

        addToken(new Token("123","000000-0001"));
        addToken(new Token("456","000000-0002"));
        addToken(new Token("789","000000-0002"));
        RabbitMqListener.listenWithRPCPattern();

    }
    public static TokenManager getInstance() {
        if (instance == null)
            instance = new TokenManager();
        return instance;
    }



    public Event receiveEvent(Event event) throws Exception {

        Event event_to_sendback = null;
       Gson gson = new Gson();
        if (event.getEventType().equals("TOKEN_GENERATION_REQUEST")) {
            //Get request
            String requestString = gson.toJson(event.getArguments()[0]);
            TokenGenerationRequest received_event = gson.fromJson(requestString,TokenGenerationRequest.class);
            //business logic
            ArrayList<String> tokens=getNewTokens(received_event.getCustomerId(), received_event.getNumberOfTokens());
            System.out.println("[Token Manager] handled: "+"GENERATE_TOKEN");

            if (tokens.size() > 0) {
                //set response
                TokenGenerationResponse response_event = new TokenGenerationResponse();
                event_to_sendback = new Event("TOKEN_GENERATION_RESPONSE", new Object[] { response_event });
                response_event.setTokens(tokens);
                response_event.setCustomerId(received_event.getCustomerId());

                //System.out.println("[Token Manager] Created response: " + event_to_sendback.toString());

            }

        } else if (event.getEventType().equals("TOKEN_VALIDATION_REQUEST")) {
            //Get request
            String requestString = gson.toJson(event.getArguments()[0]);
            TokenValidationRequest received_event = gson.fromJson(requestString,TokenValidationRequest.class);

            //business logic
            String tokenId = received_event.getToken();
            TokenValidationResponse response_event = validateToken(tokenId);
            //create response event
            event_to_sendback = new Event("TOKEN_VALIDATION_RESPONSE", new Object[] { response_event });

            System.out.println("[Token Manager] handled: "+"VALIDATE_TOKEN");

            //System.out.println("[Token Manager] Created response: "+event_to_sendback.toString());


        }
        else {
            System.out.println("[Token Manager] Event ignored: "+event.toString());

        }

        return event_to_sendback;

    }

    private TokenValidationResponse validateToken(String tokenId) {
        TokenValidationResponse response_event = new TokenValidationResponse();
        Boolean isValid = false;
        String customerId = "";
        //check if the token exists
        if (tokens.containsKey(tokenId)) {
            //get customer if token exists
            customerId=tokens.get(tokenId).getUserId();
            if (!tokens.get(tokenId).isUsed()) {
                isValid = true;
                //after validation, set the token as used
                tokens.get(tokenId).setUsed(true);
            }
        }
        //set response
        response_event.setCustomerId(customerId);
        response_event.setValid(isValid);
        return response_event;
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

    public int getAvailableTokenAmount(String userId){
        int result = 0;
        for (Map.Entry<String,Token> entry : tokens.entrySet())
        {
            //System.out.println(entry.getKey() + ":::::::" + entry.getValue());
            if (entry.getValue().getUserId().equals(userId) && !entry.getValue().isUsed())
            {
                result+=1;
            }
        }
        return result;
    }



    public ArrayList<String> getNewTokens(String userId, int requestedTokenNumber)
    {
        ArrayList<String> tokens = null;
        int numberOfAvailableTokens = this.getAvailableTokenAmount(userId);
        if (numberOfAvailableTokens > 1 || requestedTokenNumber > 5 || requestedTokenNumber < 1)
        {
            return null;
        }
        else
        {
            //If the number of available tokens are 0 or 1 the system will generate the requested number of tokens if it's less or equal to 5
            tokens = new ArrayList<>();
            for(int i = 0; i < requestedTokenNumber; i++)
            {
                tokens.add(generateToken(userId));
            }
        }
        return tokens;
    }


}
