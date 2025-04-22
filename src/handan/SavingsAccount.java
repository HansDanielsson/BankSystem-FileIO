/**
 * Klass som definierar ett bankkonto.
 * Modellen ärver egenskaper från Account
 * Begränsning på ett fritt uttag/år
 * @author Hans Danielsson, handan-2
 */
package handan;

public class SavingsAccount extends Account {

  // Variabler för enskilt sparkonto
  private boolean notFirstFree; // Första uttaget är fritt, ingen uttagsränta.
  private double withdrawRate; // Uttagsränta, max 100%: 2.0 är 2% av uttaget belopp

  protected SavingsAccount() {
    this(0, 2.4, 2.0, false);
  }

  /**
   * Skapa ett Sparkonto
   *
   * @param theBalance      , Startkapital
   * @param theInterestRate , Ränta 2.4% på insatta pengar > 0
   * @param theWithdrawRate , Uttagsränta 2% på beloppet
   * @param addNumber       , Öka kontonummer med 1
   */
  protected SavingsAccount(int theBalance, double theInterestRate, double theWithdrawRate, boolean addNumber) {
    super("Sparkonto", theBalance, theInterestRate, addNumber);
    notFirstFree = false;
    withdrawRate = theWithdrawRate;
  }

  /**
   * Rutin som beräknar räntan på Spar-konto
   *
   * @return x xxx kr
   */
  @Override
  protected String calculateInterest() {
    int balance = getAccountBalance();
    double interestRate = getInterestRate();
    double numberInterest = balance * interestRate / 100.0;
    return makePointCurrency(numberInterest);
  }

  /**
   * Rutin som tar bort beloppet (amount) från saldo (balance) belopet ska vara >
   * 0 och att uttagsränta beräknas efter första. Beloppet finns på saldo
   *
   * @param theAmount
   * @return om beloppet har minskat saldo
   */
  @Override
  protected boolean withdraw(int theAmount) {
    // Tidig return om beloppet är negativt
    if (theAmount <= 0) {
      return false;
    }

    // Justera beloppet efter första uttaget
    if (notFirstFree) {
      theAmount += (theAmount * withdrawRate / 100.0);
    }

    notFirstFree = true;

    return (getAccountBalance() - theAmount >= 0) && balanceSubtract(theAmount);
  }
}