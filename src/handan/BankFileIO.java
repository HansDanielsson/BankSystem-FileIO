/**
 * Klass som definierar rutiner för BankIO.
 * @author Hans Danielsson, handan-2
 */
package handan;

/**
 * Importsatser
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Importsatser för JavaFX med olika API rutiner
 */
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BankFileIO {

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd-HHmmss");
  private static final String BASE_PATH = "src/handan/files/";

  /**
   * Lokal hjälprutin som visar en dialog för att bekräfta om användaren vill
   * radera banken.
   */
  protected static boolean alertBankErase() {
    var alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Öppna bankfil");
    alert.setHeaderText("Vill du läsa in en ny bankfil?");
    alert.setContentText("Detta kommer att radera alla nuvarande kunder. Åtgärden kan inte ångras.");
    return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
  }

  /**
   * Hjälprutin som skapar ett unikt filnamn med datum och tid.
   *
   * @param prefix    Prefix till filnamnet
   * @param extension Filändelse
   * @return Filnamnet
   */
  private static String createUniqueFileName(String prefix, String extension) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ignored) {
      // Ignorera avbrott
    }
    return BASE_PATH + prefix + sdf.format(new Date()) + extension;
  }

  /**
   * Hjälprutin för att välja en fil och läsa in banken.
   *
   * @return den nya banken eller null
   */
  protected static BankLogic getFileBank() {
    // Skapa en filväljare
    var file = openFile("*.dat");
    if (file == null) {
      return null;
    }

    try (var in = new ObjectInputStream(new FileInputStream(file))) {
      // Läs in kontonummer
      Account.setLastAssignedNumber(in.readInt());
      // Läs in bank objektet
      return (BankLogic) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Rutin som öppnar en fil och läser in transaktioner till en dialog ruta med
   * scroll.
   *
   * @return true om det inte går att läsa in filen
   */
  protected static boolean getFileTransactions() {
    var file = openFile("*.txt");
    if (file == null) {
      return false;
    }

    try (var br = new BufferedReader(new FileReader(file))) {
      var content = br.lines().collect(Collectors.joining(System.lineSeparator()));
      showTextInDialog("Transaktioner", "Innehåll i filen: " + file.getName(), content);
      return false;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Lokal hjälprutin för att öppna en filväljare och välja en fil.
   *
   * @return Den valda filen
   */
  private static File openFile(String filter) {
    var fileChooser = new FileChooser();
    fileChooser.setTitle("Öppna bankfil");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bankfiler", filter));

    // Sätt startkatalogen
    var initialDir = new File(BASE_PATH);
    fileChooser.setInitialDirectory(initialDir.exists() ? initialDir : new File(System.getProperty("user.home")));

    return fileChooser.showOpenDialog(new Stage());
  }

  /**
   * Hjälprutin som skriver bank objektet till en fil.
   *
   * @param bank - Bank objektet
   * @return Filnamnet
   */
  protected static String putFileBank(BankLogic bank) {
    var path = createUniqueFileName("bank-", ".dat");

    try (var oos = new ObjectOutputStream(new FileOutputStream(path))) {
      // Spara kontonummer
      oos.writeInt(Account.getLastAssignedNumber());
      // Spara bank objektet
      oos.writeObject(bank);
      return "Sparad till fil: " + path;
    } catch (IOException e) {
      e.printStackTrace();
      return "Sökväg/Åtkomst nekad: " + path;
    }
  }

  /**
   *
   * Hjälprutin som skriver transaktioner till filen, givet att result är inte
   * null.
   *
   * @param transactions
   * @return Filnamnet
   */
  protected static String putFileTransactions(List<String> transactions) {
    var path = createUniqueFileName("bank-", ".txt");

    try (var fileWriter = new FileWriter(path, true)) {
      fileWriter.write("Datum: " + sdf.format(new Date()).split("-")[0] + System.lineSeparator());
      fileWriter.write("====================================" + System.lineSeparator());

      for (var str : transactions) {
        fileWriter.write(str + System.lineSeparator());
      }

      fileWriter.write("====================================" + System.lineSeparator());
      return "Sparad till fil: " + path;
    } catch (IOException e) {
      e.printStackTrace();
      return "Sökväg/Åtkomst nekad: " + path;
    }
  }

  /**
   * Hjälprutin som visar en modal dialogruta med textinnehåll.
   *
   * @param title   Titel på dialogrutan
   * @param header  Rubrik på dialogrutan
   * @param content Innehåll i dialogrutan
   */
  private static void showTextInDialog(String title, String header, String content) {

    // Skapa en TextArea för visning
    var textArea = new TextArea(content);
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(600);
    textArea.setMaxHeight(400);

    // Visa i alert-dialog
    var alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.getDialogPane().setContent(textArea);
    alert.setResizable(true);
    alert.showAndWait();
  }

  protected BankFileIO() {
    // Privat konstruktor för att förhindra instansiering
  }
}