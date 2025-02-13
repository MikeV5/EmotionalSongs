package DBHelper;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * La classe {@code DBManager} fornisce un'interfaccia per gestire la connessione al database PostgreSQL
 * utilizzando il driver JDBC.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class DBManager {
    /**
     * L'URL di default per la connessione al database.
     */
    private final static String urlDefault = "jdbc:postgresql://localhost/emotionalsongs";
    /**
     * L'oggetto Connection utilizzato per la connessione al database.
     */
    private static Connection connection = null;
    /**
     * L'oggetto Statement utilizzato per eseguire le query SQL sul database.
     */
    private static Statement statement = null;

    /**
     * Costruttore vuoto della classe {@code DBManager}.
     */
    public DBManager() {
    }
    /**
     * Restituisce un oggetto di tipo {@code Connection} rappresentante una connessione al database.
     * @return L'oggetto {@code Connection} rappresentante la connessione al database.
     * @throws SQLException Se si verifica un errore durante la connessione al database.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null) {
                String[] dati= recuperaCredenzialiDaConfig();
                connection = DriverManager.getConnection(dati[0], dati[1], dati[2]);
                System.out.println("Connessione al database PostgreSQL stabilita.");
                return connection;
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la connessione al database PostgreSQL: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del file di configurazione: " + e.getMessage());
        }
        return null;
    }

    /**
     * Recupera le credenziali di connessione al database PostgreSQL dal file di configurazione.
     * @return Un array di stringhe contenente l'URL, l'username e la password per la connessione al database.
     * @throws IOException Se si verifica un errore durante la lettura del file di configurazione.
     */
    private static String[] recuperaCredenzialiDaConfig() throws IOException {
        String configFilePath = "ConfigServerPostgreSQL/configServerPostgresSQL.properties";
        Properties properties = new Properties();
        String[] credenziali = new String[3];
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            credenziali[0] = url;
            credenziali[1] = username;
            credenziali[2] = password;
        } catch (IOException e) {
            throw new IOException("Errore nella lettura del file di configurazione: " + e.getMessage());
        }
        return credenziali;
    }

    /**
     * Chiude la connessione al database, liberando le risorse.
     * @throws SQLException Se si verifica un errore durante la chiusura della connessione.
     */
    public static void closeConnection() throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
    /**
     * Restituisce un oggetto di tipo {@code Statement} utilizzato per eseguire le query sul database.
     * @return L'oggetto {@code Statement} utilizzato per eseguire le query sul database.
     * @throws SQLException Se si verifica un errore durante la creazione dello statement.
     */
    public static Statement getStatement() throws SQLException {
        if (statement == null) {
            Connection connection = getConnection();
            statement = connection.createStatement();
        }
        return statement;
    }

    /**
     * Chiude lo statement, liberando le risorse.
     * @throws SQLException Se si verifica un errore durante la chiusura dello statement.
     */
    public static void closeStatement() throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
    }
}
