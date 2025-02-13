package dbcreator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Properties;

/**
 * Classe controller per la creazione del database EmotionalSongs.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class EmotionalSongsDBController {
    /**
     * Progress bar visualizzata durante il caricamento dei dati.
     */
    @FXML
    private ProgressBar loadingProgressBar;
    /**
     * Label utilizzata per fornire feedback durante il caricamento dei dati.
     */
    @FXML
    private Label loadingStatusLabel;
    /**
     * Campo di input username per la connessione al database PostgreSQL.
     */
    @FXML
    private TextField usernameField;
    /**
     * Campo di input password per la connessione al database PostgreSQL.
     */
    @FXML
    private PasswordField passwordField;
    /**
     * Campo di input porta per la connessione al server PostgreSQL.
     */
    @FXML
    private TextField portaField;
    /**
     * Percorso del file CSV contenente i dati delle canzoni da caricare nella tabella Canzoni.
     */
    private final String pathCanzoniCSV = "data/canzoni.csv";
    /**
     * Percorso del file SQL contenente le query per creare le tabelle.
     */
    private final String pathQueryTabelle ="data/creaTabelle.sql";
    /**
     * Percorso del file SQL contenente le query per creare il trigger.
     */
    String pathTrigger = "data/trigger_canzoni.sql";
    /**
     * Nome del database da creare o al quale connettersi.
     */
    private final String nomeDatabase = "emotionalsongs";

    /**
     * Pulsante per la verifica dell'username e la password per la connessione al database PostgreSQL.
     * Connette al server PostgreSQL, crea il database se non esiste
     * e carica i dati dai file SQL e CSV.
     */
    @FXML
    public void creaDBEmotionalSongs(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String porta = portaField.getText();
        //Verifica se i campi sono vuoti
        if (username.isEmpty() || password.isEmpty() || porta.isEmpty()) {
            showInformationAlert("Inserire username, password e porta.");
            return; //Esci dal metodo
        }
        File fileSQL = new File(pathQueryTabelle);
        File fileCSV = new File(pathCanzoniCSV);
        File fileTriggerSQL = new File(pathTrigger);
        //Verifica se i file esistono
        if (!fileSQL.exists() || !fileCSV.exists() || !fileTriggerSQL.exists()){
            showErrorAlert("Il fileSQL o il fileCSV non è stato trovato nel " +
                    "percorso specificato.\n" + pathQueryTabelle +"\n"+pathCanzoniCSV);
            return; //Esci dal metodo
        }
        Thread loadingThread = new Thread(() -> {
            String url = "jdbc:postgresql://localhost:"+ porta +"/";
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connessione al server PostgreSQL stabilita!");
                // Salva le credenziali nel file di configurazione
                url = url + nomeDatabase;
                salvaCredenzialiConfig(url, username, password);
                //Verifica se il database esiste
                if (!checkEsisteDatabase(connection, nomeDatabase)) {
                    creaDatabase(connection,nomeDatabase);
                    connection.close();
                    connection = DriverManager.getConnection(url, username, password);
                    //Esegue il file creaTabelle.sql
                    eseguiScriptSQL(connection, pathQueryTabelle);
                    //Caricamento dei dati dal CSV
                    Platform.runLater(() -> {
                        loadingProgressBar.setVisible(true);
                        loadingStatusLabel.setVisible(true);
                        updateLoadingStatusLabel("Caricamento del file 'canzoni.csv' in corso...");
                    });
                    System.out.println( "Provo a caricare il contenuto dei file sql-csv nel database...");
                    connection = DriverManager.getConnection(url, username, password);
                    inserisciCanzoniDaCSV(connection);
                    //Esegue il file trigger_canzoni.sql
                    eseguiScriptSQL(connection, pathTrigger);
                    Platform.runLater(() -> {
                        loadingProgressBar.setVisible(false);
                        loadingStatusLabel.setAlignment(Pos.CENTER);
                        updateLoadingStatusLabel("Caricamento completato");
                        showSuccessAlert("Contenuto dei file 'creaTabelle.sql', 'canzoni.csv' e 'trigger_canzoni.sql' caricati nel database con successo.");
                    });
                } else  showErrorAlert("Il database '" + nomeDatabase + "' esiste gia'. Non è necessario caricare il contenuto del file .sql.");
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Errore nella lettura del file: " + e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Errore durante la connessione al server postgreSQL: " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        System.err.println("Errore nella chiusura della connessione: " + e.getMessage());
                    }
                }
            }
        });
        loadingThread.start();
    }

    /**
     * Metodo per inserire tutte le canzoni dal file CSV nella tabella Canzoni del database.
     * @param connection La connessione al database.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public void inserisciCanzoniDaCSV(Connection connection) throws SQLException {
        String csvFilePath = Paths.get("data/canzoni.csv").toAbsolutePath().toString();
        String copyQuery = "COPY Canzoni(titolo, autore, anno, album, durata) " +
                "FROM '" + csvFilePath + "' WITH (FORMAT csv, HEADER true, DELIMITER ';', ENCODING 'UTF-8')";
        try (Statement statement = connection.createStatement()) {
            statement.execute(copyQuery);
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        }
    }

    /**
     * Esegue il contenuto di uno script SQL da un file specificato sul database.
     * @param connection La connessione al database 'emotionalsongs'.
     * @param filePath Il percorso del file contenente lo script SQL da eseguire.
     */
    private void eseguiScriptSQL(Connection connection, String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String fileContent = String.join("\n", lines);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(fileContent);
            } catch (SQLException e) {
                showErrorAlert("Errore nell'esecuzione della query: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Errore generico: " + e.getMessage());
            }
        } catch (IOException e) {
            showErrorAlert("Errore nella lettura del file: " + e.getMessage());
        }
    }

    /**
     * Metodo per verificare se il database esiste gia'.
     * @param connection    La connessione al database.
     * @param databaseName  Il nome del database da verificare.
     * @return true se il database esiste, altrimenti false.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public boolean checkEsisteDatabase(Connection connection, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next();
            }
        }
    }

    /**
     * Metodo per creare il database se non esiste.
     * @param connection    La connessione al database.
     * @param databaseName  Il nome del database da creare.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public void creaDatabase(Connection connection, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE DATABASE " + databaseName;
            statement.executeUpdate(query);
            System.out.println("Database '" + databaseName + "' creato con successo.");
        }
    }

    /**
     * Metodo per salvare le credenziali nel file di configurazione.
     * @param url       L'URL del database.
     * @param username  Lo username per la connessione al database.
     * @param password  La password per la connessione al database.
     * @throws IOException Se si verifica un errore durante la scrittura del file di configurazione.
     */
    public void salvaCredenzialiConfig(String url, String username, String password) throws IOException {
        String configFilePath = Paths.get("ConfigServerPostgreSQL/configServerPostgresSQL.properties").toAbsolutePath().toString();
        Properties properties = new Properties();
        properties.setProperty("url",url);
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
            properties.store(fos, "Configurazione Server postgreSQL");
        }
        System.out.println("Credenziali salvate nel file " + configFilePath);
    }

    /**
     * Metodo per mostrare un messaggio di successo.
     * @param messaggio Il messaggio da mostrare.
     */
    private void showSuccessAlert(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * Metodo per mostrare un messaggio di errore.
     * @param errorMessage Il messaggio di errore da mostrare.
     */
    private void showErrorAlert(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            Platform.exit();
        });
    }
    /**
     * Metodo per mostrare un messaggio informativo.
     * @param messaggio Il messaggio informativo da mostrare.
     */
    private void showInformationAlert(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Aggiorna il testo del label di stato in modo thread-safe
     * utilizzando il thread di JavaFX Application.
     * @param message Il testo da visualizzare nel label di stato.
     */
    private void updateLoadingStatusLabel(String message) {
        Platform.runLater(() -> loadingStatusLabel.setText(message));
    }
}