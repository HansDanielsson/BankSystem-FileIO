/**
 * Huvudmodul som definierar ett GUI.
 * @author Hans Danielsson, handan-2
 */
package handan;

//Importsatser
import java.util.ArrayList;
import java.util.List;

//Importsatser för JavaFX med olika API rutiner
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

  private static final String SAVED = "Sparad - Ok";
  private static final String NOTSAVED = "Ej sparad";

  public static void main(String[] args) {
    launch(args);
  }

  // bank-hanteraren
  private BankLogic bank = new BankLogic();

  // Skapar upp en BorderPane
  private BorderPane borderPane = new BorderPane();
  // Menyer till BorderPane
  private Menu menuFile = new Menu("File");
  private String[] strMenuFile = { "Läs in banken", "Spara banken", "Spara transaktioner", "Avsluta" };
  private MenuItem[] menuItemFile = new MenuItem[strMenuFile.length];

  private Menu menuCustomer = new Menu("Kund");
  private String[] strMenuCustomer = { "Spara", "Hämta", "Byt namn", "Ta bort", "Lista" };
  private MenuItem[] menuItemCustomer = new MenuItem[strMenuCustomer.length];

  private Menu menuAccount = new Menu("Konton");
  private String[] strMenuAccount = { "Spar", "Kredit", "Saldo", "Sätta in", "Ta ut", "Transaktioner", "Ta bort" };
  private MenuItem[] menuItemAccount = new MenuItem[strMenuAccount.length];

  // Meny-hanterare, innehåller alla menyer
  private MenuBar menuBar = new MenuBar(menuFile, menuCustomer, menuAccount);

  // Spara knappen till olika syften
  private String[] strButton = { "Spara", "Hämta", "Byt namn", "Ta bort", "Spar", "Kredit", "Saldo", "Sätt in", "Ta ut",
      "Transaktioner", "Ta bort", "Läs in bank", "Spara bank", "Spara transaktioner" };

  // Deklaration av text/inmatning, som används på olika ställen

  private Label[] labelPNo = new Label[strButton.length];
  private TextField[] tfPNo = new TextField[strButton.length];

  private Label[] labelName = new Label[strButton.length];
  private TextField[] tfName = new TextField[strButton.length];

  private Label[] labelSurname = new Label[strButton.length];
  private TextField[] tfSurname = new TextField[strButton.length];

  /**
   * Man behöver bara använda en tfKontoList som används för alla listor på alla
   * "sidor"
   */
  private ObservableList<String> tfKontoList = FXCollections.observableArrayList();
  private ObservableList<String> tfResultList = FXCollections.observableArrayList();

  private Label[] labelKontoNr = new Label[strButton.length];

  // Ignorerar varningen, det blir rätt kod ändå.
  @SuppressWarnings("unchecked")
  private ListView<String>[] tfKontoNr = new ListView[strButton.length];

  private Label[] labelBelopp = new Label[strButton.length];
  private TextField[] tfBelopp = new TextField[strButton.length];

  private Button[] saveButton = new Button[strButton.length];

  private VBox[] vbox = new VBox[strButton.length];

  // Bilder att visa i högra delen
  private Image imageBag = new Image("file:src/handan/files/bag-96x96.png");
  private Image imagePiggy = new Image("file:src/handan/files/piggy-bank-96x96.png");
  private Image imageSafe = new Image("file:src/handan/files/safe-96x96.png");

  // Skapa 3 ImageView för att visa bilderna
  private ImageView bagImageView = new ImageView(imageBag);
  private ImageView piggyImageView = new ImageView(imagePiggy);
  private ImageView safeImageView = new ImageView(imageSafe);
  private VBox imageVBox = new VBox(20);

  private ListView<String> centralResult = new ListView<>(tfResultList);
  private Label statusText = new Label();

  /**
   * Rutin som fixar alla konton till ett pNr, Givet att pNr är ifyllt
   *
   * @param index - Menyvalet
   */
  private void bankAccountList(short index) {
    switch (index) {
    case 6, 7, 8, 9, 10:
      List<String> result = bank.getAccountList(tfPNo[index].getText());
      tfKontoList.clear();
      if (result != null) {
        tfKontoList.addAll(result);
      }
      break;
    default:
      break;
    }
  }

  /**
   * Hjälprutin som kollar att belopp är utan decimaler. Systemet är byggt på
   * modellen integer.
   *
   * @param index - Beloppet som ska kollas upp
   */
  private void bankBeloppCheck(short index) {
    String str = tfBelopp[index].getText();
    int pos = str.indexOf("."); // Ta bort allt efter .
    if (pos > -1) {
      str = str.substring(0, pos);
    }
    pos = str.indexOf(","); // Ta bort allt efter ,
    if (pos > -1) {
      str = str.substring(0, pos);
    }
    tfBelopp[index].setText(str);
  }

  /**
   * Rutin som utför menyval under Konto
   *
   * @param index - Menyvalet
   */
  private void bankMenuAccount(short index) {
    switch (index) {
    case 0, 1, 2, 3, 4, 5, 6: // Konto kommando
      borderPane.setLeft(vbox[index + 4]);
      break;
    default:
      setStatusError("Konton, index = " + index);
      break;
    }
  }

  /**
   * Rutin som utför kommandon under Kund
   *
   * @param index - Menyvalet
   */
  private void bankMenuCustomer(short index) {
    switch (index) {
    case 0, 1, 2, 3: // Kund kommando
      borderPane.setLeft(vbox[index]);
      break;
    case 4: // Lista alla kunder
      getBankAllCustomers();
      break;
    default:
      setStatusError("Kund, index = " + index);
      break;
    }
  }

  /**
   * Rutin som utför kommando under File
   *
   * @param index - Menyvalet
   */
  private void bankMenuFile(short index) {
    switch (index) {
    case 0:
      setStatusError("Läs in banken");
      break;
    case 1:
      setStatusError("Spara banken");
      break;
    case 2:
      setStatusError("Spara transaktioner");
      break;
    case 3:
      System.exit(0);
      break;
    default:
      setStatusError("File, index = " + index);
      break;
    }
  }

  /**
   * Rutin som anropas när användaren har tryckt på en Button-knapp
   *
   * @param index - Knappens syfte
   */
  private void bankMenuSave(short index) {
    switch (index) {
    case 0: // Spara ny kund
      createBankCustomer();
      break;
    case 1: // Hämta EN kund
      getBankCustomer();
      break;
    case 2: // Byt namn på kund
      changeBankCustomerName();
      break;
    case 3: // Ta bort kund
      deletBankCustomer();
      break;
    case 4: // Skapa Sparkonto
      createBankSavingAccount();
      break;
    case 5: // Skapa Kreditkonto
      createBankCreditAccount();
      break;
    case 6: // Saldo
      getBankAccount();
      break;
    case 7: // Sätta in
      depositBankAccount();
      break;
    case 8: // Ta ut
      withdrawBankAccount();
      break;
    case 9: // Transaktioner
      getBankTransactions();
      break;
    case 10: // Ta bort konto
      closeBankAccount();
      break;
    default:
      setStatusError("Save button, index = " + index);
      break;
    }
  }

  /**
   * Rutin som byte namn på en kund(pNr)
   */
  private void changeBankCustomerName() {
    if (bank.changeCustomerName(tfName[2].getText(), tfSurname[2].getText(), tfPNo[2].getText())) {
      setStatusOk(SAVED);
    } else {
      setStatusError(NOTSAVED);
    }
  }

  /**
   * Rutin som tar bort ett konto(accountId) på kund(pNr)
   */
  private void closeBankAccount() {
    String strKonto = tfKontoNr[10].getSelectionModel().getSelectedItem();
    try {
      if (!strKonto.isBlank()) {
        String str = bank.closeAccount(tfPNo[10].getText(), Integer.parseInt(strKonto));
        if (str != null) {
          List<String> result = new ArrayList<>();
          result.add(str);
          putCenterText(result);
        }
      }
    } catch (Exception e) {
      setStatusError("Felaktigt kontonummer: " + strKonto);
    }

  }

  /**
   * Rutin som skapar ett kreditkonto för person(pNo)
   */
  private void createBankCreditAccount() {
    int accountId = bank.createCreditAccount(tfPNo[5].getText());
    if (accountId > 0) {
      List<String> result = new ArrayList<>();
      result.add("Kontonummer: " + accountId);
      putCenterText(result);
    } else {
      setStatusError(NOTSAVED);
    }
  }

  /**
   * Rutin som skapar en kund med f-namn, e-namn och pNr
   */
  private void createBankCustomer() {
    if (bank.createCustomer(tfName[0].getText(), tfSurname[0].getText(), tfPNo[0].getText())) {
      setStatusOk(SAVED);
    } else {
      setStatusError(NOTSAVED);
    }
  }

  /**
   * Rutin som skapar ett sparkonto för kund pNr
   */
  private void createBankSavingAccount() {
    int accountId = bank.createSavingsAccount(tfPNo[4].getText());
    if (accountId > 0) {
      List<String> result = new ArrayList<>();
      result.add("Kontonummer: " + accountId);
      putCenterText(result);
    } else {
      setStatusError(NOTSAVED);
    }
  }

  /**
   * Rutin som tar bort en kund(pNo)
   */
  private void deletBankCustomer() {
    List<String> result = bank.deleteCustomer(tfPNo[3].getText());
    if (result != null) {
      putCenterText(result);
    }
  }

  /**
   * Rutin som sätter in pengar på ett konto
   */
  private void depositBankAccount() {
    String strKonto = tfKontoNr[7].getSelectionModel().getSelectedItem();
    String strBelopp = tfBelopp[7].getText();
    try {
      if (bank.deposit(tfPNo[7].getText(), Integer.parseInt(strKonto), Integer.parseInt(strBelopp))) {
        setStatusOk(SAVED);
      } else {
        setStatusError(NOTSAVED);
      }
    } catch (Exception e) {
      setStatusError("Felaktiga värden: " + strKonto + "/" + strBelopp);
    }
  }

  /**
   * Rutin som hämtar saldo för ett konto
   */
  private void getBankAccount() {
    String strKonto = tfKontoNr[6].getSelectionModel().getSelectedItem();
    try {
      if (!strKonto.isBlank()) {
        String str = bank.getAccount(tfPNo[6].getText(), Integer.parseInt(strKonto));
        if (str != null) {
          List<String> result = new ArrayList<>();
          result.add(str);
          putCenterText(result);
        }
      }
    } catch (Exception e) {
      setStatusError("Felaktigt Kontonummer: " + strKonto);
    }
  }

  /**
   * Rutin som hämtar alla kunder och visar det i fönster Center
   */
  private void getBankAllCustomers() {
    List<String> result = bank.getAllCustomers();
    if (result != null) {
      putCenterText(result);
    }
  }

  /**
   * Rutin som tar fram en kund med konton
   */
  private void getBankCustomer() {
    List<String> result = bank.getCustomer(tfPNo[1].getText());
    if (result != null) {
      putCenterText(result);
    }
  }

  /**
   * Rutin som hämtar alla transaktioner för ett konto
   */
  private void getBankTransactions() {
    String strKonto = tfKontoNr[9].getSelectionModel().getSelectedItem();
    try {
      if (!strKonto.isBlank()) {
        List<String> result = bank.getTransactions(tfPNo[9].getText(), Integer.parseInt(strKonto));
        if (result != null) {
          putCenterText(result);
        }
      }
    } catch (Exception e) {
      setStatusError("Felaktigt kontonummer: " + strKonto);
    }
  }

  /**
   * Hjälprutin som sätter texten i centrum rutan. Kontrollen är utfört redan
   * innan anropet att strResult pekar på något.
   *
   * @param strResult
   */
  private void putCenterText(List<String> strResult) {
    tfResultList.clear();
    tfResultList.addAll(strResult);
  }

  /**
   * Rutin som visar i status texten ett felmeddelande
   *
   * @param str - Meddelandet
   */
  private void setStatusError(String str) {
    statusText.setText(str);
    statusText.setTextFill(Color.RED);
  }

  /**
   * Rutin som visar i status texten ett ok-meddelande
   *
   * @param str - Meddelandet
   */
  private void setStatusOk(String str) {
    statusText.setText(str);
    statusText.setTextFill(Color.GREEN);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    short i;
    try {
      // Sätter en titel på fönstret till "BankSystem"
      primaryStage.setTitle("BankSystem");

      // Fixar alla menyer på plats
      // Initierar olika variabler.
      for (i = 0; i < strMenuFile.length; i++) {
        menuItemFile[i] = new MenuItem(strMenuFile[i]);
        final short indexFile = i;
        menuItemFile[i].setOnAction(_ -> bankMenuFile(indexFile));
        if (i == strMenuFile.length - 1) {
          menuFile.getItems().add(new SeparatorMenuItem());
        }
        menuFile.getItems().add(menuItemFile[i]);
      }

      for (i = 0; i < strMenuCustomer.length; i++) {
        menuItemCustomer[i] = new MenuItem(strMenuCustomer[i]);
        final short indexCustomer = i;
        menuItemCustomer[i].setOnAction(_ -> bankMenuCustomer(indexCustomer));
        menuCustomer.getItems().add(menuItemCustomer[i]);
      }

      for (i = 0; i < strMenuAccount.length; i++) {
        menuItemAccount[i] = new MenuItem(strMenuAccount[i]);
        final short indexAccount = i;
        menuItemAccount[i].setOnAction(_ -> bankMenuAccount(indexAccount));
        menuAccount.getItems().add(menuItemAccount[i]);
      }

      // Initierar alla "sidor" med olika fält
      for (i = 0; i < strButton.length; i++) {

        labelPNo[i] = new Label("Personnummer: ");
        tfPNo[i] = new TextField();

        labelName[i] = new Label("Förnamn: ");
        tfName[i] = new TextField();

        labelSurname[i] = new Label("Efternamn: ");
        tfSurname[i] = new TextField();

        labelKontoNr[i] = new Label("Kontonr: ");
        tfKontoNr[i] = new ListView<>(tfKontoList);

        labelBelopp[i] = new Label("Belopp: ");
        tfBelopp[i] = new TextField();

        saveButton[i] = new Button(strButton[i]);

        // Aktivera händelser efter inmatning på valda fält
        final short index = i;
        saveButton[i].setOnAction(_ -> bankMenuSave(index));
        tfPNo[i].focusedProperty().addListener(_ -> bankAccountList(index));
        tfBelopp[i].focusedProperty().addListener(_ -> bankBeloppCheck(index));
        vbox[i] = new VBox(10);
        vbox[i].getChildren().addAll(labelPNo[i], tfPNo[i]); // pNo finns på alla vbox, även button men den ska vara
                                                             // sist.
      }

      // Initiering av vbox som innehåller olika element, har redan lite initierat

      // Meny Kund kommandon
      // Spara ny kund
      vbox[0].getChildren().addAll(labelName[0], tfName[0], labelSurname[0], tfSurname[0]);

      // Byt namn på en kund
      vbox[2].getChildren().addAll(labelName[2], tfName[2], labelSurname[2], tfSurname[2]);

      // Saldo
      vbox[6].getChildren().addAll(labelKontoNr[6], tfKontoNr[6]);

      // Sätta in
      vbox[7].getChildren().addAll(labelKontoNr[7], tfKontoNr[7], labelBelopp[7], tfBelopp[7]);

      // Ta ut
      vbox[8].getChildren().addAll(labelKontoNr[8], tfKontoNr[8], labelBelopp[8], tfBelopp[8]);

      // Transaktioner
      vbox[9].getChildren().addAll(labelKontoNr[9], tfKontoNr[9]);

      // Ta bort konto
      vbox[10].getChildren().addAll(labelKontoNr[10], tfKontoNr[10]);

      // Sätt saveButtons sist
      for (i = 0; i < strButton.length; i++) {
        vbox[i].getChildren().add(saveButton[i]);
      }

      imageVBox.getChildren().addAll(bagImageView, piggyImageView, safeImageView);

      borderPane.setTop(menuBar);
      borderPane.setCenter(centralResult);
      borderPane.setBottom(statusText);
      borderPane.setRight(imageVBox);

      // Skapar upp en scene med borderPane, bredd, höjd samt bakgrundsfärg som
      // argument
      Scene scene = new Scene(borderPane, 1400, 600, Color.LIGHTGREY);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

      // Lägger in Scene i Stage
      primaryStage.setScene(scene);

      // Visa fönstret
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Rutin som tar ut pengar från ett konto
   */
  private void withdrawBankAccount() {
    try {
      int accountId = Integer.parseInt(tfKontoNr[8].getSelectionModel().getSelectedItem());
      int amount = Integer.parseInt(tfBelopp[8].getText());
      if (bank.withdraw(tfPNo[8].getText(), accountId, amount)) {
        setStatusOk(SAVED);
      } else {
        setStatusError(NOTSAVED);
      }
    } catch (Exception e) {
      setStatusError(
          "Felaktiga värden: " + tfKontoNr[8].getSelectionModel().getSelectedItem() + "/" + tfBelopp[8].getText());
    }

  }
}