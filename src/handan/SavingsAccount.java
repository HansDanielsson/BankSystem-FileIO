/**
 * Klass som definierar ett bankkonto.
 * Modellen ärver egenskaper från Account
 * Begränsning på ett fritt uttag/år
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account implements Serializable {

  /**
   * Versionshanterings variabel till deserialisering
   */
  private static final long serialVersionUID = 611114L;

  // Variabler för enskilt sparkonto
  private boolean hasMadeWithdrawal; // Första uttaget är fritt, ingen uttagsränta.
  private final BigDecimal withdrawRate; // Ex: 2.0 = 2%

  protected SavingsAccount() {
    this(0, 2.4, 2.0, false);
  }

  /**
   * Skapa ett nytt Sparkonto
   *
   * @param initialBalance      , Startbelopp
   * @param interestRate        , Ränta 2.4% på insatta pengar > 0
   * @param withdrawRatePercent , Uttagsränta 2% på beloppet efter första uttaget.
   * @param addNumber           , Om kontonummer ska ökas.
   */
  protected SavingsAccount(int initialBalance, double interestRate, double withdrawRatePercent, boolean addNumber) {
    super("Sparkonto", initialBalance, interestRate, addNumber);
    this.withdrawRate = BigDecimal.valueOf(withdrawRatePercent);
  }

  /**
   * Beräknar räntan baserat på saldo och räntesats.
   *
   * @return Ränta i formaterad valuta
   */
  @Override
  protected String calculateInterest() {
    BigDecimal interest = getAccountBalance().multiply(getInterestRate()).divide(BigDecimal.valueOf(100), 2,
        RoundingMode.HALF_UP);
    return formatCurrency(interest);
  }

  /**
   * Gör ett uttag. Det första uttaget är avgiftsfritt. Därefter tillkommer
   * uttagsavgift.
   *
   * @param amount Belopp att ta ut (måste vara > 0 och finnas på kontot)
   * @return true om uttaget lyckades
   */
  @Override
  protected boolean withdraw(int amount) {
    // Tidig return om beloppet är negativt
    if (amount <= 0) {
      return false;
    }

    BigDecimal withdrawal = BigDecimal.valueOf(amount);

    // Justera beloppet efter första uttaget
    if (hasMadeWithdrawal) {
      BigDecimal fee = withdrawal.multiply(withdrawRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      withdrawal = withdrawal.add(fee);
    }

    if (getAccountBalance().compareTo(withdrawal) < 0) {
      return false; // Inte tillräckligt med pengar
    }
    hasMadeWithdrawal = true;
    return balanceSubtract(withdrawal);
  }
}