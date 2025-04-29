/**
 * Klass som definierar ett kreditkonto.
 * Modellen ärver egenskaper från Account
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditAccount extends Account implements Serializable {

  /**
   * Versionshanterings variabel till deserialisering
   */
  private static final long serialVersionUID = 611114L;

  // Variabler för enskilt kreditkonto
  private final BigDecimal creditLimit; // Max kredit, t.ex. -5000 kr
  private final BigDecimal debtInterest; // Skuldränta, t.ex. 5%

  protected CreditAccount() {
    this(0, 1.1, 5000, 5.0, false);
  }

  /**
   * Skapa ett Kreditkonto
   *
   * @param initialBalance  Startsaldo
   * @param theInterestRate Räntan 1.1% på insatta pengar > 0
   * @param theCreditLimit  Kreditgräns på 5000, kan ta ut pengar till belopp
   *                        -5000 kr
   * @param theDeptInterest Skuldränta 5% om saldo < 0
   * @param addNumber       Öka kontonummer med 1
   */
  protected CreditAccount(int initialBalance, double interestRate, int creditLimit, double debtInterest,
      boolean addNumber) {
    super("Kreditkonto", initialBalance, interestRate, addNumber);
    this.creditLimit = BigDecimal.valueOf(creditLimit);
    this.debtInterest = BigDecimal.valueOf(debtInterest);
  }

  /**
   * Rutin som beräknar räntan på Kredit-konto Olika beroende på saldo beloppet
   *
   * @return Räntan i formaterad valuta
   */
  @Override
  protected String calculateInterest() {
    BigDecimal balance = getAccountBalance();
    BigDecimal rate = balance.signum() >= 0 ? getInterestRate() : debtInterest;
    BigDecimal interest = balance.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    return formatCurrency(interest);
  }

  @Override
  public String toString() {
    BigDecimal rate = getAccountBalance().signum() >= 0 ? getInterestRate() : debtInterest;
    return makeAccountInfo(rate);
  }

  /**
   * Uttag med kontroll av kreditgräns. Tillåter saldo ner till -creditLimit
   *
   * @param amount Belopp att ta ut (måste vara > 0)
   * @return true om uttaget kunde göras
   */
  @Override
  protected boolean withdraw(int amount) {
    // Tidig return om beloppet är negativt
    if (amount <= 0) {
      return false;
    }

    BigDecimal withdrawal = BigDecimal.valueOf(amount);
    BigDecimal newBalance = getAccountBalance().subtract(withdrawal);

    // Kontrollerar att nya saldot inte underskrider kreditgränsen
    return newBalance.compareTo(creditLimit.negate()) >= 0 && balanceSubtract(withdrawal);
  }
}