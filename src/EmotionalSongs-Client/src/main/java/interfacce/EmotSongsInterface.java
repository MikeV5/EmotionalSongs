package interfacce;

import util.Canzone;
import util.Emozione;
import util.Playlist;
import util.Utente;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Interfaccia remota per il servizio EmotSongs.
 * L'interfaccia e' utilizzata per la comunicazione RMI tra client e server.
 * Definisce i metodi che permettono di interagire con il servizio
 * per eseguire varie operazioni riguardanti gli utenti, le canzoni, le emozioni e altre funzionalità.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public interface EmotSongsInterface extends Remote {
    String login(String username, String password) throws RemoteException;
    UUID loginSession(String username) throws RemoteException;
    void chiudiSessione(String username, UUID sessionId) throws RemoteException;
    boolean isUserIDAvailable(String user)throws RemoteException;
    Utente fetchUtenteByUser(String userid) throws RemoteException;
    void insertUtente(Utente u) throws RemoteException;
    HashMap<Integer, Canzone> fetchAllSongs() throws RemoteException;
    long getUltimoTimeStamp() throws RemoteException;
    ArrayList<Playlist> fetchPlaylistByUser(String user) throws RemoteException;
    ArrayList<Canzone> fetchCanzoniByidPlaylist(int idPlaylist) throws RemoteException;
    boolean insertCanzoni(String user, int idPlaylist, ArrayList<Canzone> canzoni) throws RemoteException;
    void insertPlaylist(String user, String nomePlaylist) throws RemoteException;
    void insertEmozioni(String user, Canzone canzone, ArrayList<Emozione> datiEmotion) throws RemoteException;
    boolean deleteEmozioni(String user, int idCanzone)throws RemoteException;
    boolean deletePlaylist(int idPlaylist) throws RemoteException;
    boolean deleteCanzoneDaPlaylist(int idPlaylist, int idCanzone) throws RemoteException;
    ArrayList<String[]> fetchAllEmotions() throws RemoteException;
    int getNumUtentiConTagEmozionali(int idCanzone) throws RemoteException;
    int[] getNumUtentiPerEmozione(int idCanzone)throws RemoteException;
    int[] getPunteggioEmozioniTotali(int idCanzone) throws RemoteException;
    int[] getPunteggioEmozioniPerUtente(String user, int idCanzone) throws RemoteException;
    String[] getCommentiPerEmozione(int idCanzone) throws RemoteException;
}
