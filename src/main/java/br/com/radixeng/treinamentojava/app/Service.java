package br.com.radixeng.treinamentojava.app;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

class NewAccount {
    public String client;
    public String accountType;
}

class Operation {
    public Double amount;
    public String accountType;
}

class AccountResponse {
    public AccountType type;
    public Double balance;
}

class AccountListResponse {
    public List<AccountResponse> allAccounts = new ArrayList<>();
}

@Path("/bank")
public class Service {

    static private IBank bank = Bank.getBank();

    /**
     * create an account for an existing client
     * @return String
     */
    @POST
    @Path("/account")
    @Produces(MediaType.APPLICATION_JSON)
    public String account(final NewAccount newAccount) {

        IClient client = new IClient() {
            @Override
            public String getId() {
                return newAccount.client;
            }
        };

        Boolean created = Service.bank.createAccount(client, AccountType.valueOf(newAccount.accountType));

        if (created) {
            return "Account created.";
        } else {
            return "{\"error\": \"Sorry! The system wasn't able to create your account.\"";
        }
    }

    /**
     * deposit an amount to an account
     * @return String
     */
    @POST
    @Path("account/{client_id}/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public String deposit(@PathParam("client_id") final String client, Operation deposit) {

        IClient recipient = new IClient() {
            @Override
            public String getId() {
                return client;
            }
        };

        Service.bank.deposit(deposit.amount, recipient, AccountType.valueOf(deposit.accountType));
        return "Deposit done.";

    }

    /**
     * withdraw an amount from an account
     * @return String
     */
    @POST
    @Path("account/{client_id}/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public String withdraw(@PathParam("client_id") final String client, Operation withdraw) {

        IClient recipient = new IClient() {
            @Override
            public String getId() {
                return client;
            }
        };

        Service.bank.withdraw(withdraw.amount, recipient, AccountType.valueOf(withdraw.accountType));
        return "Withdraw done.";

    }

    /**
     * get client account list
     * @param client
     * @return
     */
    @GET
    @Path("account/{client_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountListResponse getAccount(@PathParam("client_id") final String client) {

        IClient recipient = new IClient() {
            @Override
            public String getId() {
                return client;
            }
        };

        AccountListResponse response = new AccountListResponse();

        Map<AccountType, Account> accounts = Service.bank.getClientAccounts(recipient);
        
        for (AccountType type: accounts.keySet()) {
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.balance = accounts.get(type).getBalance();
            accountResponse.type = type;
            response.allAccounts.add(accountResponse);
        }

        return response;

    }

}