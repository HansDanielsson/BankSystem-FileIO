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
   * @param theValue
   * @return Nu med punkt
   */
  protected static String formatCurrency(Number theValue) {
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(SWEDISH_LOCALE);
    return numberFormat.format(theValue).replace(',', '.');
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
   * @param theNumber
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
   * @param theAccountType  , Sparkonto eller Kreditkonto
   * @param theBalance      , start belopp
   * @param theInterestRate , 2.4% eller 1.1% på insatta pengar
   * @param addNumber
   */
  protected Account(String theAccountType, int theBalance, double theInterestRate, boolean addNumber) {
    if (addNumber) {
      lastAssignedNumber++; // Ska bara räknas upp med 1 ibland.
    }
    this.accountNumber = lastAssignedNumber;
    this.accountType = theAccountType;
    this.balance = BigDecimal.valueOf(theBalance);
    this.interestRate = BigDecimal.valueOf(theInterestRate);
    this.transactions = new ArrayList<>();
  }

  /**
   * Rutin som tar bort beloppet (amount) från saldo (balance)
   *
   * @param amount
   * @return om det gick bra
   */
  protected boolean balanceSubtract(BigDecimal amount) {
    if (amount == null) {
      return false;
    }
    balance = balance.subtract(amount);
    // Skapa transaktionen och spara den
    makeTransaction(amount.negate());
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
   * Rutin som tar bort dess transaktioner
   */
  protected void deleteTransactions() {
    // Tar bort alla transaktioner
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
    if (amount == null) {
      return false;
    }
    balance = balance.add(amount);
    // Skapa transaktion och spara den
    makeTransaction(amount);
    return true;
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
    return accountNumber + " " + formatCurrency(balance) + " " + accountType;
  }

  /**
   * Rutin som räknar ut räntan på kontot Räntan är olika beroende på belopp och
   * kontotyp.
   *
   * @param theInterestRate , Räntan som gäller till beloppet
   * @return
   */
  protected String makeAccountInfo(BigDecimal rate) {
    NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.of("SV", "SE"));
    percentFormat.setMaximumFractionDigits(1); // Anger att vi vill ha max 1 decimal
    String strPercent = percentFormat.format(rate.divide(BigDecimal.valueOf(100.0), 2, RoundingMode.HALF_UP))
        .replace(',', '.');
    return accountNumber + " " + formatCurrency(balance) + " " + accountType + " " + strPercent;
  }

  /**
   * Hjälpmetod att skapa en transaktion, gäller både för spar- och kredit-konto
   * Skapa texten yyyy-MM-dd HH:mm:ss -500.00 kr Saldo: -500.00 kr, Lägg till det
   * i transaktionslistan
   *
   * @param theAmount
   */
  private void makeTransaction(BigDecimal amount) {
    String oneTransaction = String.format("%s %s Saldo: %s", DATE_FORMAT.format(LocalDateTime.now()),
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
   * Rutin som tar bort beloppet (amount) från saldo (balance) belopet ska vara >
   * 0 och att beloppet finns på saldo Det är olika beräkningar beroende på spar-
   * eller kredit-konto därför ör rutinen abstrakt och den skapas senare.
   *
   * @param theAmount
   * @return om beloppet har minskat saldo
   */
  protected abstract boolean withdraw(int theAmount);
}