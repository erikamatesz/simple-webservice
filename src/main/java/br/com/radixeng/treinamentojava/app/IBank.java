package br.com.radixeng.treinamentojava.app;
import java.util.Map;

public interface IBank {

    Boolean createAccount(IClient client, AccountType type);

    void deposit(Double amount, IClient client, AccountType type);

    public Double withdraw(Double amount, IClient client, AccountType type);

    String showAllBalances();

    Map<AccountType, Account> getClientAccounts(IClient client);

}