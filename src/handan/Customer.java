/**
 * Klass som definierar en kund.
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
  /**
   * Versionshanterings variabel till deserialisering
   */
  private static final long serialVersionUID = 611114L;

  // Privata variabler till kund.
  private String firstName;
  private String lastName;
  private String personalNumber;
  private List<Account> accounts; // Lista med konton

  /**
   * Default konstruktor för en kund.
   */
  protected Customer() {
    this("UnknownA", "UnknownB", "UnknownC");
  }

  /**
   * Skapa en ny kund med f-namn, e-namn och pNo
   *
   * @param firstName         Förnamn
   * @param lastName          Efternamn
   * @param thePersonalNumber Personnummer
   */
  protected Customer(String firstName, String lastName, String personalNumber) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.personalNumber = personalNumber;
    this.accounts = new ArrayList<>();
  }

  /**
   * Ändrar på kunden. Endast tillåtet att ändra på sin egen post.
   *
   * @param newFirstName Nytt förnamn
   * @param newLastName  Nytt efternamn
   * @return true om något värde har ändrats
   */
  protected boolean changeCustomerName(String newFirstName, String newLastName) {
    boolean updated = false;
    // Byter endast om det är någon information att byta till
    if (newFirstName != null && !newFirstName.isBlank()) {
      firstName = newFirstName;
      updated = true;
    }
    if (newLastName != null && !newLastName.isBlank()) {
      lastName = newLastName;
      updated = true;
    }
    return updated;
  }

  /**
   * Rutin som tar bort konton till en kund givet att alla transaktioner redan är
   * bortagna
   */
  protected void deleteAccounts() {
    accounts.clear();
  }

  /**
   * Rutin som ger ut den privata listan men konton, accounts
   *
   * @return pekare till listan
   */
  protected List<Account> getAccounts() {
    return accounts;
  }

  /**
   * Hämtar personnummer
   *
   * @return personalNumber
   */
  protected String getPersonalNumber() {
    return personalNumber;
  }

  @Override
  public String toString() {
    return personalNumber + " " + firstName + " " + lastName;
  }
}