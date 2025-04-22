/**
 * Klass som definierar en lista med kunder.
 * @author Hans Danielsson, handan-2
 */
package handan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankLogic {

  private List<Customer> bankCustomer = new ArrayList<>();

  /**
   * Rutin som byter namnet på en kund med pNo
   *
   * @param name
   * @param surname
   * @param pNo
   * @return om bytet är utfört.
   */
  public boolean changeCustomerName(String name, String surname, String pNo) {
    if ((name.isEmpty()) && (surname.isEmpty())) {
      return false;
    }

    Customer changeCustomer = getSearchCustomer(pNo);
    if (changeCustomer == null) {
      return false;
    }

    return changeCustomer.changeCustomerName(name, surname);
  }

  /**
   * Rutin på konto för att ta bort transaktioner och stänga för en kund
   *
   * @param pNo
   * @param accountId
   * @return "kontonr belopp kontotyp ränta"
   */
  public String closeAccount(String pNo, int accountId) {
    Customer closeCustomer = getSearchCustomer(pNo);
    if (closeCustomer == null) {
      return null;
    }

    Account account = getSearchAccount(closeCustomer.getAccounts(), accountId);
    if (account == null) {
      return null;
    }

    String result = account.infoAccount() + " " + account.calculateInterest();
    // Ta bort Transaktionerna
    account.getAccountTransactions().clear();
    closeCustomer.getAccounts().remove(account);
    return result;
  }

  /**
   * Skapar ett kreditkonto för person pNo
   *
   * @param pNo
   * @return -1 = Hittar inte pNo, annars kreditkontonummer
   */
  public int createCreditAccount(String pNo) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return -1;
    }

    if (customer.getAccounts() == null) {
      customer.setAccounts();
    }

    Account newAccount = new CreditAccount(0, 1.1, 5000, 5.0, true); // Här räknas kontonummer.
    customer.getAccounts().add(newAccount);

    return newAccount.getAccountNumber();
  }

  /**
   * Rutin för att skapa en ny kund
   *
   * @param name
   * @param surname
   * @param pNo
   * @return om kund är ny
   */
  public boolean createCustomer(String name, String surname, String pNo) {
    // Kontroll att kunden inte finns redan.
    if (getSearchCustomer(pNo) != null) {
      return false;
    }
    // Ny kund till listan
    return bankCustomer.add(new Customer(name, surname, pNo));
  }

  /**
   * Skapar ett konto för person pNo
   *
   * @param pNo
   * @return -1 = Hittar inte pNo, annars kontonummer
   */
  public int createSavingsAccount(String pNo) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return -1;
    }

    if (customer.getAccounts() == null) {
      customer.setAccounts();
    }

    Account newAccount = new SavingsAccount(0, 2.4, 2.0, true); // Här räknas kontonummer.
    customer.getAccounts().add(newAccount);

    return newAccount.getAccountNumber();
  }

  /**
   * Rutin som tar bort en kund och dess konton Returnerar en oföränderlig lista
   * med resultat
   *
   * @param pNo
   * @return "pNr f-Namn E-namn, KontoNr Typ Saldo Kr,..."
   */
  public List<String> deleteCustomer(String pNo) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return null;
    }

    // Skapa en ny lista med kundens data och konton
    List<String> deList = new ArrayList<>();
    deList.add(customer.toString());

    if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) { // Kund har konton
      for (Account account : customer.getAccounts()) {
        deList.add(account.infoAccount() + " " + account.calculateInterest());
        // Ta bort Transaktionerna
        account.getAccountTransactions().clear();
      }
      // Ta bort kontot
      customer.getAccounts().clear();
    }
    bankCustomer.remove(customer);
    return List.copyOf(deList);
  }

  /**
   * Gör en insättning på konto med kontonummer som tillhör kunden med personnr
   *
   * @param pNo
   * @param accountId
   * @param amount
   * @return True om det gick bra
   */
  public boolean deposit(String pNo, int accountId, int amount) {
    if (amount <= 0) {
      return false;
    }

    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return false;
    }

    Account account = getSearchAccount(customer.getAccounts(), accountId);
    return account != null && account.deposit(amount);
  }

  /**
   * Rutin som returnerar en String som innehåller "kontonr saldo typ ränta"
   *
   * @param pNo
   * @param accountId
   * @return om accountid = kundens konto
   */
  public String getAccount(String pNo, int accountId) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return null;
    }

    Account account = getSearchAccount(customer.getAccounts(), accountId);
    return account == null ? null : account.toString();
  }

  /**
   * Lab 3 : Ny rutin som hämtar alla konton för en kund(pNo)
   *
   * @param pNo
   * @return
   */
  public List<String> getAccountList(String pNo) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null || customer.getAccounts() == null) {
      return Collections.emptyList();
    }

    return customer.getAccounts().stream().map(acc -> String.valueOf(acc.getAccountNumber()))
        .collect(Collectors.toList());
  }

  /**
   * Rutin som returnerar en lista med strängar som innehåller alla kunder
   *
   * @return , finns inga kunder blir den tom lista []
   */
  public List<String> getAllCustomers() {
    return bankCustomer.stream().map(Customer::toString).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Rutin som tar fram en kunds information och denns konton.
   *
   * @param pNo
   * @return lista på bortagna poster.
   */
  public List<String> getCustomer(String pNo) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return null;
    }

    return Stream
        .concat(Stream.of(customer.toString()),
            customer.getAccounts() == null ? Stream.empty() : customer.getAccounts().stream().map(Account::toString))
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Hjälpmetod som letar reda på ett konto
   *
   * @param accounts         , Lista med konton
   * @param theAccountNumber , som söks upp
   * @return result , pekare till konto om det finns.
   */
  private Account getSearchAccount(List<Account> accounts, int theAccountNumber) {
    if (accounts == null) {
      return null;
    }

    return accounts.stream().filter(acc -> acc.getAccountNumber() == theAccountNumber).findFirst().orElse(null);
  }

  /**
   * Hjälpmetod som letar reda på en kund med hjälp av pNr som är unikt.
   *
   * @param theSearchNo
   * @return pekare till kundens post om den finns.
   */
  private Customer getSearchCustomer(String theSearchNo) {
    if (theSearchNo == null || theSearchNo.isEmpty()) {
      return null;
    }

    return bankCustomer.stream().filter(customer -> theSearchNo.equals(customer.getPersonalNumber())).findFirst()
        .orElse(null);
  }

  /**
   * Hämtar en lista som innehåller presentation av alla transaktioner
   *
   * @param pNo
   * @param accountId
   * @return null or List<>
   */
  public List<String> getTransactions(String pNo, int accountId) {
    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return null;
    }

    Account account = getSearchAccount(customer.getAccounts(), accountId);
    if (account == null) {
      return null;
    }

    return List.copyOf(account.getAccountTransactions());
  }

  /**
   * Gör ett uttag på kontot för en kund.
   *
   * @param pNo
   * @param accountId
   * @param amount
   * @return true if ok
   */
  public boolean withdraw(String pNo, int accountId, int amount) {
    if (amount <= 0) {
      return false;
    }

    Customer customer = getSearchCustomer(pNo);
    if (customer == null) {
      return false;
    }

    Account account = getSearchAccount(customer.getAccounts(), accountId);
    if (account == null) {
      return false;
    }

    return account.withdraw(amount);
  }
}