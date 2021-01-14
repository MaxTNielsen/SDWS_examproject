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

    @JsonIgnore
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

    @JsonIgnore
    public void addToken(String _newToken) {
        if(tokens == null)
        {
            tokens = new ArrayList<>();
        }
        this.tokens.add(_newToken); }

    @JsonIgnore
    public void addTokens(ArrayList<String> _newTokens){
        if(tokens == null)
        {
            tokens = new ArrayList<>();
        }
        this.tokens.addAll(_newTokens);}

    @JsonIgnore
    public void setValidity(boolean _newValidityState)
    {
        this.isValid = _newValidityState;
    }

    @JsonIgnore
    public void setValid(boolean _newValue) { this.isValid = _newValue; }

    @JsonIgnore
    public boolean equals(TokenServiceResponseMessage obj)
    {
        if(this.messageType != obj.getType() || this.isValid != obj.getValidity())
        {
            return false;
        }
        ArrayList<String> objectTokens = obj.getTokens();
        for(String currentToken:objectTokens)
        {
            if(!this.tokens.contains(currentToken))
            {
                return false;
            }
        }
            return true;
    }
}
