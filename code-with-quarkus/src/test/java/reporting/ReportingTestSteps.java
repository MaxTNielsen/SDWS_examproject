package reporting;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.eclipse.aether.RepositoryEvent;
import reporting.controller.*;
import reporting.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class ReportingTestSteps {
    EventController reportingEventController = EventController.getInstance();
    UUID uuid = UUID.randomUUID();
    ArrayList<String> transactions = new ArrayList<>();

    @Given("an empty transaction history")
    public void an_empty_transaction_history() {
        reportingEventController.clearAll();
        transactions.clear();
    }

    @When("{string} pays {int} Kr to {string}")
    public void pays_kr_to(String string, Integer int1, String string2) {
        String token = UUID.randomUUID().toString();
        transactions.add(token);
        Transaction transaction = new Transaction(token,string2, string, int1);
        Event event = new Event("NEW_TRANSACTION", new Object[]{transaction});
        reportingEventController.eventHandler(event);
    }

    @Then("The manager report should contain {int} transactions")
    public void the_manager_report_should_contain_transactions(Integer int1) {
        Event event = new Event("MANAGER_REPORT", new Object[]{});
        Event response = reportingEventController.eventHandler(event);
        ArrayList<Transaction> receivedTransactions = (ArrayList<Transaction>) response.getArguments()[0];
        assertEquals((int) int1,(int) receivedTransactions.size());
    }

    @Then("The money flow should be {int} Kr")
    public void the_money_flow_should_be_kr(Integer int1) {
        Event event = new Event("MANAGER_MONEY_FLOW", new Object[]{});
        Event response = reportingEventController.eventHandler(event);
        int moneyFlow = (int) response.getArguments()[0];
        assertEquals((int) int1,(int) moneyFlow);
    }

    @When("{int} st\\/rd\\/nd Transaction is refunded")
    public void st_rd_nd_transaction_is_refunded(Integer int1) {
        Event event = new Event("NEW_REFUND", new Object[]{transactions.get(int1 - 1)});
        Event response = reportingEventController.eventHandler(event);
    }

    @Then("The {int} st\\/rd\\/nd Transaction should be refunded")
    public void the_st_rd_nd_transaction_should_be_refunded(Integer int1) {
        Event event = new Event("MANAGER_REPORT", new Object[]{});
        Event response = reportingEventController.eventHandler(event);
        ArrayList<Transaction> receivedTransactions = (ArrayList<Transaction>) response.getArguments()[0];
        String selectedToken = transactions.get(int1 - 1);
        boolean refunded = false;
        for(Transaction currentTransaction:receivedTransactions)
        {
            if(selectedToken.equals(currentTransaction.getToken()))
            {
                refunded = currentTransaction.isRefunded();
                break;
            }
        }
        assertTrue(refunded);
    }

    @Then("The customer report of {string} from the last {int} secs should contain {int} transactions")
    public void the_customer_report_of_from_the_last_secs_should_contain_transactions(String string, Integer int1, Integer int2) throws InterruptedException {
        Thread.sleep(1000);
        LocalDateTime intervalEnd = LocalDateTime.now();
        LocalDateTime intervalStart = intervalEnd.minus(Duration.ofSeconds(int1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String intervalStartString = intervalStart.format(formatter);
        String intervalEndString = intervalEnd.format(formatter);
        Event event = new Event("COSTUMER_REPORT", new Object[]{string, intervalStartString, intervalEndString});
        Event response = reportingEventController.eventHandler(event);
        ArrayList<CustomerTransaction> reportingEventController = (ArrayList<CustomerTransaction>) response.getArguments()[0];
        assertEquals((int) int2,(int) reportingEventController.size());
    }

    @When("waits {int} secs")
    public void waits_secs(Integer int1) throws InterruptedException {
        Thread.sleep(1000 * int1);
    }

    @Then("The merchant report of {string} from the last {int} secs should contain {int} transactions")
    public void the_merchant_report_of_from_the_last_secs_should_contain_transactions(String string, Integer int1, Integer int2) throws InterruptedException {
        Thread.sleep(1000);
        LocalDateTime intervalEnd = LocalDateTime.now();
        LocalDateTime intervalStart = intervalEnd.minus(Duration.ofSeconds(int1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String intervalStartString = intervalStart.format(formatter);
        String intervalEndString = intervalEnd.format(formatter);
        Event event = new Event("MERCHANT_REPORT", new Object[]{string, intervalStartString, intervalEndString});
        Event response = reportingEventController.eventHandler(event);
        ArrayList<MerchantTransaction> reportingEventController = (ArrayList<MerchantTransaction>) response.getArguments()[0];
        assertEquals((int) int2,(int) reportingEventController.size());;
    }
}
