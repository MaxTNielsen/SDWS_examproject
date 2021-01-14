package org.tokenManagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class TokenServiceResponseMessage {
    public enum tokenServiceResponseMessageType
    {
        RESPONSE_GET_VALIDITY, // only the isValid argument is required
        RESPONSE_GET_ALL_TOKENS, // only the list of tokenID-s is required
        RESPONSE_NEW_TOKENS // only the list of tokenID-s is required
    }

    @JsonProperty("messageType")
    private tokenServiceResponseMessageType messageType;
    @JsonProperty("isValid")
    private boolean isValid;
    @JsonProperty("tokens")
    private ArrayList<String> tokens;

    public TokenServiceResponseMessage ()
    {
        this.messageType = null;
        this.isValid = false;
        this.tokens = null;
    }

    public TokenServiceResponseMessage (tokenServiceResponseMessageType _messageType)
    {
        this.messageType = _messageType;
        this.isValid = false;
        this.tokens = null;
    }

    public TokenServiceResponseMessage (tokenServiceResponseMessageType _messageType, boolean _isValid)
    {
        this.messageType = _messageType;
        this.isValid = _isValid;
        this.tokens = null;
    }

    public TokenServiceResponseMessage (tokenServiceResponseMessageType _messageType, ArrayList<String> _tokens)
    {
        this.messageType = _messageType;
        this.isValid = true;
        this.tokens = _tokens;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TokenServiceResponseMessage (@JsonProperty("messageType") tokenServiceResponseMessageType _messageType,
                                        @JsonProperty("isValid")boolean _isValid,
                                        @JsonProperty("tokens") ArrayList<String> _tokens)
    {
        this.messageType = _messageType;
        this.isValid = _isValid;
        this.tokens = _tokens;
    }

    public TokenServiceResponseMessage (String json) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenServiceResponseMessage temporaryObject = mapper.readValue(json,TokenServiceResponseMessage.class);
        this.messageType = temporaryObject.messageType;
        this.isValid = temporaryObject.isValid;
        this.tokens = temporaryObject.tokens;
    }

    public String toJson() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonIgnore
    public tokenServiceResponseMessageType getType()
    {
        return this.messageType;
    }

    @JsonIgnore
    public boolean getValidity()
    {
        return this.isValid;
    }

    @JsonIgnore
    public ArrayList<String> getTokens()
    {
        return this.tokens;
    }

    public void addToken(String _newToken) { this.tokens.add(_newToken); }

    public void addTokens(ArrayList<String> _newTokens){this.tokens.addAll(_newTokens);}

    public void setValidity(boolean _newValidityState)
    {
        this.isValid = _newValidityState;
    }

    public void setValid(boolean _newValue) { this.isValid = _newValue; }
}
