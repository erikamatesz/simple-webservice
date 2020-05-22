package br.com.radixeng.treinamentojava.app;
import java.util.List;
import java.util.Map;

public interface IBank {

    Boolean createAccount(IClient client, AccountType type);

    void deposit(Double amount, IClient client, AccountType type);

    Double withdraw(Double amount, IClient client, AccountType type);

    void transfer(Double amount, IClient sender, AccountType senderType, IClient recipient, AccountType recipientType);

    String showAllBalances();

    Map<AccountType, Account> getClientAccounts(IClient client);

    List<Transaction> getClientTransactions(IClient client);

}