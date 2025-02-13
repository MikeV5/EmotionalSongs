package interfacce;
import eccezioni.UtenteInesistenteException;
import util.Canzone;
import util.Emozione;
import util.Playlist;
import util.Utente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Interfaccia remota per il servizio EmotSongs.
 * L'interfaccia è utilizzata per la comunicazione RMI tra client e server.
 * Definisce i metodi che permettono di interagire con il servizio
 * per eseguire varie operazioni riguardanti gli utenti, le canzoni, le emozioni e altre funzionalità.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public interface EmotSongsInterface extends Remote {
    String login(String username, String password) throws RemoteException, SQLException;
    UUID loginSession(String username) throws RemoteException;
    void chiudiSessione(String username, UUID sessionId) throws RemoteException;
    boolean isUserIDAvailable(String user) throws RemoteException, SQLException;
    Utente fetchUtenteByUser(String userid) throws RemoteException, UtenteInesistenteException, SQLException;
    void insertUtente(Utente u) throws RemoteException, SQLException;
    HashMap<Integer,Canzone> fetchAllSongs() throws RemoteException, SQLException;
    long getUltimoTimeStamp() throws RemoteException, SQLException;
    ArrayList<Playlist> fetchPlaylistByUser(String user) throws RemoteException, SQLException;
    ArrayList<Canzone> fetchCanzoniByidPlaylist(int idPlaylist) throws RemoteException, SQLException;
    boolean insertCanzoni(String user, int idPlaylist, ArrayList<Canzone> canzoni) throws RemoteException, SQLException;
    void insertPlaylist(String user, String nomePlaylist) throws RemoteException, SQLException;
    void insertEmozioni(String user, Canzone canzone, ArrayList<Emozione> datiEmotion) throws RemoteException, SQLException;
    boolean deleteEmozioni(String user, int idCanzone) throws RemoteException, SQLException;
    boolean deletePlaylist(int idPlaylist) throws RemoteException, SQLException;
    boolean deleteCanzoneDaPlaylist(int idPlaylist, int idCanzone) throws RemoteException, SQLException;
    ArrayList<String[]> fetchAllEmotions() throws RemoteException, SQLException;
    int getNumUtentiConTagEmozionali(int idCanzone) throws RemoteException, SQLException;
    int[] getNumUtentiPerEmozione(int idCanzone) throws RemoteException, SQLException;
    int[] getPunteggioEmozioniTotali(int idCanzone) throws RemoteException, SQLException;
    int[] getPunteggioEmozioniPerUtente(String user, int idCanzone) throws RemoteException, SQLException;
    String[] getCommentiPerEmozione(int idCanzone) throws RemoteException, SQLException;
}
