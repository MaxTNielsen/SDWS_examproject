package org.Json;

public class AccountRegistrationReponse {
    public String ID;
    public Boolean status;

    public AccountRegistrationReponse(String ID, boolean status)
    {
        this.ID = ID;
        this.status = status;
    }
}
