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
        GET_VALIDITY,
        GET_ALL_TOKENS,
        REQUEST_NEW_TOKENS,
    }

    @JsonProperty("messageType")
    private tokenServiceRequestMessageType messageType;
    @JsonProperty("token")
    private String token;
    @JsonProperty("userCpr")
    private String userCpr;

    public TokenServiceRequestMessage ()
    {

    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TokenServiceRequestMessage (@JsonProperty("messageType") tokenServiceRequestMessageType _messageType,
                                       @JsonProperty("token") String _token,
                                       @JsonProperty("userCpr")String _userCpr)
    {
        this.messageType = _messageType;
        this.token = _token;
        this.userCpr = _userCpr;
    }

    public TokenServiceRequestMessage (String json) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenServiceRequestMessage temporaryObject = mapper.readValue(json,TokenServiceRequestMessage.class);
        this.messageType = temporaryObject.messageType;
        this.token = temporaryObject.token;
        this.userCpr = temporaryObject.userCpr;
    }

    public String toJson() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

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
    public String getUserCpr()
    {
        return this.userCpr;
    }

}