/**
 * Klass som definierar en kund.
 * @author Hans Danielsson, handan-2
 */
package handan;

import java.util.ArrayList;
import java.util.List;

public class Customer {
  // Privata variabler till kund.
  private String customerName;
  private String customerSurname;
  private String personalNumber;
  private List<Account> accounts = null; // Lista med konton

  /**
   * Default konstruktor för en kund.
   */
  protected Customer() {
    this("UnknownA", "UnknownB", "UnknownC");
  }

  /**
   * Skapa en ny kund med f-namn, e-namn och pNo
   *
   * @param theCustomerName
   * @param theCustomerSurname
   * @param thePersonalNumber
   */
  protected Customer(String theCustomerName, String theCustomerSurname, String thePersonalNumber) {
    customerName = theCustomerName;
    customerSurname = theCustomerSurname;
    personalNumber = thePersonalNumber;
    accounts = null;
  }

  /**
   * Ändrar på kunden. Endast tillåtet att ändra på sin egen post.
   *
   * @param theName
   * @param theSureName
   * @return om något värde har ändrats
   */
  protected boolean changeCustomerName(String theName, String theSureName) {
    boolean result = false;
    // Byter endast om det är någon information att byta till
    if (!theName.isEmpty()) {
      customerName = theName;
      result = true;
    }
    if (!theSureName.isEmpty()) {
      customerSurname = theSureName;
      result = true;
    }
    return result;
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

  /**
   * Rutin som sätter den privata pekaren för konton till en ny tom lista
   */
  protected void setAccounts() {
    accounts = new ArrayList<>();
  }

  @Override
  public String toString() {
    return personalNumber + " " + customerName + " " + customerSurname;
  }
}