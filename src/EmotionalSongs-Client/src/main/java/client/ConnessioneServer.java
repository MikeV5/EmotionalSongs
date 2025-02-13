package client;
import interfacce.EmotSongsInterface;
import util.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Classe che gestisce la connessione e le richieste al server RMI 'EmotionalSongs'.
 * Inoltre, fornisce un'istanza singleton della connessione per l'intero progetto.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ConnessioneServer {
    /**
     * Indirizzo host di default per la connessione.
     */
    private final String defaultHostAddress = "localhost";
    /**
     * Indirizzo host impostato dall'utente per la connessione.
     */
    private final int defaultPORT = 1099;

    /**
     * Porta impostata dall'utente per la connessione.
     */
    private String hostAddress;
    /**
     * Porta per la connessione.
     */
    private int port;

    /**
     * Registro RMI utilizzato per la connessione al server.
     */
    private Registry registry;

    /**
     * UserID dell'utente loggato.
     */
    private String userLoggato;

    /**
     * Playlist attualmente attiva nell'applicazione.
     */
    private Playlist playlistCorrente;

    /**
     * Canzone attualmente attiva nell'applicazione.
     */
    private Canzone canzoneCorrente;

    /**
     * ID della sessione corrente dell'utente.
     */
    private UUID sessionID;

    /**
     * Nome del server RMI da utilizzare per la connessione.
     */
    private final String nomeServer = "EmotSongsInterface";

    /**
     * Stub dell'interfaccia {@code EmotSongsInterface} utilizzato per comunicare con il server.
     */
    private EmotSongsInterface stub = null;

    /**
     * Istanza singleton di {@code ConnessioneServer}.
     */
    private static ConnessioneServer istanza;

    /**
     * Costruttore privato del singleton. Non e' possibile istanziare questa classe direttamente.
     * Imposta l'indirizzo host e la porta di default per la connessione al server. Inoltre,
     * inizializza l'ID di sessione a null.
     */
    private ConnessioneServer() {
        hostAddress = defaultHostAddress;
        port = defaultPORT;
        sessionID = null; //Inizialmente non abbiamo ancora una sessione
    }
    /**
     * Restituisce l'istanza del singleton {@code ConnessioneServer}.
     * Se l'istanza non esiste, ne crea una nuova e la restituisce.
     * @return L'istanza del singleton {@code ConnessioneServer}.
     */
    public static ConnessioneServer getIstanza() {
        if (istanza == null) {
            istanza = new ConnessioneServer();
        }
        return istanza;
    }

    /**
     * Testa la connessione al server RMI.
     * @return True se la connessione e' stabilita con successo, altrimenti false.
     */
    public boolean testConnessione() {
        boolean esito = true;
        try {
            Registry registry = LocateRegistry.getRegistry(hostAddress, port);
            EmotSongsInterface stub = (EmotSongsInterface) registry.lookup(nomeServer);
            esito = (stub != null); //Se esiste true
        } catch (RemoteException | NotBoundException e) {
            esito = false;
        }
        return esito;
    }

    /**
     * Stabilisce la connessione al server RMI.
     * @return True se la connessione e' stabilita con successo, altrimenti false.
     */
    public boolean connectToServer() { //true se si stabilisce la connessione
        boolean esitoConnessione = testConnessione();
        if(esitoConnessione) {
            try {
                registry = LocateRegistry.getRegistry(hostAddress, port);
                stub = (EmotSongsInterface) registry.lookup(nomeServer);
            } catch (RemoteException | NotBoundException e) {
                AlertDialogUtil.showErroreConnessioneDialog();
            }
        } else
            AlertDialogUtil.showErroreConnessioneDialog();
        return esitoConnessione;
    }

    /**
     * Invia una richiesta al server per verificare le credenziali dell'utente durante il tentativo di login.
     * @param username L'userID dell'utente.
     * @param password La password dell'utente.
     * @return True se il login ha successo, altrimenti false.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean verificaLogin(String username, String password) throws RemoteException {
        if(!connectToServer())
            return false;
        boolean esito = false;
        try {
            userLoggato = stub.login(username, password); //richiesta al server
            esito = (userLoggato != null); //se true esiste e accede
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return esito;
    }

    /**
     * Invia una richiesta al server per verificare se l'userID specificato e' disponibile per la registrazione.
     * @param username L'userID da verificare.
     * @return True se l'userID e' disponibile, altrimenti false.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean verificaUserID(String username) throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa.");
        boolean esito;
        try {
            esito = stub.isUserIDAvailable(username); //richiesta al server
        } catch (RemoteException e) {
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return esito;
    }

    /**
     * Invia una richiesta al server per ottenere l'ID di sessione associato all'utente loggato.
     * @param username L'userID dell'utente.
     * @return L'ID di sessione dell'utente.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public UUID sessionUser(String username) throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa.");
        UUID sessioneUser = null;
        try {
            sessioneUser = stub.loginSession(username); //richiesta al server
        } catch (RemoteException e) {
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return sessioneUser;
    }

    /**
     * Invia una richiesta al server per ottenere la lista delle playlist associate all'utente attualmente loggato.
     * @return Un arraylist delle playlist dell'utente corrente.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public ArrayList<Playlist> getUserPlaylists() throws RemoteException{
         if(!connectToServer())
            throw new RemoteException("Connessione persa");
        ArrayList<Playlist> listaPlaylist = null;
        try {
            listaPlaylist = stub.fetchPlaylistByUser(userLoggato);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return listaPlaylist;
    }

    /**
     * Invia una richiesta al server per inserire una nuova playlist associata all'utente corrente.
     * @param nomeplaylist Il nome della nuova playlist.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public void richiestaInsertPlaylist(String nomeplaylist) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        try {
            stub.insertPlaylist(userLoggato,nomeplaylist);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
    }

    /**
     * Invia una richiesta al server per ottenere la lista delle canzoni
     * associate alla playlist selezionata dall'utente.
     * @return Un ArrayList di Playlist
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public ArrayList<Canzone> getCanzoniPlaylist() throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        ArrayList<Canzone> listaCanzoni = null;
        try {
            listaCanzoni = stub.fetchCanzoniByidPlaylist(playlistCorrente.getIdPlaylist());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return listaCanzoni;
    }

    /**
     * Invia una richiesta al server per ottenere il catalogo completo
     * di tutte le canzoni disponibili.
     * @return Una mappa contenente l'elenco delle canzoni con il rispettivo ID come chiave.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public HashMap<Integer,Canzone> getCatalagoCanzoni() throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        HashMap<Integer, Canzone> listaCanzoni = null;
        try {
            listaCanzoni = stub.fetchAllSongs();
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return listaCanzoni;
    }

    /**
     * Invia una richiesta al server per ottenere l'ultimo timestamp
     * di modifica delle canzoni.
     * @return L'ultimo timestamp di modifica delle canzoni.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public long richiestaUltimoTimeStampModifica() throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        long ultimaModifica;
        try {
            ultimaModifica = stub.getUltimoTimeStamp();
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return ultimaModifica;
    }

    /**
     * Invia una richiesta al server per inserire nuova canzone nella playlist.
     * @param canzoni L'elenco delle canzoni da inserire nella playlist.
     * @return True se l'inserimento della canzone e' avvenuto con successo, false altrimenti.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean richiestaInsertCanzoni(ArrayList<Canzone> canzoni) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        boolean esito = false;
        try {
            esito = stub.insertCanzoni(userLoggato, playlistCorrente.getIdPlaylist(), canzoni);
        } catch (RemoteException e) {
            AlertDialogUtil.showErrorDialog("Canzone duplicata nella playlist.");
        }
        return esito;
    }

    /**
     * Invia una richiesta al server per inserire un nuovo utente.
     * @param utente Il nuovo utente da inserire.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public void richiestaInsertUtente(Utente utente) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        try {
            stub.insertUtente(utente);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
    }

    /**
     * Invia una richiesta al server per ottenere una lista contenente le emozioni associate alla canzone corrente
     * e i relativi commenti lasciati dagli utenti.
     * @return Una arraylist di stringhe contenente le emozioni e i commenti associati alla canzone.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public ArrayList<String[]> getEmotionsTable() throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        ArrayList<String[]> emozioniList = null;
        try {
            emozioniList = stub.fetchAllEmotions();
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return emozioniList;
    }

    /**
     * Invia una richiesta al server per inserire le emozioni passate come argomento
     * e associarle alla canzone corrente, insieme all'utente attualmente loggato.
     * @param datiEmotion Un Arraylist di {@link Emozione} contenente le emozioni da inserire.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public void richiestaInsertEmozione(ArrayList<Emozione> datiEmotion) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        try {
            stub.insertEmozioni(userLoggato, canzoneCorrente, datiEmotion);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
    }

    /**
     * Invia una richiesta al server per ottenere il numero di utenti che hanno lasciato
     * almeno un tag emozionale alla canzone corrente.
     * @return Il numero di utenti con emozioni associate alla canzone corrente.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public int richiestaNumUtentiConTagEmozionali() throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        int numUtenti = 0;
        try {
            numUtenti = stub.getNumUtentiConTagEmozionali(canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return numUtenti;
    }

    /**
     * Invia una richiesta al server per ottenere un vettore contenente il numero di utenti
     * associati a ciascuna emozione della canzone corrente.
     * @return Un vettore di interi contenente il numero di utenti associati a ciascuna emozione della canzone.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public int[] richiestaNumUtentiPerEmozione() throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        int[] dati = null;
        try {
            dati = stub.getNumUtentiPerEmozione(canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return dati;
    }

    /**
     * Invia una richiesta al server per ottenere un vettore contenente il punteggio totale
     * di ciascuna emozione della canzone corrente.
     * @return Un vettore di interi contenente il punteggio totale di ciascuna emozione della canzone.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public int[] richiestaPunteggioEmozioniTotali() throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        int[] dati = null;
        try {
            dati = stub.getPunteggioEmozioniTotali(canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return dati;
    }

    /**
     * Invia una richiesta al server per ottenere un vettore contenente il punteggio di ciascuna emozione
     * assegnata dall'utente loggato alla canzone corrente.
     * @return Un vettore di interi contenente il punteggio di ciascuna emozione assegnata dall'utente alla canzone.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public int[] richiestaPunteggioEmozioniPerUtente() throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        int[] dati = null;
        try {
            dati = stub.getPunteggioEmozioniPerUtente(userLoggato, canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return dati;
    }

    /**
     * Invia una richiesta al server per ottenere un array dei tag emozionali
     * con i rispettivi commenti per ciascuna emozione, nel caso ce ne siano.
     * @return Un array contenente i commenti associati a ciascuna emozione lasciati dagli utenti
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public String[] richiestaCommentiPerEmozione() throws RemoteException {
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        String[] commentiUtenti = null;
        try {
            commentiUtenti = stub.getCommentiPerEmozione(canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return commentiUtenti;
    }

    /**
     * Invia una richiesta al server per verificare se e' possibile rimuovere i punteggi delle emozioni
     * assegnate dall'utente alla canzone corrente.
     * @return True se e' possibile rimuovere i punteggi delle emozioni, altrimenti false.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean checkRimuoviPunteggiEmozioni() throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        boolean esito = false;
        try {
            esito = stub.deleteEmozioni(userLoggato,canzoneCorrente.getIdCanzone());
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return esito;
    }

    /**
     * Invia una richiesta al server per rimuovere la playlist selezionata dall'utente.
     * @param idPlaylist L'ID della playlist da rimuovere.
     * @return True se la playlist e' stata rimossa con successo, altrimenti false.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean checkRimuoviPlaylist(int idPlaylist) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        boolean esito = false;
        try {
            esito = stub.deletePlaylist(idPlaylist);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return esito;
    }

    /**
     * Invia una richiesta al server per rimuovere la canzone specificata dalla playlist corrente.
     * @param idCanzone L'ID della canzone da rimuovere dalla playlist corrente.
     * @return True se la canzone e' stata rimossa dalla playlistcon successo, altrimenti false.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public boolean checkRimuoviCanzoneDaPlaylist(int idCanzone) throws RemoteException{
        if(!connectToServer())
            throw new RemoteException("Connessione persa");
        boolean esito = false;
        try {
            esito = stub.deleteCanzoneDaPlaylist(playlistCorrente.getIdPlaylist(),idCanzone);
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
        return esito;
    }


    /**
     * Rimuove la sessione corrente dell'utente loggato.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public void rimuoviSessione() throws RemoteException {
        if (!connectToServer()) {
            throw new RemoteException("Connessione persa");
        }
        try {
            stub.chiudiSessione(userLoggato, sessionID);
            //System.out.println(userLoggato + " - SessionID: " + sessionID + " rimosso.");
        } catch (RemoteException e) {
            AlertDialogUtil.showErroreConnessioneDialog();
            throw new RemoteException("Errore durante la comunicazione RMI: " + e.getMessage());
        }
    }

    /**
     * Verifica se l'utente e' loggato.
     * @return True se l'utente e' loggato, altrimenti false.
     */
    public boolean isLogged() {
        return userLoggato != null;
    }

    /**
     * Restituisce l'indirizzo IP di default del server.
     * @return L'indirizzo IP di default.
     */
    public String getDefaultHostAddress() {
        return defaultHostAddress;
    }

    /**
     * Restituisce la porta di default del server.
     * @return La porta del server.
     */
    public int getDefaultPORT() {
        return defaultPORT;
    }

    /**
     * Restituisce l'indirizzo IP del server.
     * @return L'indirizzo IP del server.
     */
    public String getHostAddress() {
        return hostAddress;
    }

    /**
     * Imposta l'indirizzo IP del server.
     * @param hostAddress L'indirizzo IP del server da impostare.
     */
    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    /**
     * Restituisce la porta del server.
     * @return La porta del server.
     */
    public int getPort() {
        return port;
    }
    /**
     * Imposta la porta del server.
     * @param port La porta del server da impostare.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Restituisce l'userID dell'utente loggato.
     * @return L'userID dell'utente.
     */
    public String getUserLoggato() {
        return userLoggato;
    }

    /**
     * Restituisce la playlist corrente dell'utente attualmente loggato.
     * @return La playlist corrente.
     */
    public Playlist getPlaylistCorrente() {
        return playlistCorrente;
    }

    /**
     * Imposta la playlist corrente.
     * @param playlistCorrente La playlist corrente da impostare.
     */
    public void setPlaylistCorrente(Playlist playlistCorrente) {
        this.playlistCorrente = playlistCorrente;
    }

    /**
     * Restituisce la canzone corrente dell'utente attualmente loggato.
     * @return La canzone corrente.
     */
    public Canzone getCanzoneCorrente() {
        return canzoneCorrente;
    }

    /**
     * Imposta la canzone corrente.
     * @param canzoneCorrente La canzone corrente da impostare.
     */
    public void setCanzoneCorrente(Canzone canzoneCorrente) {
        this.canzoneCorrente = canzoneCorrente;
    }

    /**
     * Restituisce l'ID della sessione corrente dell'utente.
     * @return L'ID della sessione corrente dell'utente.
     */
    public UUID getSessionID() {
        return sessionID;
    }
    /**
     * Imposta l'ID della sessione corrente dell'utente.
     * @param sessionID L'ID della sessione corrente dell'utente da impostare.
     */
    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Resetta gli attributi e disconnette l'utente dal server.
     */
    public void disconnetti(){
        stub = null;
        userLoggato = null;
        playlistCorrente = null;
        canzoneCorrente = null;
        sessionID = null;
    }


}
