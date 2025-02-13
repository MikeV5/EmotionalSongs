package emotionalsongs;

import client.ConnessioneServer;
import javafx.collections.FXCollections;
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
 * Classe Controller che riporta e gestisce la tabella con tutte le emozioni
 * ai quali inserire un punteggio per il brano selezionato.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ListaEmozioniController implements Initializable {
    /**
     * Label per visualizzare l'utente, la playlist e
     * la canzone corrente. Inoltre, quelle per visualizzare i punteggi delle emozioni.
     */
    @FXML
    private Label userLabel, playlistLabel, canzoneLabel, myEmotionsLabel,
            scrittaEmotionsLabel,infoMyEmotionsLabel;
    /**
     * Tabella per visualizzare le emozioni associate alla canzone corrente.
     */
    @FXML
    private TableView<TableUtil> emotionTable;
    /**
     * Colonna per visualizzare la categoria dell'emozione.
     */
    @FXML
    private TableColumn<TableUtil, String> categoryCol;
    /**
     * Colonna per visualizzare la spiegazione dell'emozione.
     */
    @FXML
    private TableColumn<TableUtil, String> explanationCol;
    /**
     * Colonna che contiene una {@code ComboBox}
     * per selezionare il punteggio desiderato.
     */
    @FXML
    private TableColumn<TableUtil, ComboBox> scoreCol;
    /**
     * Colonna che contiene una {@code TextArea} per inserire eventuali commenti alle emozioni.
     */
    @FXML
    private TableColumn<TableUtil, TextArea> notesCol;
    /**
     * L'ID dell'utente corrente
     */
    private String username;
    /**
     * La playlist corrente.
     */
    private Playlist playlistCorrente;
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
     * ScrollPane contenente il label delle emozioni per l'utente loggato.
     */
    @FXML
    private ScrollPane scrollPaneEmotions;
    /**
     * Pulsante per rimuovere i punteggi.
     */
    @FXML
    private Button rimuoviEmotionsButton;

    /**
     * Metodo che inizializza il controller quando viene creato.
     * Imposta i label con i dati correnti e popola la tabella delle emozioni.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        username = connectServer.getUserLoggato();
        userLabel.setText("User: "+ username);
        playlistCorrente = connectServer.getPlaylistCorrente();
        playlistLabel.setText(playlistCorrente.getNomePlaylist());
        canzoneLabel.setText(connectServer.getCanzoneCorrente().getNomeCanzone());
        ArrayList<String[]> emozioniTable = null;
        int[] punteggioEmozioniPerUtente = null;
        try {
            emozioniTable = connectServer.getEmotionsTable();
            punteggioEmozioniPerUtente = connectServer.richiestaPunteggioEmozioniPerUtente();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (emozioniTable != null) {
            inizializzaTable(emozioniTable);
        }
        StatsBrano.resizeEmotionsLabel(myEmotionsLabel, rimuoviEmotionsButton, scrittaEmotionsLabel,
                scrollPaneEmotions, username, infoMyEmotionsLabel, punteggioEmozioniPerUtente);
    }

    /**
     * Inizializza la tabella con i dati di default per ciascuna delle 9 emozioni.
     * @param emozioniTable Lista di array contenente i dati delle emozioni da inserire nella tabella.
     */
    private void inizializzaTable(ArrayList<String[]> emozioniTable) {
        ObservableList<TableUtil> emotionDataList = FXCollections.observableArrayList();
        for (String[] tuple : emozioniTable) {
            String nomeEmozione = tuple[0];
            String spiegazioneEmozione = tuple[1];
            emotionDataList.add(new TableUtil(nomeEmozione, spiegazioneEmozione));
        }
        TableUtil.defaultTableEmotions(emotionTable, categoryCol, explanationCol, scoreCol, notesCol, emotionDataList);
    }

    /**
     * Salva i punteggi delle emozioni ed eventuali commenti della rispettiva canzone
     * dopo aver premuto sul pulsante "Salva".
     * @param event L'evento generato dal click sul pulsante "Salva".
     */
    public void inserisciEmozioniBrano(ActionEvent event) throws IOException {
        ArrayList<Emozione> datiEmotion = new ArrayList<>();
        ObservableList<TableUtil> emotionDataTable= emotionTable.getItems();
        boolean checkEmotion = false;
        for (int i = 0; i < emotionDataTable.size(); i++) {
            if (emotionDataTable.get(i).getScore().getValue() != null) {
                String valoreComboBox = (String) emotionDataTable.get(i).getScore().getValue();  // Si ottiene il valore dalla ComboBox come stringa
                int punteggio = Integer.parseInt(valoreComboBox);
                datiEmotion.add(new Emozione(emotionDataTable.get(i).getEmotionalCategory(),
                        punteggio, emotionDataTable.get(i).getNotes().getText()));
                checkEmotion = true;
            }
        }
        if (checkEmotion) { //se almeno un'emozione è stata selezionata
            connectServer.richiestaInsertEmozione(datiEmotion);
            String messaggio = "Punteggio emozioni inserite con successo!";
            System.out.println(messaggio);
            boolean confermato = AlertDialogUtil.showSuccessDialog(messaggio);
            if (confermato) {
                mainController = new MainController();
                mainController.scenaListaEmozioni();
            }
        } else {
            String messaggio = "Non hai selezionato nessuna emozione!";
            AlertDialogUtil.showWarningDialog(messaggio);
            System.out.println(messaggio);
        }
    }

    /**
     * Resetta i punteggi ed eventuali commenti inseriti nella tabella
     * dopo aver premuto sul pulsante "Resetta tabella".
     * @param event L'evento generato dal click sul pulsante "Resetta tabella".
     */
    public void resetEmozione(ActionEvent event) {
        try {
            ArrayList<String[]> emozioniTable = connectServer.getEmotionsTable();
            inizializzaTable(emozioniTable);
            String messaggio = "Tabella emozioni resettata con successo!";
            System.out.println(messaggio);
            AlertDialogUtil.showSuccessDialog(messaggio);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rimuove i punteggi emozionali relativi alla canzone per l'utente corrente
     * dopo aver premuto sul pulsante "Rimuovi i miei punteggi".
     * @param actionEvent L'evento generato dal click sul pulsante "Rimuovi i miei punteggi".
     * @throws IOException Se si verifica un errore durante la comunicazione con il server
     * o durante il ricaricamento della scena.
     */
    public void rimuoviEmotions(ActionEvent actionEvent) throws IOException {
        String messaggio = "Sei sicuro di voler rimuovere i punteggi?";
        boolean confirmed = AlertDialogUtil.showConfirmationDialog(messaggio);
        if (confirmed) {
            boolean eliminazioneRiuscita = connectServer.checkRimuoviPunteggiEmozioni();
            if (eliminazioneRiuscita) {
                mainController = new MainController();
                mainController.scenaListaEmozioni();
            } else {
                String errore = "Errore durante l'eliminazione dei punteggi.";
                AlertDialogUtil.showErrorDialog(errore);
                System.out.println(errore);
            }
        }
    }

    /**
     * Torna alla scena {@code listaCanzoni.fxml} controllata da {@link ListaCanzoniController}
     * dopo aver premuto sul pulsante "<--".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void tornaCanzoni() throws IOException {
        mainController = new MainController();
        mainController.scenaListaCanzoni();
    }
}
