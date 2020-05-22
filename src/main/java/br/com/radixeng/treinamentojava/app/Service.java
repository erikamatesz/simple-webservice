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

class TransferOperation {
    public Double amount;
    public String senderAccountType;
    public String recipientAccountType;
}

class AccountResponse {
    public AccountType type;
    public Double balance;
}

class AccountListResponse {
    public List<AccountResponse> allAccounts = new ArrayList<>();
}

class TransactionResponse {
    public Double amount;
    public Date timestamp;
    public String description;
}

class TransactionListResponse {
    public List<TransactionResponse> allTransactions = new ArrayList<>();
}


@Path("/bank")
public class Service {

    static private IBank bank = Bank.getBank();

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Welcome to your online bank!";
    }

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
            return "{\"message\": \"Account created.\"}";
        } else {
            return "{\"error\": \"Sorry! The system wasn't able to create your account.\"}";
        }
    }

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
        return "{\"message\": \"Deposit done.\"}";

    }

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
        return "{\"message\": \"Withdraw done.\"}";

    }

    @POST
    @Path("account/{from_id}/transfers/to/{to_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String transfer(@PathParam("from_id") String sender, @PathParam("to_id") String recipient, TransferOperation transfer) {

        IClient from = new IClient() {
            @Override
            public String getId() {
                return sender;
            }
        };

        IClient to = new IClient() {
            @Override
            public String getId() {
                return recipient;
            }
        };

       Service.bank.transfer(transfer.amount, from, AccountType.valueOf(transfer.senderAccountType), to, AccountType.valueOf(transfer.recipientAccountType));
       return "{\"message\": \"Transfer done.\"}";
    }

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

    @GET
    @Path("account/{client_id}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionListResponse getTransactions(@PathParam("client_id") final String client) {

        IClient accountOwner = new IClient() {
            @Override
            public String getId() {
                return client;
            }
        };

        List<Transaction> response = Service.bank.getClientTransactions(accountOwner);

        TransactionListResponse transactions = new TransactionListResponse();
        for (Transaction transaction : response) {
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.amount = transaction.getAmount();
            transactionResponse.description = transaction.getDescription();
            transactionResponse.timestamp = transaction.getTimestamp();
            transactions.allTransactions.add(transactionResponse);
        }

        return transactions;

    }

}