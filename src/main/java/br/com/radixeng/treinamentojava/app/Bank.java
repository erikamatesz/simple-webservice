package br.com.radixeng.treinamentojava.app;
import java.util.*;

class InvalidAccountType extends Exception {

}

class Transaction {

    private Double amount;
    private Date timestamp;
    private String description;

    public Double getAmount() {
        return this.amount;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getDescription() { return this.description; }

    Transaction(Double amount, Date timestamp, String description) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
    }

    static Double getBalance(List<Transaction> transactions) {

        Double balance = 0.0;

        for (Transaction transaction : transactions) {
            balance += transaction.getAmount();
        }

        return balance;
    }
}

class Account {

    protected List<Transaction> transactions;

    protected Double addTransaction(Double amount, String description) {

        Transaction transaction = new Transaction(amount, new Date(), description);
        transactions.add(transaction);
        return amount;
    
    }

    public List<Transaction> getTransactions() {

        return transactions;

    }

    public void deposit(Double amount) {
        this.addTransaction(amount, "Deposit");
    }

    public Double withdraw(Double amount) {
        return this.addTransaction(-amount, "Withdraw");
    }

    public void transfer(Double amount, Account recipientAccount) {
        this.withdraw(amount);
        recipientAccount.deposit(amount);
    }

    public Double getBalance() {
        return Transaction.getBalance(this.transactions);
    }

    public void checkBalance() {
        System.out.println("Current balance $ " + this.getBalance());
    }

    Account() {
        this.transactions = new ArrayList<>();
    }

    public static Account createAccount(AccountType type) throws InvalidAccountType {

        switch (type) {
            case CHECKING:
                return new CheckingAccount();
            case SAVINGS:
                return new SavingsAccount();
            case MONEY_MARKET:
                return new MoneyMarketAccount();
            case OTHER:
                return new OtherAccount();
        }

        throw new InvalidAccountType();
    }
}

class CheckingAccount extends Account { 

}

class SavingsAccount extends Account { 

}

class OtherAccount extends Account { 

}

class MoneyMarketAccount extends Account {

    public Double withdraw(Double amount) {

        List<Transaction> transactions30DayOld = new ArrayList<>();

        for (Transaction transaction : this.transactions) {
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(transaction.getTimestamp());
            calendar.add(Calendar.DATE, 30);

            if (transaction.getAmount() < 0 || calendar.before(new Date())) {
                transactions30DayOld.add(transaction);
            }
        }

        Double balance = Transaction.getBalance(transactions30DayOld);
        if (balance > 0 && balance > amount) {
            this.withdraw(amount);
            return amount;
        }

        return 0.0;

    }

}

public class Bank implements IBank {

    private Map<String, Map<AccountType, Account>> clientAccountMap;

    private static Bank instance = new Bank();

    public static Bank getBank() {

        if (Bank.instance != null) {
            return Bank.instance;
        } else {
            Bank.instance = new Bank();
            return Bank.instance;
        }
    
    }

    private Bank() {
        this.clientAccountMap = new HashMap();
    }

    public Boolean createAccount(IClient client, AccountType type) {

        try {

            Account account = Account.createAccount(type);

            if (this.clientAccountMap.containsKey(client.getId())) {
                this.clientAccountMap.get(client.getId()).put(type, account);
            } else {
                Map<AccountType, Account> accounts = new HashMap<>();
                accounts.put(type, account);
                this.clientAccountMap.put(client.getId(), accounts);
            }

            return true;

        } catch (InvalidAccountType e) {

            System.out.println("Invalid account type.");
            return false;

        }

    }

    public String showAllBalances() {

        String message = "";

        for (String clientId: this.clientAccountMap.keySet()) {

            message += clientId + "\n";
            Map<AccountType, Account> types = this.clientAccountMap.get(clientId);
            for (AccountType type : types.keySet()) {
                message += type + "\n";
                message += "Current balance $ " + types.get(type).getBalance() + "\n";
            }
            message += "_____________________________\n";
        }

        return message;
    }


    public void deposit(Double amount, IClient client, AccountType type) {

        Account account = this.getClientAccount(type, client);
        account.deposit(amount);
    }

    public Double withdraw(Double amount, IClient client, AccountType type) {

        Account account = this.getClientAccount(type, client);

        try {

            account.withdraw(amount);

        } catch (java.lang.NullPointerException e) {

            System.out.println(client.getId() + ", an error occurred!");

        }

        return amount;
    }

    public void transfer(Double amount, IClient sender, AccountType senderType, IClient recipient, AccountType recipientType) {

        Account senderAccount = this.getClientAccount(senderType, sender);
        Account recipientAccount = this.getClientAccount(recipientType, recipient);

        try {

            senderAccount.transfer(amount, recipientAccount);

        } catch (java.lang.NullPointerException e) {

            System.out.println(sender.getId() + ", an error occurred!");

        }

    }

    public Map<AccountType, Account> getClientAccounts(IClient client) {

        return this.clientAccountMap.get(client.getId());

    }

    private Account getClientAccount(AccountType type, IClient client) {

        Map<AccountType, Account> types = this.clientAccountMap.get(client.getId());
        Account account = types.get(type);
        return account;

    }

    public List<Transaction> getClientTransactions(IClient client) {

        List<Transaction> transactions = new ArrayList<>();
        Map<AccountType, Account> types = this.clientAccountMap.get(client.getId());

        if (types.containsKey(AccountType.CHECKING)) {
            Account checking = types.get(AccountType.CHECKING);
            transactions.addAll(checking.transactions);
        }

        if (types.containsKey(AccountType.SAVINGS)) {
            Account savings = types.get(AccountType.SAVINGS);
            transactions.addAll(savings.transactions);
        }

        if (types.containsKey(AccountType.MONEY_MARKET)) {
            Account moneyMarket = types.get(AccountType.MONEY_MARKET);
            transactions.addAll(moneyMarket.transactions);
        }

        return transactions;

    }
}