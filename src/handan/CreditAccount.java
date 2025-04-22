/**
 * Klass som definierar ett kreditkonto.
 * Modellen ärver egenskaper från Account
 * @author Hans Danielsson, handan-2
 */
package handan;

public class CreditAccount extends Account {

  // Variabler för enskilt kreditkonto
  private int creditLimit; // Kreditgräns (5000 kr)
  private double deptInterest; // Skuldränta 5% om saldo < 0.

  protected CreditAccount() {
    this(0, 1.1, 5000, 5.0, false);
  }

  /**
   * Skapa ett Kreditkonto
   *
   * @param theBalance      , Startkapital
   * @param theInterestRate , Räntan 1.1% på insatta pengar > 0
   * @param theCreditLimit  , Kreditgräns på 5000, kan ta ut pengar till belopp
   *                        -5000 kr
   * @param theDeptInterest , Skuldränta 5% om saldo < 0
   * @param addNumber       , Öka kontonummer med 1
   */
  protected CreditAccount(int theBalance, double theInterestRate, int theCreditLimit, double theDeptInterest,
      boolean addNumber) {
    super("Kreditkonto", theBalance, theInterestRate, addNumber);
    creditLimit = theCreditLimit;
    deptInterest = theDeptInterest;
  }

  /**
   * Rutin som beräknar räntan på Kredit-konto Olika beroende på saldo beloppet
   *
   * @return x xxx kr
   */
  @Override
  protected String calculateInterest() {
    int balance = getAccountBalance();
    double interestRate = balance > 0 ? getInterestRate() : deptInterest;
    double numberInterest = balance * interestRate / 100.0;
    return makePointCurrency(numberInterest);
  }

  @Override
  public String toString() {
    int balanceValue = getAccountBalance();
    if (balanceValue < 0) {
      return makeAccountInfo(deptInterest);
    }
    return super.toString();
  }

  /**
   * Rutin som tar bort beloppet (amount) från saldo (balance) belopet ska vara >
   * 0 och att beloppet är lägst creditLimit
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

    // Ta bort theAmount från balance, min -creditLimit
    return (getAccountBalance() - theAmount + creditLimit >= 0) && balanceSubtract(theAmount);
  }
}