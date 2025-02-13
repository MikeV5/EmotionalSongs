package emotionalsongs;

import client.ConnessioneServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import util.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;


/**
 * Classe Controller che mostra la scena di consultazione delle canzoni.
 * Gestisce la ricerca dei brani musicali e permette di visualizzare le emozioni associate ad esse.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ConsultaCanzoniController implements Initializable {
    /**
     * Label per visualizzare il nome dell'utente corrente.
     */
    @FXML
    private Label userLabel;
    /**
     * Campo di testo utilizzato per cercare una canzone nella tabella.
     */
    @FXML
    private TextField canzoneTextField;
    /**
     * Tabella per visualizzare l'elenco delle canzoni disponibili.
     */
    @FXML
    private TableView<Canzone> songTable;
    /**
     * Colonna per l'ID delle canzoni.
     * Questa colonna e' invisibile agli utenti, ma utilizzata internamente per il recupero dei dati.
     */
    @FXML
    private TableColumn<Canzone, Integer> idCol, annoCol;
    /**
     * Colonne per visualizzare rispettivamente
     * i titoli, gli autori, gli anni d'uscita e la durata delle canzoni.
     */
    @FXML
    private TableColumn<Canzone, String> titoloCol, autoreCol, durataCol;
    /**
     * Oggetto di tipo {@link MainController}, utilizzato per impostare
     * la scena che si vuole visualizzare.
     */
    private static MainController mainController;
    /**
     * Oggetto di tipo {@link ConnessioneServer}, utilizzato per gestire la connessione al server.
     */
    private ConnessioneServer connectServer;

    /**
     * Oggetto di tipo {@link SongManager}, utilizzato per gestire l'elenco delle canzoni.
     */
    private SongManager songManager;

    /**
     * Inizializza la scena con il catalago dei brani e filtra la tabella dinamicamente
     * a seconda dei dati inseriti.
     */
    public void initialize(URL url, ResourceBundle rb) {
        connectServer = ConnessioneServer.getIstanza();
        songManager = SongManager.getIstanza();
        userLabel.setText(connectServer.getUserLoggato());

        if (songManager.getAllSongs().isEmpty()) {
            caricaCanzoni(connectServer,songManager);
        }
        //Verifica se è necessario aggiornare le canzoni
        if (checkAggiornamentoCanzoni(connectServer, songManager)) {
            caricaCanzoni(connectServer, songManager);
        }
        //Popola la tabella con le canzoni
        popolaTabella(songManager, songTable);
        //Imposta il filtro dinamico per la tabella
        try {
            TableUtil.cercaBranoMusicale(songTable, idCol, titoloCol, autoreCol, annoCol, durataCol, canzoneTextField);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica se e' necessario aggiornare l'elenco delle canzoni in base al timestamp di ultima modifica.
     * @param connectServer L'istanza della connessione al server remoto.
     * @param songManager L'istanza del gestore delle canzoni.
     * @return True se e' necessario aggiornare l'elenco delle canzoni, altrimenti false.
     */
    public static boolean checkAggiornamentoCanzoni(ConnessioneServer connectServer, SongManager songManager) {
        long ultimoTimeStampTabellaDB = 0;
        long ultimoTimeStampClient = songManager.getUltimoTimeStampClient();
        try {
            //Ottiene il timestamp dell'ultima modifica della tabella Canzoni
            ultimoTimeStampTabellaDB = connectServer.richiestaUltimoTimeStampModifica();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (ultimoTimeStampTabellaDB > ultimoTimeStampClient) {
            AlertDialogUtil.showReloadTabellaDialog();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Carica il catalogo delle canzoni dal server e aggiorna l'elenco nel gestore delle canzoni.
     * @param connectServer L'istanza della connessione al server remoto.
     * @param songManager L'istanza del gestore delle canzoni.
     */
    public static void caricaCanzoni(ConnessioneServer connectServer, SongManager songManager) {
        //Richiede le canzoni dal server
        HashMap<Integer, Canzone> canzoniMap = null;
        long ultimoTimeStampClient;
        try {
            canzoniMap = connectServer.getCatalagoCanzoni();
            ultimoTimeStampClient = connectServer.richiestaUltimoTimeStampModifica();
            songManager.setUltimoTimeStampClient(ultimoTimeStampClient);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        //Svuota la mappa delle canzoni esistente prima di aggiornarla
        songManager.clearTableSongs();
        //Aggiunge le canzoni ottenute dal server al SongManager
        for (Canzone canzone : canzoniMap.values()) {
            songManager.addSong(canzone);
        }
    }

    /**
     * Popola la tabella delle canzoni con i dati dal gestore delle canzoni.
     * @param songManager L'istanza del gestore delle canzoni.
     * @param songTable La tabella in cui popolare i dati delle canzoni.
     */
    public static void popolaTabella(SongManager songManager, TableView<Canzone> songTable) {
        //Ottiene tutte le canzoni dal SongManager
        Collection<Canzone> songs = songManager.getAllSongs();
        songTable.getItems().addAll(songs);
    }

    /**
     * Gestisce l'evento del doppio click su una canzone.
     * Quando viene selezionata una canzone, viene caricata la scena {@code statsEmozioneBrano.fxml}
     * controllata da {@link StatsEmozioneBranoController}.
     * @param event L'evento generato dal click.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void selezionaCanzone(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2 && songTable.getSelectionModel().getSelectedItem() != null) {
            Canzone canzoneSelezionata = songTable.getSelectionModel().getSelectedItem();
            connectServer.setCanzoneCorrente(canzoneSelezionata);
            visualizzaEmozioneBrano(connectServer);
        }
    }

    /**
     * Visualizza le statistiche di emozioni associate alla canzone selezionata.
     * Decide quale scena di statistiche visualizzare in base al numero di utenti con tag emozionali.
     * @param connectServer Oggetto di {@link ConnessioneServer} utilizzato per gestire la connessione al server.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public static void visualizzaEmozioneBrano(ConnessioneServer connectServer) throws IOException {
        mainController = new MainController();
        int numUtentiConTagEmozionali = connectServer.richiestaNumUtentiConTagEmozionali();
        if (numUtentiConTagEmozionali != 0) {
            mainController.scenaStatsBrano(730, 750);
        } else {
            mainController.scenaStatsBrano( 730, 230);
        }
    }

    /**
     * Ritorna alla scena principale {@code loginMenu.fxml} controllata da {@link LoginController}
     * se l'utente non e' loggato,
     * altrimenti alla rispettiva libreria playlist {@link ListaPlaylistController}.
     * @param event l'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void tornaScena(ActionEvent event) throws IOException {
        mainController = new MainController();
        if(connectServer.isLogged()){
            mainController.scenaListaPlaylist();
        }else {
            mainController.scenaMenuPrincipale();
        }
    }

}
