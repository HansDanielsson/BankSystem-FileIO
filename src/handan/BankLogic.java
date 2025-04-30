/**
 * Klass som definierar en lista med kunder.
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankLogic implements Serializable {

  /**
   * Versionshanterings variabel till deserialisering
   */
  private static final long serialVersionUID = 611114L;

  /**
   * Hjälpmetod som letar reda på ett konto
   *
   * @param accounts         , Lista med konton
   * @param theAccountNumber , som söks upp
   * @return result , pekare till konto om det finns.
   */
  private static Account findAccount(List<Account> accounts, int accountId) {
    return accounts.stream().filter(a -> a.getAccountNumber() == accountId).findFirst().orElse(null);
  }

  /**
   * customers kan inte vara static, ska sparas på fil
   */
  private List<Customer> customers = new ArrayList<>();

  /**
   * Rutin som byter namnet på en kund med pNo
   *
   * @param name
   * @param surname
   * @param pNo
   * @return om bytet är utfört.
   */
  public boolean changeCustomerName(String name, String surname, String pNo) {
    if ((name.isBlank()) && (surname.isBlank())) {
      return false;
    }
    var customer = findCustomer(pNo);
    return customer != null && customer.changeCustomerName(name, surname);
  }

  /**
   * Rutin på konto för att ta bort transaktioner och stänga för en kund
   *
   * @param pNo
   * @param accountId
   * @return "kontonr belopp kontotyp ränta"
   */
  public String closeAccount(String pNo, int accountId) {
    var closeCustomer = findCustomer(pNo);
    if (closeCustomer == null) {
      return null;
    }

    var account = findAccount(closeCustomer.getAccounts(), accountId);
    if (account == null) {
      return null;
    }

    var result = account.infoAccount() + " " + account.calculateInterest();
    // Ta bort Transaktionerna
    account.deleteTransactions();
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
    var customer = findCustomer(pNo);
    if (customer == null) {
      return -1;
    }

    var account = new CreditAccount(0, 1.1, 5000, 5.0, true); // Här räknas kontonummer.
    customer.getAccounts().add(account);

    return account.getAccountNumber();
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
    if (findCustomer(pNo) != null) {
      return false;
    }
    // Ny kund till listan
    return customers.add(new Customer(name, surname, pNo));
  }

  /**
   * Skapar ett konto för person pNo
   *
   * @param pNo
   * @return kontonummer om kunden hittas, annars -1
   */
  public int createSavingsAccount(String pNo) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return -1;
    }

    var account = new SavingsAccount(0, 2.4, 2.0, true); // Här räknas kontonummer.
    customer.getAccounts().add(account);

    return account.getAccountNumber();
  }

  /**
   * Rutin som tar bort en kund och dess konton Returnerar en oföränderlig lista
   * med resultat
   *
   * @param pNo
   * @return "pNr f-Namn E-namn, KontoNr Typ Saldo Kr,..."
   */
  public List<String> deleteCustomer(String pNo) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return null;
    }

    // Skapa en ny lista med kundens data och konton
    List<String> result = new ArrayList<>();
    result.add(customer.toString());

    var accounts = customer.getAccounts();
    if (!accounts.isEmpty()) { // Kund har konton
      accounts.forEach(account -> {
        result.add(account.infoAccount() + " " + account.calculateInterest());
        // Ta bort Transaktionerna
        account.deleteTransactions();
      });
      // Ta bort kontot
      customer.deleteAccounts();
    }
    customers.remove(customer);
    return List.copyOf(result);
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

    var customer = findCustomer(pNo);
    if (customer == null) {
      return false;
    }

    var account = findAccount(customer.getAccounts(), accountId);
    return account != null && account.deposit(BigDecimal.valueOf(amount));
  }

  /**
   * Hjälpmetod som letar reda på en kund med hjälp av pNr som är unikt. Kan inte
   * vara static
   *
   * @param theSearchNo
   * @return pekare till kundens post om den finns.
   */
  private Customer findCustomer(String pNo) {
    if (pNo == null || pNo.isBlank()) {
      return null;
    }

    return customers.stream().filter(c -> pNo.equals(c.getPersonalNumber())).findFirst().orElse(null);
  }

  /**
   * Rutin som returnerar en String som innehåller "kontonr saldo typ ränta"
   *
   * @param pNo
   * @param accountId
   * @return om accountid = kundens konto
   */
  public String getAccount(String pNo, int accountId) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return null;
    }

    var account = findAccount(customer.getAccounts(), accountId);
    return account == null ? null : account.toString();
  }

  /**
   * Lab 3 : Ny rutin som hämtar alla konton för en kund(pNo)
   *
   * @param pNo
   * @return
   */
  public List<String> getAccountList(String pNo) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return Collections.emptyList();
    }

    return customer.getAccounts().stream().map(a -> String.valueOf(a.getAccountNumber())).collect(Collectors.toList());
  }

  /**
   * Rutin som returnerar en lista med strängar som innehåller alla kunder
   *
   * @return , finns inga kunder blir den tom lista []
   */
  public List<String> getAllCustomers() {
    return customers.stream().map(Customer::toString).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Rutin som returnerar alla kunder i orginal listan. Behövs för att kunna ta
   * bort allt i banken
   *
   * @return , customers
   */
  protected List<Customer> getAllCustomersList() {
    return customers;
  }

  /**
   * Rutin som tar fram en kunds information och denns konton.
   *
   * @param pNo
   * @return lista på bortagna poster.
   */
  public List<String> getCustomer(String pNo) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return null;
    }

    return Stream.concat(Stream.of(customer.toString()), customer.getAccounts().stream().map(Account::toString))
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Hämtar en lista som innehåller presentation av alla transaktioner
   *
   * @param pNo
   * @param accountId
   * @return null or List<>
   */
  public List<String> getTransactions(String pNo, int accountId) {
    var customer = findCustomer(pNo);
    if (customer == null) {
      return null;
    }

    var account = findAccount(customer.getAccounts(), accountId);
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

    var customer = findCustomer(pNo);
    if (customer == null) {
      return false;
    }

    var account = findAccount(customer.getAccounts(), accountId);
    if (account == null) {
      return false;
    }

    return account.withdraw(amount);
  }
}