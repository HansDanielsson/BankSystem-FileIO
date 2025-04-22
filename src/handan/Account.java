/**
 * Klass som definierar ett bankkonto.
 * @author Hans Danielsson, handan-2
 */
package handan;

// Importsatser
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Klassdeklarationer för Kontot
public abstract class Account {

  // Variabler som är gemensamt för alla konton
  private static int lastAssignedNumber = 1000;
  private static String accountName = "Sparkonto";

  // Variabler för enskilda konton
  private int accountNumber; // 1001, 1002, 1003, 1004 osv.
  private String accountType;
  private BigDecimal balance;
  private BigDecimal interestRate;
  private List<String> transactions = null;

  // Default Konstruktor för ett nytt bankkonto
  protected Account() {
    this(accountName, 0, 2.4, false);
  }

  /**
   * Konstruktor för nytt bankkonto
   *
   * @param theAccountType  , Sparkonto eller Kreditkonto
   * @param theBalance      , start belopp
   * @param theInterestRate , 2.4% eller 1.1% på insatta pengar
   * @param addNumber
   */
  protected Account(String theAccountType, int theBalance, double theInterestRate, boolean addNumber) {
    if (addNumber) {
      lastAssignedNumber++; // Ska bara räknas upp med 1 ibland.
      transactions = new ArrayList<>();
    }
    accountNumber = lastAssignedNumber;
    accountType = theAccountType;
    balance = BigDecimal.valueOf(theBalance);
    interestRate = BigDecimal.valueOf(theInterestRate);
  }

  /**
   * Rutin som tar bort beloppet (theAmount) från saldo (balance) Använder
   * BigDecimal
   *
   * @param theAmount
   * @return om det gick bra
   */
  protected boolean balanceSubtract(int theAmount) {
    try {
      balance = balance.subtract(BigDecimal.valueOf(theAmount));
      // Skapa transaktionen och spara den
      makeTransaction(-theAmount);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Rutin som beräknar räntan beroende om Spar- eller Kredit-konto Är abstrakt
   * och definieras senare
   *
   * @return x xxx kr
   */
  protected abstract String calculateInterest();

  /**
   * Rutin som sätter in beloppet (theAmount) till saldo (balance) Kontroll har
   * redan utförts på theAmount > 0
   *
   * @param theAmount
   * @return true hela tiden för att theAmount > 0
   */
  protected boolean deposit(int theAmount) {
    try {
      balance = balance.add(BigDecimal.valueOf(theAmount));
      // Skapa transaktion och spara den
      makeTransaction(theAmount);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  protected int getAccountBalance() {
    return balance.intValue();
  }

  /**
   * Hämtar kontonummer
   *
   * @return accountNumber
   */
  protected int getAccountNumber() {
    return accountNumber;
  }

  /**
   * Hämtar pekare till en lista med transaktioner
   *
   * @return pekare
   */
  protected List<String> getAccountTransactions() {
    return transactions;
  }

  /**
   * Hämtar räntan på insatta pengar
   *
   * @return double värdet
   */
  protected double getInterestRate() {
    return interestRate.doubleValue();
  }

  /**
   * Vid bearbetning av kontot med kontonummer saldo kontotyp.
   *
   * @return "kontonr saldo kontotyp <procent %>"
   */
  protected String infoAccount() {
    return accountNumber + " " + makePointCurrency(balance) + " " + accountType;
  }

  /**
   * Rutin som räknar ut räntan på kontot Räntan är olika beroende på belopp och
   * kontotyp.
   *
   * @param theInterestRate , Räntan som gäller till beloppet
   * @return
   */
  protected String makeAccountInfo(double theInterestRate) {
    NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.of("SV", "SE"));
    percentFormat.setMaximumFractionDigits(1); // Anger att vi vill ha max 1 decimal
    String strPercent = percentFormat.format(theInterestRate / 100.0);
    strPercent = strPercent.replace(',', '.'); // Men decimal punkt
    return accountNumber + " " + makePointCurrency(balance) + " " + accountType + " " + strPercent;
  }

  /**
   * Privat hjälprutin till BigDecimal som byter "," till "." Underlättar vid
   * kommande listor som är med avgränsare ,
   *
   * @param str
   * @return Nu med punkt
   */
  private String makePointCurrency(BigDecimal theValue) {
    String str = NumberFormat.getCurrencyInstance(Locale.of("SV", "SE")).format(theValue);
    str = str.replace(',', '.');
    return str;
  }

  /**
   * Protected hjälprutin till double som byter "," till "." Underlättar vid
   * kommande listor som är med avgränsare ,
   *
   * @param str
   * @return Nu med punkt
   */
  protected String makePointCurrency(double theValue) {
    String str = NumberFormat.getCurrencyInstance(Locale.of("SV", "SE")).format(theValue);
    str = str.replace(',', '.');
    return str;
  }

  /**
   * Protected hjälprutin till int som byter "," till "." Underlättar vid kommande
   * listor som är med avgränsare ,
   *
   * @param str
   * @return Nu med punkt
   */
  protected String makePointCurrency(int theValue) {
    String str = NumberFormat.getCurrencyInstance(Locale.of("SV", "SE")).format(theValue);
    str = str.replace(',', '.');
    return str;
  }

  /**
   * Hjälpmetod att skapa en transaktion, gäller både för spar- och kredit-konto
   * Skapa texten yyyy-MM-dd HH:mm:ss -500.00 kr Saldo: -500.00 kr, Lägg till det
   * i transaktionslistan
   *
   * @param theAmount
   */
  private void makeTransaction(int theAmount) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime date = LocalDateTime.now();
    String strDate = date.format(formatter);
    String strBalance = makePointCurrency(balance);
    String strAmount = makePointCurrency(theAmount);

    String oneTransaction = strDate + " " + strAmount + " Saldo: " + strBalance;
    transactions.add(oneTransaction);
  }

  /**
   * Vid utskrift av kontot med kontonummer saldo kontotyp, percent.
   *
   * @return "kontonr saldo kontotyp procent %"
   */
  @Override
  public String toString() {
    return makeAccountInfo(interestRate.doubleValue());
  }

  /**
   * Rutin som tar bort beloppet (amount) från saldo (balance) belopet ska vara >
   * 0 och att beloppet finns på saldo Det är olika beräkningar beroende på spar-
   * eller kredit-konto därför ör rutinen abstrakt och den skapas senare.
   *
   * @param theAmount
   * @return om beloppet har minskat saldo
   */
  protected abstract boolean withdraw(int theAmount);
}