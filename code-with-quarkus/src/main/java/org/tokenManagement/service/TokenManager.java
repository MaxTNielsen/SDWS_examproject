package org.tokenManagement.service;

import com.google.gson.Gson;


import org.tokenManagement.model.Token;
import org.tokenManagement.utils.TokenGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.tokenManagement.messaging.RabbitMqListener;
import org.tokenManagement.messaging.model.Event;

import org.tokenManagement.messaging.model.TokenGenerationRequest;
import org.tokenManagement.messaging.model.TokenGenerationResponse;
import org.tokenManagement.messaging.model.TokenValidationRequest;
import org.tokenManagement.messaging.model.TokenValidationResponse;


public class TokenManager {

    static TokenManager instance;
    public Map<String, Token> tokens = new HashMap<>();

    public TokenManager() {

        addToken(new Token("123", "000000-0001"));
        addToken(new Token("456", "000000-0002"));
        addToken(new Token("789", "000000-0002"));
        RabbitMqListener.listenWithRPCPattern();

    }

    public static TokenManager getInstance() {
        if (instance == null)
            instance = new TokenManager();
        return instance;
    }

    //implement EventReceiver
    public String receiveEvent(String request) throws Exception {
        //System.out.println("Request String:" + request);
        //Convert string to Event
        Gson gson = new Gson();
        Event event = gson.fromJson(request, Event.class);
        Event event_to_sendback = null;
        String responseString = "";
        System.out.println("Convert to Event:" + event);
        if (event.getEventType().equals("TOKEN_GENERATION_REQUEST")) {
            //Translate received event
            String jsonString2 = gson.toJson(event.getArguments()[0]);
            TokenGenerationRequest received_event = gson.fromJson(jsonString2, TokenGenerationRequest.class);

            //prepare response
            TokenGenerationResponse response_event = new TokenGenerationResponse();
            event_to_sendback = new Event("TOKEN_GENERATION_RESPONSE", new Object[]{response_event});

            //call business logic of handling token generation
            ArrayList<String> tokens = getNewTokens(received_event.getCustomerId(), received_event.getNumberOfTokens());
            System.out.println("event handled: " + "GENERATE_TOKEN");

            //set response
            if (tokens.size() > 0) {

                response_event.setTokens(tokens);
                response_event.setCustomerId(received_event.getCustomerId());

                System.out.println("[Token Manager] Created response: " + event_to_sendback.toString());

                responseString = gson.toJson(event_to_sendback);
            }

        } else if (event.getEventType().equals("TOKEN_VALIDATION_REQUEST")) {
            //Translate received event
            String jsonString3 = gson.toJson(event.getArguments()[0]);
            TokenValidationRequest received_event = gson.fromJson(jsonString3, TokenValidationRequest.class);

            //prepare response
            TokenValidationResponse response_event = new TokenValidationResponse();
            event_to_sendback = new Event("TOKEN_VALIDATION_RESPONSE", new Object[]{response_event});

            //business logic
            Boolean isValid = false;
            String customerId = "";
            String tokenId = received_event.getToken();
            //check if the token exists
            if (tokens.containsKey(tokenId)) {
                //get customer if token exists
                customerId = tokens.get(tokenId).getUserId();
                if (!tokens.get(tokenId).isUsed()) {
                    isValid = true;
                    //after validation, set the token as used
                    tokens.get(tokenId).setUsed(true);
                }
            }
            System.out.println("event handled: " + "VALIDATE_TOKEN");
            //set response
            response_event.setCustomerId(customerId);
            response_event.setValid(isValid);
            System.out.println("[Token Manager] Created response: " + event_to_sendback.toString());
            //return event_to_sendback;
            responseString = gson.toJson(event_to_sendback);

        } else {
            System.out.println("[Token Manager] Event ignored: " + event.toString());

        }

        return responseString;

    }

    public void addToken(Token token) {
        tokens.put(token.getId(), token);
    }

    public String generateToken(String userId) {
        Token token = new Token(TokenGenerator.createToken(), userId);
        tokens.put(token.getId(), token);
        return token.getId();
    }

    public int getAvailableTokenAmount(String userId) {
        int result = 0;
        for (Map.Entry<String, Token> entry : tokens.entrySet()) {
            //System.out.println(entry.getKey() + ":::::::" + entry.getValue());
            if (entry.getValue().getUserId().equals(userId) && !entry.getValue().isUsed()) {
                result += 1;
            }
        }
        return result;
    }


    public ArrayList<String> getNewTokens(String userId, int requestedTokenNumber) {
        ArrayList<String> tokens = null;
        int numberOfAvailableTokens = this.getAvailableTokenAmount(userId);
        if (numberOfAvailableTokens > 1 || requestedTokenNumber > 5 || requestedTokenNumber < 1) {
            return null;
        } else {
            //If the number of available tokens are 0 or 1 the system will generate the requested number of tokens if it's less or equal to 5
            tokens = new ArrayList<>();
            for (int i = 0; i < requestedTokenNumber; i++) {
                tokens.add(generateToken(userId));
            }
        }
        return tokens;
    }


}
