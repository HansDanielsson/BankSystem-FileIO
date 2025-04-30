/**
 * Klass som definierar ett bankkonto.
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstrakt klass för bankkonto.
 */
public abstract class Account implements Serializable {

  /**
   * Versionshanterings variabel till deserialisering
   */
  private static final long serialVersionUID = 611114L;
  private static final int START_ACCOUNT_NUMBER = 1000; // Startvärde för kontonummer
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final Locale SWEDISH_LOCALE = Locale.forLanguageTag("sv-SE");
  private static final String DEFAULT_ACCOUNT_NAME = "Sparkonto";

  /**
   * Variabel som är gemensam för alla konton
   */
  private static int lastAssignedNumber = START_ACCOUNT_NUMBER; // Kontonummer

  /**
   * Protected hjälprutin till Number som byter "," till "." Underlättar vid
   * kommande listor som är med avgränsare ,
   *
   * @param value
   * @return Nu med punkt
   */
  protected static String formatCurrency(Number value) {
    var numberFormat = NumberFormat.getCurrencyInstance(SWEDISH_LOCALE);
    return numberFormat.format(value).replace(',', '.');
  }

  /**
   * Hjälprutin som hämtar senaste kontonummer
   *
   * @return lastAssignedNumber
   */
  protected static int getLastAssignedNumber() {
    return lastAssignedNumber;
  }

  /**
   * Hjälprutin som sätter senaste kontonummer
   *
   * @param number
   */
  protected static void setLastAssignedNumber(int number) {
    lastAssignedNumber = number;
  }

  /**
   * Variabler för enskilda konton
   */
  private final int accountNumber; // 1001, 1002, 1003, 1004 osv.
  private final String accountType;
  private BigDecimal balance;
  private final BigDecimal interestRate;
  private final List<String> transactions;

  /**
   * Default Konstruktor för ett nytt bankkonto
   */
  protected Account() {
    this(DEFAULT_ACCOUNT_NAME, 0, 2.4, false);
  }

  /**
   * Konstruktor för nytt bankkonto
   *
   * @param accountType  , Sparkonto eller Kreditkonto
   * @param balance      , start belopp
   * @param interestRate , 2.4% eller 1.1% på insatta pengar
   * @param addNumber
   */
  protected Account(String accountType, int balance, double interestRate, boolean addNumber) {
    if (addNumber) {
      lastAssignedNumber++; // Ska bara räknas upp med 1 ibland.
    }
    this.accountNumber = lastAssignedNumber;
    this.accountType = accountType;
    this.balance = BigDecimal.valueOf(balance);
    this.interestRate = BigDecimal.valueOf(interestRate);
    this.transactions = new ArrayList<>();
  }

  /**
   * Rutin som tar bort beloppet (amount) från saldo (balance)
   *
   * @param amount
   * @return om det gick bra
   */
  protected boolean balanceSubtract(BigDecimal amount) {
    return updateBalance(amount.negate());
  }

  /**
   * Abstrakt metod för att beräkna räntan, implementeras i subklasserna.
   *
   * @return x xxx kr
   */
  protected abstract String calculateInterest();

  /**
   * Rutin för att tömma transaktionshistoriken.
   */
  protected void deleteTransactions() {
    transactions.clear();
  }

  /**
   * Rutin som sätter in beloppet (amount) till saldo (balance) Kontroll har redan
   * utförts på amount > 0
   *
   * @param amount
   * @return true hela tiden för att amount > 0
   */
  protected boolean deposit(BigDecimal amount) {
    return updateBalance(amount);
  }

  protected BigDecimal getAccountBalance() {
    return balance;
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
  protected BigDecimal getInterestRate() {
    return interestRate;
  }

  /**
   * Vid bearbetning av kontot med kontonummer saldo kontotyp.
   *
   * @return "kontonr saldo kontotyp <procent %>"
   */
  protected String infoAccount() {
    return String.format("%d %s %s", accountNumber, formatCurrency(balance), accountType);
  }

  /**
   * Rutin som räknar ut räntan på kontot Räntan är olika beroende på belopp och
   * kontotyp.
   *
   * @param rate , Räntan som gäller till beloppet
   * @return
   */
  protected String makeAccountInfo(BigDecimal rate) {
    var percentFormat = NumberFormat.getPercentInstance(SWEDISH_LOCALE);
    percentFormat.setMaximumFractionDigits(1); // Anger att vi vill ha max 1 decimal
    var strPercent = percentFormat.format(rate.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP)).replace(',',
        '.');
    return String.format("%d %s %s %s", accountNumber, formatCurrency(balance), accountType, strPercent);
  }

  /**
   * Privatrutin för att registrera en transaktion med datum, föndrat belopp och
   * nytt saldo.
   *
   * @param amount
   */
  private void makeTransaction(BigDecimal amount) {
    var oneTransaction = String.format("%s %s Saldo: %s", DATE_FORMAT.format(LocalDateTime.now()),
        formatCurrency(amount), formatCurrency(balance));
    transactions.add(oneTransaction);
  }

  /**
   * Vid utskrift av kontot med kontonummer saldo kontotyp, percent.
   *
   * @return "kontonr saldo kontotyp procent %"
   */
  @Override
  public String toString() {
    return makeAccountInfo(interestRate);
  }

  /**
   * Privat hjälprutin för att uppdatera saldot och registrera transaktionen.
   *
   * @param change
   * @return true om det gick bra
   */
  private boolean updateBalance(BigDecimal change) {
    if (change == null) {
      return false;
    }
    balance = balance.add(change);
    makeTransaction(change);
    return true;
  }

  /**
   * Abstrakt metod för uttag, implementeras i subklasserna.
   */
  protected abstract boolean withdraw(int amount);
}