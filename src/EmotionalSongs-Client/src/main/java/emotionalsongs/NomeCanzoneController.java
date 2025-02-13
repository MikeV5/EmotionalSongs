package emotionalsongs;

import client.ConnessioneServer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Classe Controller che permette di aggiungere una canzone nella playlist corrente.
 * @author Ashley Chudory, Matricola 746423, Sede CO.
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO.
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO.
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO.
 */
public class NomeCanzoneController implements Initializable {
    /**
     * Label per visualizzare il nome dell'utente corrente ed eventuali errori.
     */
    @FXML
    private Label userLabel, erroreLabel;
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
     * Variabile booleana che indica se la connessione al server e' stata stabilita correttamente.
     */
    private boolean connesso;
    /**
     * Oggetto di tipo {@link MainController}, utilizzato per impostare
     * la scena che si vuole visualizzare.
     */
    private MainController mainController;
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
        userLabel.setText("User: "+ connectServer.getUserLoggato());
        songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (songManager.getAllSongs().isEmpty()) {
            ConsultaCanzoniController.caricaCanzoni(connectServer,songManager);
        }
        //Verifica se è necessario aggiornare le canzoni
        if (ConsultaCanzoniController.checkAggiornamentoCanzoni(connectServer, songManager)) {
            ConsultaCanzoniController.caricaCanzoni(connectServer, songManager);
        }
        //Popola la tabella con le canzoni
        ConsultaCanzoniController.popolaTabella(songManager,songTable);
        //Imposta il filtro dinamico per la tabella
        try {
            TableUtil.cercaBranoMusicale(songTable, idCol, titoloCol, autoreCol, annoCol, durataCol, canzoneTextField);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aggiunge le canzoni selezionate nella playlist corrente.
     * @param event L'evento generato dal click sul pulsante "Salva".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void salvaNomeCanzone(ActionEvent event) throws IOException {
        ObservableList<Canzone> canzoniSelezionate = songTable.getSelectionModel().getSelectedItems();
        ArrayList<Canzone> canzoniPlaylist = connectServer.getCanzoniPlaylist();
        ArrayList<String> canzoniDuplicateNomi = new ArrayList<>();
        for (Canzone canzoneSelezionata : canzoniSelezionate) {
            int idCanzoneSelezionata = canzoneSelezionata.getIdCanzone();

            for (Canzone canzonePlaylist : canzoniPlaylist) {
                if (canzonePlaylist.getIdCanzone() == idCanzoneSelezionata) {
                    canzoniDuplicateNomi.add(canzoneSelezionata.getNomeCanzone());
                    break; //Interrompe il ciclo una volta trovata una corrispondenza
                }
            }
        }
        if (!canzoniDuplicateNomi.isEmpty()) {
            String nomiCanzoniDuplicate = String.join(", ", canzoniDuplicateNomi);
            AlertDialogUtil.showErrorDialog("Canzoni duplicate nella playlist: \n" + nomiCanzoniDuplicate);
        } else {
            mainController = new MainController();
            System.out.println("Nessun duplicato.");
            ArrayList<Canzone> canzoni = new ArrayList<>(canzoniSelezionate); //Converte ObservableList in ArrayList
            boolean isInserted = connectServer.richiestaInsertCanzoni(canzoni);
            if(isInserted) {
                AlertDialogUtil.showSuccessDialog("Canzoni inserite con successo.");
                mainController.scenaListaCanzoni();
            }else {
                String messaggio = "Errore durante l'inserimento dei brani.";
                erroreLabel.setText(messaggio);
                AlertDialogUtil.showErrorDialog(messaggio);
            }
        }
    }

    /**
     * Annulla l'aggiunta del brano e ritorna alla scena {@code listaCanzoni.fxml} controllata da  {@link ListaCanzoniController}.
     * @param event L'evento generato dal click sul pulsante "Annulla".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void annullaNomeCanzone(ActionEvent event) throws IOException {
        connesso = connectServer.testConnessione();
        if(connesso) {
            mainController = new MainController();
            mainController.scenaListaCanzoni();
        }else AlertDialogUtil.showErroreConnessioneDialog();
    }
}
