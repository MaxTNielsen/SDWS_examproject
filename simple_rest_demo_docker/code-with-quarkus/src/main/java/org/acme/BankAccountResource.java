package org.acme;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/accounts/balance")
public class BankAccountResource {
    SimpleDTUPayBL dtuPay = SimpleDTUPayBL.getInstance_();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public BigDecimal getAccountBalance(String accountID){
        return dtuPay.getBalance(accountID);
    }
}
