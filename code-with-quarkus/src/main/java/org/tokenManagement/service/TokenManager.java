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
<<<<<<< HEAD
        addToken(new Token("888", "6243c4c1-d7ba-4ea0-8363-141ca09c9082"));
        addToken(new Token("999", "d7d42ca5-3923-4def-a7d0-4be6d9622764"));
        addToken(new Token("456", "000000-0002"));
=======
        addToken(new Token("888", "1c78cbb5-3b2c-4f75-b325-a0895c135af2"));
        addToken(new Token("999", "cfb38983-1e05-4200-a7dc-86948f405de6"));
        addToken(new Token("456", "b004c7e5-b0dc-42ad-b55d-a9c8186fc97a"));
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
        addToken(new Token("789", "000000-0002"));
        RabbitMqListener.listenWithRPCPattern();
    }

    public static TokenManager getInstance() {
        if (instance == null)
            instance = new TokenManager();
        return instance;
    }

    public Event receiveEvent(Event event) throws Exception {

        Gson gson = new Gson();

        Event event_to_sendback = null;

        if (event.getEventType().equals("TOKEN_GENERATION_REQUEST")) {

            //Get request
            String requestString = gson.toJson(event.getArguments()[2]);
            TokenGenerationRequest received_event = gson.fromJson(requestString, TokenGenerationRequest.class);
            //business logic
            ArrayList<String> tokens = getNewTokens(received_event.getCustomerId(), received_event.getNumberOfTokens());
            //System.out.println("[Token Manager] handled: " + "GENERATE_TOKEN");

            if (tokens.size() > 0) {
                //set response
                TokenGenerationResponse response_event = new TokenGenerationResponse();
                event_to_sendback = new Event("TOKEN_GENERATION_RESPONSE", new Object[]{response_event});
                response_event.setTokens(tokens);
                response_event.setCustomerId(received_event.getCustomerId());
                //System.out.println("[Token Manager] Created response: " + event_to_sendback.toString());
            }

        } else if (event.getEventType().equals("TOKEN_VALIDATION_REQUEST")) {

            //Get request
            String requestString = gson.toJson(event.getArguments()[2]);
            TokenValidationRequest received_event = gson.fromJson(requestString, TokenValidationRequest.class);
            //business logic
            String tokenId = received_event.getToken();
            TokenValidationResponse response_event = validateToken(tokenId);
            //create response event
            event_to_sendback = new Event("TOKEN_VALIDATION_RESPONSE", new Object[]{response_event});

            System.out.println("[Token Manager] handled: " + "VALIDATE_TOKEN");

            //System.out.println("[Token Manager] Created response: "+event_to_sendback.toString());

        } else {
            System.out.println("[Token Manager] Event ignored: " + event.toString());

        }
        return event_to_sendback;
    }

    public TokenValidationResponse validateToken(String tokenId) {
        TokenValidationResponse response = new TokenValidationResponse();
        Boolean isValid = false;
        String customerId = "";
        //check if the token exists
        if (tokens.containsKey(tokenId)) {
            //get customer if token exists
            customerId = tokens.get(tokenId).getUserId();
            if (!tokens.get(tokenId).isUsed()) {
                isValid = true;
                //after validation, set the token as used
<<<<<<< HEAD
                //tokens.get(tokenId).setUsed(true); //commented out for testing purpose
=======
                //tokens.get(tokenId).setUsed(true);
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
            }
        }
        //set response
        response.setCustomerId(customerId);
        response.setValid(isValid);
        return response;
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
