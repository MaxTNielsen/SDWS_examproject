package org.tokenManagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;


public class TokenServiceRequestMessage implements Serializable {
    public enum tokenServiceRequestMessageType
    {
        REQUEST_PAYMENT_VALIDATION, //provide tokenID
        GET_ALL_TOKENS, //provide userId
        REQUEST_NEW_TOKENS, //provide userId and the number of the requested tokens
    }

    @JsonProperty("messageType")
    private tokenServiceRequestMessageType messageType;
    @JsonProperty("token")
    private String token;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("tokenNumber")
    private int tokenNumber;

    public TokenServiceRequestMessage ()
    {
        this.messageType = null;
        this.token = null;
        this.userId = null;
        this.tokenNumber = 0;
    }

    public TokenServiceRequestMessage (tokenServiceRequestMessageType _messageType)
    {
        this.messageType = _messageType;
        this.token = null;
        this.userId = null;
        this.tokenNumber = 0;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TokenServiceRequestMessage (@JsonProperty("messageType") tokenServiceRequestMessageType _messageType,
                                       @JsonProperty("token") String _token,
                                       @JsonProperty("userId")String _userId,
                                       @JsonProperty("tokenNumber")int _tokenNumber)
    {
        this.messageType = _messageType;
        this.token = _token;
        this.userId = _userId;
        this.tokenNumber = _tokenNumber;
    }

    public TokenServiceRequestMessage (String json) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenServiceRequestMessage temporaryObject = mapper.readValue(json,TokenServiceRequestMessage.class);
        this.messageType = temporaryObject.messageType;
        this.token = temporaryObject.token;
        this.userId = temporaryObject.userId;
        this.tokenNumber = temporaryObject.tokenNumber;
    }

    public String toJson() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @JsonIgnore
    public tokenServiceRequestMessageType getType()
    {
        return this.messageType;
    }

    @JsonIgnore
    public String getToken()
    {
        return this.token;
    }

    @JsonIgnore
    public String getUserId ()
    {
        return this.userId;
    }

    @JsonIgnore
    public int getRequestedTokenNumber(){return this.tokenNumber;}

    @JsonIgnore
    public void setToken(String _tokenId)
    {
        this.token = _tokenId;
    }

    @JsonIgnore
    public void setUserId (String _userId)
    {
        this.userId = _userId;
    }

    @JsonIgnore
    public void setRequestedTokenNumber(int _newNumber){this.tokenNumber = _newNumber;}

    @JsonIgnore
    public boolean equals(TokenServiceRequestMessage obj)
    {
        if(this.messageType == obj.getType() && this.userId.equals(obj.getUserId()) && this.token.equals(obj.getToken()) && this.tokenNumber == obj.getRequestedTokenNumber())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}