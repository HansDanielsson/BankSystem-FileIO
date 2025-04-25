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
import java.util.Optional;
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

  /**
   * Lokal hjälprutin som visar en dialog för att bekräfta om användaren vill
   * radera banken.
   */
  protected static boolean alertBankErase() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Öppna bankfil");
    alert.setHeaderText("Vill du läsa in en ny bankfil?");
    alert.setContentText("Detta kommer att radera alla nuvarande kunder. Åtgärden kan inte ångras.");
    Optional<ButtonType> result = alert.showAndWait();
    return (result.isPresent() && result.get() == ButtonType.OK);
  }

  /**
   * Hjälprutin för att välja en fil och läsa in banken.
   *
   * @param bank Hjälp-parameter, inte på riktigt
   * @return null eller den nya banken
   */
  protected static BankLogic getFileBank() {
    // Skapa en filväljare
    File file = openFileName("*.dat");
    if (file == null) {
      return null;
    }

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      // Läs in kontonummer
      Account.setLastAssignedNumber(in.readInt());
      // Läs in bank objektet
      return (BankLogic) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Rutin som öppnar en fil och läser in transaktioner till en dialog ruta med
   * scroll.
   *
   * @return true om det inte går att läsa in filen
   */
  protected static boolean getFileTransactions() {
    File file = openFileName("*.txt");
    if (file == null) {
      return false;
    }

    String content;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      content = br.lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (IOException e) {
      e.printStackTrace();
      return true;
    }

    // Skapa en TextArea för visning
    TextArea textArea = new TextArea(content);
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(600);
    textArea.setMaxHeight(400);

    // Visa i alert-dialog
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Transaktioner");
    alert.setHeaderText("Innehåll i filen: " + file.getName());
    alert.getDialogPane().setContent(textArea);
    alert.setResizable(true);
    alert.showAndWait();
    return false;
  }

  /**
   * Lokal hjälprutin för att öppna en filväljare och välja en fil.
   *
   * @return Den valda filen
   */
  private static File openFileName(String filter) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Öppna bankfil");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bankfiler", filter));

    // Sätt startkatalogen
    File initialDir = new File("src/handan/files");
    fileChooser.setInitialDirectory(initialDir.exists() ? initialDir : new File(System.getProperty("user.home")));

    return fileChooser.showOpenDialog(new Stage());
  }

  /**
   * Hjälprutin som sparar bank objektet till en fil
   */
  protected static String putFileBank(BankLogic bank) {
    String strDate = sdf.format(new Date());
    String strFiles = "src/handan/files/bank-" + strDate + ".dat";
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(strFiles))) {
      // Spara kontonummer
      oos.writeInt(Account.getLastAssignedNumber());
      // Spara bank objektet
      oos.writeObject(bank);
      return "Sparad till fil: " + strFiles;
    } catch (IOException e) {
      e.printStackTrace();
      return "Sökväg/Åtkomst nekad: " + strFiles;
    }
  }

  /**
   * Hjälprutin som skriver transaktioner till filen, givet att result är inte
   * null.
   *
   * @param result - Transaktionerna
   */
  protected static boolean putFileTransactions(List<String> result) {
    String strDate = sdf.format(new Date());
    String strFiles = "src/handan/files/bank-" + strDate + ".txt";
    try (FileWriter fileWriter = new FileWriter(strFiles, true)) {

      String dateOnly = strDate.split("-")[0];
      fileWriter.write("Datum: " + dateOnly + System.lineSeparator());
      fileWriter.write("====================================" + System.lineSeparator());

      for (String str : result) {
        fileWriter.write(str + System.lineSeparator());
      }

      fileWriter.write("====================================" + System.lineSeparator());
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}