package payment;



import java.util.List;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.AccountInfo;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

public class BankAccountTests {

	public static void main(String[] args) throws BankServiceException_Exception {
		BankService bank = new BankServiceService().getBankServicePort();
		List<AccountInfo> accounts = bank.getAccounts();
		//get two random existing bank accounts
		String id = "14c3e7ca-0fb6-4b73-a26f-750784ea1044";
		System.out.println(bank.getAccount(id).getBalance());
		//System.out.println(accounts.get(1).getAccountId());


	}

}