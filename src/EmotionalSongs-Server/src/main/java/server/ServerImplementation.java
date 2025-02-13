package server;
import DBHelper.DBManager;
import eccezioni.UtenteInesistenteException;
import util.*;
import interfacce.EmotSongsInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;
import static util.Canzone.convertiAFormatMinuti;
import static util.CaratteriASCII.convertAscii;

/**
 * La classe {@code ServerImplementation} fornisce un'implementazione dell'interfaccia {@code EmotSongsInterface}
 * e rappresenta l'oggetto remoto che sara' accessibile dai client tramite RMI.
 * Gestisce l'accesso al database PostgreSQL e fornisce i metodi per interagire
 * con i dati delle canzoni, utenti, playlist ed emozioni.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ServerImplementation  extends UnicastRemoteObject implements EmotSongsInterface{
    /**
     * L'oggetto Connection utilizzato per la connessione al database.
     */
    private Connection connection;

    /**
     * Una mappa che tiene traccia delle sessioni utente mediante l'associazione di userid a
     * liste di identificatori univoci (UUID). Questo permette di gestire le sessioni utente e i relativi
     * accessi da diversi dispositivi al server tramite RMI.
     */
    private Map<String, List<UUID>> userSessionMap = new HashMap<>();

    /**
     * Crea un'istanza dell'implementazione del server RMI e stabilisce la connessione al database
     * tramite {@code DBManager}.
     * @throws RemoteException Se si verifica un errore durante la creazione dell'oggetto remoto.
     * @throws SQLException Se si verifica un errore durante la connessione al database.
     */
    public ServerImplementation() throws RemoteException, SQLException {
        super();
        establishDatabaseConnection();
    }

    /**
     * Stabilisce la connessione al database PostgreSQL utilizzando la classe {@code DBManager}.
     * Se si verifica un errore durante la connessione, viene stampato un messaggio di errore sulla console.
     * @throws SQLException Se si verifica un errore durante la connessione al database.
     */
    private void establishDatabaseConnection() throws SQLException {
        try {
            connection = DBManager.getConnection();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage()+"\nVerificare username e password " +
                    "nel file di configurazione configServerPostgresSQL.properties");
        }
    }

    /**
     * Effettua il login dell'utente verificando le credenziali fornite.
     * Esegue una query per verificare la correttezza delle credenziali.
     * @param username L'username dell'utente.
     * @param password La password dell'utente.
     * @return L'userID dell'utente se le credenziali sono valide, altrimenti null.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     */
    @Override
    public synchronized String login(String username, String password) throws RemoteException, SQLException {
        String query = "SELECT userid, password FROM utentiregistrati WHERE userid = ?";
        String userID = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPasswordFromDB = resultSet.getString("password");
                    if (PasswordHashManager.verificaPassword(password, hashedPasswordFromDB)) {
                        userID = resultSet.getString("userid");
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di login: " + e.getMessage());
        }
        return userID;
    }

    /**
     * Gestisce il login di un utente e la creazione di una nuova sessione utente.
     * Se l'utente non e' loggato, viene creata la prima sessione per l'utente e viene aggiunta alla mappa delle sessioni utente,
     * altrimenti, ne crea una nuova e la restituisce.
     * @param username L'username dell'utente per cui creare o aggiungere una sessione.
     * @return L'ID della sessione utente creata o aggiunta.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     */
    @Override
    public synchronized UUID loginSession(String username) throws RemoteException {
        List<UUID> userSessions = userSessionMap.get(username);
        if (userSessions != null && !userSessions.isEmpty()) {
            // L'utente è già loggato, aggiungi la nuova sessione alla lista esistente
            UUID sessionId = UUID.randomUUID();
            userSessions.add(sessionId);
            System.out.println(username + " gia' loggato - Nuova SessioneID: " + sessionId);
            stampaSessioniUtente(username);
            return sessionId;
        } else {
            // Crea la prima sessione per l'utente
            UUID sessionId = UUID.randomUUID();
            List<UUID> newSessionList = new ArrayList<>();
            newSessionList.add(sessionId);
            userSessionMap.put(username, newSessionList);
            System.out.println(username + " : SessioneID creata: " + sessionId);
            return sessionId;
        }
    }

    /**
     * Stampa le sessioni attive per un determinato utente.
     * Utile per il monitoraggio e il debugging del server RMI.
     * @param username L'userid dell'utente per cui stampare le sessioni attive.
     */
    private synchronized void stampaSessioniUtente(String username) {
        List<UUID> userSessions = userSessionMap.get(username);
        if (userSessions != null) {
            System.out.println("Sessioni attive per l'utente " + username + ":");
            for (UUID session : userSessions) {
                System.out.println(session);
            }
        }
    }

    /**
     * Chiude una sessione per un determinato utente.
     * Rimuove l'ID della sessione dalla lista delle sessioni attive per l'utente specificato.
     * @param username L'userid dell'utente.
     * @param sessionId L'ID della sessione utente da chiudere.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     */
    @Override
    public synchronized void chiudiSessione(String username, UUID sessionId) throws RemoteException {
        List<UUID> userSessions = userSessionMap.get(username);
        if (userSessions != null) {
            userSessions.remove(sessionId);
            System.out.println(username + " - SessionID: " + sessionId + " rimossa.");
        }
    }


    /**
     * Verifica la disponibilita' di un determinato userID.
     * Esegue una query per contare il numero di occorrenze del userID specificato.
     * @param user L'userID da verificare.
     * @return True se l'userID e' disponibile (non presente nel database), altrimenti false.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    @Override
    public synchronized boolean isUserIDAvailable(String user) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        try {
            String query = "SELECT COUNT(*) FROM utentiregistrati WHERE userid = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, user);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return (count == 0); //true se l'userID non è presente, altrimenti false.
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query per verificare la disponibilita' dell'userID: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return false;
    }

    /**
     * Recupera le informazioni di un utente dal database tramite il suo userID.
     * @param userid L'userID dell'utente.
     * @return Un oggetto di tipo {@code Utente} contenente le sue informazioni.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws UtenteInesistenteException Se l'utente non e' presente nel database.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized Utente fetchUtenteByUser(String userid) throws RemoteException, UtenteInesistenteException, SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM utentiregistrati WHERE userid = '" + userid + "'");
            if (rs.next()) {
                return new Utente(rs.getString("userid"),
                        rs.getString("nome"),
                        rs.getString("cognome"));
            } else {
                throw new UtenteInesistenteException("Utente non trovato: " + userid);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'accesso al database." + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Inserisce un nuovo utente nel database.
     * @param u Nuovo utente con le sue informazioni da inserire nel database.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized void insertUtente(Utente u) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        try {
            //Ottiene l'hash della password
            String hashedPassword = PasswordHashManager.hashPassword(u.getPassword());
            String query = "INSERT INTO utentiregistrati (userid, password, nome, cognome, codicefiscale, indirizzo, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, u.getUserid());
            statement.setString(2, hashedPassword); //Salva l'hash della password
            statement.setString(3, u.getNome());
            statement.setString(4, u.getCognome());
            statement.setString(5, u.getCodFisc());
            statement.setString(6, u.getIndirizzo());
            statement.setString(7, u.getEmail());
            int rowsInserted = statement.executeUpdate();
            System.out.println("Utente creato: " + u.getUserid());
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'inserimento di un utente: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Recupera l'elenco delle canzoni dal database e restituisce una mappa che associa l'ID della canzone
     * all'oggetto {@code Canzone} corrispondente.
     * @return Una mappa contenente l'elenco delle canzoni con il rispettivo ID come chiave.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized HashMap<Integer,Canzone> fetchAllSongs() throws RemoteException, SQLException {
        HashMap<Integer, Canzone> canzoniMap = new HashMap<>();;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Canzoni ORDER BY idCanzone ASC");
            while (rs.next()) {
                int idCanzone = rs.getInt("idCanzone");
                String nomeCanzone = convertAscii(rs.getString("titolo"));
                String nomeAutore = convertAscii(rs.getString("autore"));
                int dataCanzone =  rs.getInt("anno");
                String durataCanzone = convertiAFormatMinuti(rs.getString("durata"));
                Canzone canzone = new Canzone(idCanzone,nomeCanzone, nomeAutore, dataCanzone, durataCanzone);
                canzoniMap.put(idCanzone,canzone);
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di recupero delle canzoni: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return canzoniMap;
    }

    /**
     * Restituisce il timestamp dell'ultima modifica registrata nella tabella "Log_Canzoni".
     * @return Il timestamp dell'ultima modifica come valore long in millisecondi.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public long getUltimoTimeStamp() throws RemoteException, SQLException {
        PreparedStatement statement = null;
        long timeStamp = 0;
        try {
            String query ="SELECT MAX(timestampModifica) FROM Log_Canzoni";
            statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                if (timestamp != null) {
                    return timestamp.getTime();
                } else { //Nessuna tupla nella tabella
                    timeStamp = Long.MIN_VALUE;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di recupero dell'ultimo time stamp: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return timeStamp;
    }

    /**
     * Recupera l'elenco delle playlist associate a un utente specifico.
     * @param user L'userID dell'utente di cui recuperare le playlist.
     * @return Un ArrayList di Playlist contenente le playlist dell'utente.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized ArrayList<Playlist> fetchPlaylistByUser(String user) throws RemoteException, SQLException {
        ArrayList<Playlist> arrayP = new ArrayList<>();
        PreparedStatement statement = null;
        try{
            String query ="SELECT * FROM playlist WHERE userid = ? ";
            statement = connection.prepareStatement(query);
            statement.setString(1, user);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayP.add( new Playlist(rs.getInt("idPlaylist"),
                        rs.getString("nome_playlist")));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di recupero delle playlist: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return arrayP;
    }

    /**
     * Recupera le canzoni associate a una playlist tramite il suo ID.
     * @param idPlaylist L'ID della playlist per cui recuperare le canzoni.
     * @return Un Arraylist di Canzone contenente le canzoni associate alla playlist.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized ArrayList<Canzone> fetchCanzoniByidPlaylist(int idPlaylist) throws RemoteException, SQLException {
        ArrayList<Canzone> canzoni = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT c.* FROM Canzoni c " +
                    "JOIN Playlist_Canzoni pc ON c.idCanzone = pc.idCanzone WHERE pc.idPlaylist ="+ idPlaylist);
            while (rs.next()) {
                canzoni.add( new Canzone(rs.getInt("idcanzone"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getInt("anno"),
                        convertiAFormatMinuti(rs.getString("durata"))));
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di recupero delle canzoni: " + e.getMessage());
        }
        return canzoni;
    }

    /**
     * Inserisce l'elenco di canzoni nella playlist specificata per l'utente passato come argomento.
     * @param user L'ID dell'utente che sta inserendo la canzone.
     * @param idPlaylist L'ID della playlist in cui inserire la canzone.
     * @param canzoni L'elenco delle canzoni da inserire nella playlist.
     * @return True se gli inserimenti sono riusciti, false altrimenti.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized boolean insertCanzoni(String user, int idPlaylist, ArrayList<Canzone> canzoni) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        boolean inserimentoRiuscito = true;
        try {
            String query = "INSERT INTO playlist_canzoni (idplaylist,idcanzone) VALUES (?, ?)";
            statement = connection.prepareStatement(query);

            for (Canzone canzone : canzoni) {
                statement.setInt(1, idPlaylist);
                statement.setInt(2, canzone.getIdCanzone());
                statement.addBatch();
            }

            int[] rowsInserted = statement.executeBatch(); //Batch per l'inserimento multiplo
            System.out.println(user + ": " + rowsInserted.length + " canzoni inserite.");
            for (int rows : rowsInserted) {
                if (rows <= 0) {
                    inserimentoRiuscito = false; //False se almeno una canzone non è stata inserita correttamente
                    break;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'inserimento delle canzoni: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return inserimentoRiuscito;
    }

    /**
     * Inserisce una nuova playlist nel database associandola all'utente specificato.
     * @param user L'userID dell'utente a cui associare la nuova playlist.
     * @param nomePlaylist Il nome della playlist da inserire.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized void insertPlaylist(String user, String nomePlaylist) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        try {
            String query = "INSERT INTO playlist (nome_playlist, userid) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, nomePlaylist);
            statement.setString(2, user);
            int rowsInserted = statement.executeUpdate();
            System.out.println(user +": "+rowsInserted + " playlist inserita.");
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'inserimento della playlist: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
    }

    /**
     * Inserisce le emozioni associate a una canzone per l'utente passato come argomento.
     * @param user L'ID dell'utente che sta inserendo le emozioni.
     * @param canzone L'oggetto {@code Canzone} per cui vengono inserite le emozioni.
     * @param datiEmotion Un'Arraylist di {@code Emozione} contenente le emozioni da inserire.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized void insertEmozioni(String user, Canzone canzone, ArrayList<Emozione> datiEmotion) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        try {
            //Disabilita il commit automatico
            //Le operazioni di modifica del database vengono mantenute in sospeso finché non si chiama "commit"
            connection.setAutoCommit(false);
            for (Emozione emozione : datiEmotion) {
                String query = "SELECT * FROM emozioni WHERE userid = ? AND idcanzone = ? AND nomeemozione = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, user);
                statement.setInt(2, canzone.getIdCanzone());
                statement.setString(3, emozione.getEmotionalCategory());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    // Effettua gli aggiornamenti solo se necessario
                    int existingScore = result.getInt("score");
                    String existingNotes = result.getString("notetestuali");
                    int newScore = emozione.getPunteggio();
                    String newNotes = emozione.getNotes();
                    if (existingScore != newScore || (existingNotes == null || existingNotes.isEmpty()) || !existingNotes.equals(newNotes)) {
                        PreparedStatement updateStatement =
                                connection.prepareStatement("UPDATE emozioni SET score = ?, notetestuali = ? WHERE userid = ? AND idcanzone = ? AND nomeemozione = ?");
                        updateStatement.setInt(1, newScore);
                        if (newNotes == null || newNotes.isEmpty()) {
                            updateStatement.setNull(2, Types.VARCHAR);
                        } else {
                            updateStatement.setString(2, newNotes);
                        }
                        updateStatement.setString(3, user);
                        updateStatement.setInt(4, canzone.getIdCanzone());
                        updateStatement.setString(5, emozione.getEmotionalCategory());
                        int rowsUpdated = updateStatement.executeUpdate();
                        System.out.println(user +": "+ + rowsUpdated + " emozione aggiornata.");
                    } else {
                        System.out.println("Il record esiste gia' con lo stesso score, non e' necessario aggiornare.");
                    }
                } else {
                    // Inserisci il nuovo record
                    PreparedStatement updateStatement =
                            connection.prepareStatement("INSERT INTO emozioni (userid, idcanzone, nomeemozione, score, notetestuali) VALUES (?, ?, ?, ?, ?)");
                    updateStatement.setString(1, user);
                    updateStatement.setInt(2, canzone.getIdCanzone());
                    updateStatement.setString(3, emozione.getEmotionalCategory());
                    updateStatement.setInt(4, emozione.getPunteggio());
                    updateStatement.setString(5, emozione.getNotes());
                    int rowsInserted = updateStatement.executeUpdate();
                    System.out.println(user + ": " + rowsInserted + " emozione inserita.");
                }
            }
            connection.commit(); // Viene eseguito il commit manuale della transazione
        } catch (SQLException e) {
            try {
                //Rollback per annullare tutte le operazioni e tornare allo stato precedente del database
                connection.rollback(); //Annulla la transazione in caso di errore
            } catch (SQLException ex) {
                throw new SQLException("Errore durante l'inserimento delle emozioni: " + e.getMessage());
            }
        } finally {
            if (statement != null) statement.close();
            connection.setAutoCommit(true); // Riabilita il commit automatico
        }
    }

    /**
     * Elimina le emozioni associate a una specifica canzone e utente dal database.
     * @param user L'userID dell'utente di cui eliminare le emozioni.
     * @param idCanzone L'ID della canzone di cui eliminare le emozioni.
     * @return True se l'eliminazione e' riuscita, false altrimenti.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o durante l'esecuzione della query.
     */
    @Override
    public synchronized boolean deleteEmozioni(String user, int idCanzone) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        boolean eliminazioneRiuscita = false;
        try {
            String query = "DELETE FROM Emozioni WHERE userid = ? AND idCanzone = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, user);
            statement.setInt(2, idCanzone);
            int rowsDeleted = statement.executeUpdate();
            System.out.println(user +": "+rowsDeleted + " emozioni eliminate.");
            eliminazioneRiuscita = true;
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione delle emozioni: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return eliminazioneRiuscita;
    }

    /**
     * Elimina una playlist in base all'ID specificato.
     * @param idPlaylist L'ID della playlist da eliminare.
     * @return True se l'eliminazione ha avuto successo, false altrimenti.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query SQL.
     */
    @Override
    public synchronized boolean deletePlaylist(int idPlaylist) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        boolean eliminazioneRiuscita = false;
        try {
            String query = "DELETE FROM Playlist WHERE idplaylist = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idPlaylist);
            int rowsDeleted = statement.executeUpdate();
            System.out.println(rowsDeleted + " playlist eliminata.");
            eliminazioneRiuscita = true;
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della playlist: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return eliminazioneRiuscita;
    }

    /**
     * Rimuove una canzone da una playlist nel database in base agli ID specificati.
     * @param idPlaylist L'ID della playlist dalla quale rimuovere la canzone.
     * @param idCanzone L'ID della canzone da rimuovere.
     * @return True se la rimozione ha avuto successo, false altrimenti.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query SQL.
     */
    @Override
    public synchronized boolean deleteCanzoneDaPlaylist(int idPlaylist, int idCanzone) throws RemoteException, SQLException {
        PreparedStatement statement = null;
        boolean eliminazioneRiuscita = false;
        try {
            String query = "DELETE FROM Playlist_Canzoni WHERE idplaylist = ? AND idcanzone = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idPlaylist);
            statement.setInt(2, idCanzone);
            int rowsDeleted = statement.executeUpdate();
            System.out.println(rowsDeleted + " canzone eliminata dalla playlist.");
            eliminazioneRiuscita = true;
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della canzone: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return eliminazioneRiuscita;
    }

    /**
     * Recupera l'elenco completo delle emozioni dal database.
     * @return Un ArrayList di array di stringhe contenente il nome e la spiegazione di ogni emozione.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized ArrayList<String[]> fetchAllEmotions() throws RemoteException, SQLException {
        ArrayList<String[]> emozioniList = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM categoriaemozione");
            while (rs.next()) {
                String nomeEmozione = rs.getString("nomeEmozione");
                String spiegazioneEmozione = rs.getString("spiegazioneEmozione");
                emozioniList.add(new String[]{nomeEmozione, spiegazioneEmozione});
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante la query di recupero delle emozioni: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return emozioniList;
    }

    /**
     * Restituisce il numero di utenti che hanno lasciato almeno un tag emozionale alla canzone passata come argomento
     * @param idCanzone L'ID della canzone di cui contare gli utenti con emozioni associate.
     * @return Il numero di utenti con emozioni associate alla canzone specificata.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized int getNumUtentiConTagEmozionali(int idCanzone) throws RemoteException, SQLException {
        Statement statement = null;
        int result = 0;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(DISTINCT userid) as numUtenti FROM emozioni WHERE idcanzone ="+idCanzone);
            while (rs.next()) {
                result =  rs.getInt("numUtenti");
            }
        }catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return result;
    }

    /**
     * Restituisce il vettore dei tag emozionali con il numero di utenti
     * che hanno percepito ciascuna emozione ("0" di default)
     * @param idCanzone L'ID della canzone di cui contare gli utenti con ciascuna emozione associata.
     * @return Un vettore di interi contenente il numero di utenti totale associati a ciascuna emozione.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized int[] getNumUtentiPerEmozione(int idCanzone) throws RemoteException, SQLException {
        String[] listaEmozioni = Emozione.getListaEmozioni();
        int[] contEmozioni = new int[listaEmozioni.length];
        Arrays.fill(contEmozioni, 0); //inizializza il conteggio per ciascuna emozione a 0
        //Mappa che associa il nome dell'emozione al suo indice nell'array
        Map<String, Integer> emozioneIndexMap = new HashMap<>();
        for (int i = 0; i < listaEmozioni.length; i++) {
            emozioneIndexMap.put(listaEmozioni[i], i);
        }
        PreparedStatement statement = null;
        try {
            String query= "SELECT nomeEmozione, COUNT(DISTINCT userid) FROM Emozioni WHERE idCanzone = ? " +
                    "GROUP BY nomeEmozione";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idCanzone);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String nomeEmozione = rs.getString("nomeEmozione");
                int count = rs.getInt(2);
                //Indice corrispondente all'emozione dalla mappa
                Integer index = emozioneIndexMap.get(nomeEmozione);
                if (index != null) {
                    //Aggiorna il conteggio dell'emozione nell'array corrispondente
                    contEmozioni[index] = count;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return contEmozioni;
    }

    /**
     * Restituisce il vettore con il punteggio totale delle emozioni associate a una specifica canzone.
     * @param idCanzone L'ID della canzone di cui ottenere il punteggio delle emozioni.
     * @return Un vettore di interi contenente il punteggio totale di ciascuna emozione associata alla canzone.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized int[] getPunteggioEmozioniTotali(int idCanzone) throws RemoteException, SQLException {
        String[] listaEmozioni = Emozione.getListaEmozioni();
        int[] punteggioEmozioni = new int[listaEmozioni.length];
        Arrays.fill(punteggioEmozioni, 0);
        PreparedStatement statement = null;
        try {
            String query = "SELECT nomeEmozione, score FROM Emozioni WHERE idCanzone = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idCanzone);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String nomeEmozione = rs.getString("nomeEmozione");
                int score = rs.getInt("score");
                int index = Arrays.asList(listaEmozioni).indexOf(nomeEmozione);
                if (index != -1) {
                    punteggioEmozioni[index] += score;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return punteggioEmozioni;
    }

    /**
     * Restituisce il punteggio delle emozioni associate a una specifica canzone per un utente specifico.
     * @param user L'userID dell'utente di cui ottenere il punteggio delle emozioni per la canzone.
     * @param idCanzone L'ID della canzone di cui ottenere il punteggio delle emozioni.
     * @return Un vettore di interi contenente il punteggio di ciascuna emozione associata alla canzone per l'utente specificato.
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized int[] getPunteggioEmozioniPerUtente(String user, int idCanzone) throws RemoteException, SQLException {
        String[] listaEmozioni = Emozione.getListaEmozioni();
        int[] punteggioEmozioniPerUtente = new int[listaEmozioni.length];
        Arrays.fill(punteggioEmozioniPerUtente, 0);
        PreparedStatement statement = null;
        try {
            String query = "SELECT nomeEmozione, score FROM Emozioni WHERE idCanzone = ? AND userid = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idCanzone);
            statement.setString(2, user);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String nomeEmozione = rs.getString("nomeEmozione");
                int score = rs.getInt("score");
                int index = Arrays.asList(listaEmozioni).indexOf(nomeEmozione);
                if (index != -1) {
                    punteggioEmozioniPerUtente[index] = score;
                }
            }
            return punteggioEmozioniPerUtente;
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
    }

    /**
     * Restituisce L'array dei tag emozionali con i rispettivi commenti per ciascuna emozione, nel caso ce ne siano
     * @param idCanzone L'ID della canzone di cui ottenere i commenti per ciascuna emozione.
     * @return Un array di stringhe contenente i commenti associati a ciascuna emozione lasciati dagli utenti
     * @throws RemoteException Se si verifica un errore durante l'esecuzione della chiamata remota.
     * @throws SQLException Se si verifica un errore durante l'accesso al database o l'esecuzione della query.
     */
    @Override
    public synchronized String[] getCommentiPerEmozione(int idCanzone) throws RemoteException, SQLException {
        String[] listaEmozioni = Emozione.getListaEmozioni();
        String[] datiCommenti = new String[9];
        Arrays.fill(datiCommenti, "");
        PreparedStatement statement = null;
        try {
            String query = "SELECT u.nome, e.nomeEmozione, e.noteTestuali FROM Emozioni e " +
                    "JOIN UtentiRegistrati u ON e.userid = u.userid WHERE e.idCanzone = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, idCanzone);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String nomeUtente = rs.getString("nome");
                String nomeEmozione = rs.getString("nomeemozione");
                String noteTestuali = rs.getString("notetestuali");
                if (noteTestuali != null && !noteTestuali.isEmpty()) {
                    for (int i = 0; i < listaEmozioni.length; i++) {
                        if (nomeEmozione.equals(listaEmozioni[i])) {
                            datiCommenti[i] += nomeUtente + ": " + noteTestuali + "\n\n";
                            break;//Esce dal ciclo una volta trovata la corrispondenza
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage());
        } finally {
            if (statement != null) statement.close();
        }
        return datiCommenti;
    }
}
