package emotionalsongs;
import client.ConnessioneServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import util.*;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Classe Controller che mostra la scena con la lista delle canzoni
 * associate alla playlist.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ListaCanzoniController implements Initializable {
    /**
     * Label per visualizzare il nome della playlist corrente,
     * il nome dell'utente loggato e gli errori relativi alle canzoni.
     */
    @FXML
    private Label labelPlaylist, userLabel, wrongCanzone;
    /**
     * Tabella per visualizzare elenco delle canzoni per la playlist corrente.
     */
    @FXML
    private TableView<Canzone> canzoniTable;
    /**
     * Colonna della tabella per visualizzare gli ID delle canzoni.
     * Questa colonna e' invisibile agli utenti, ma utilizzata internamente per il recupero dei dati.
     */
    @FXML
    private TableColumn<Canzone, Integer> idCol, annoCol;
    /**
     * Colonne della tabella per visualizzare rispettivamente
     * i titoli, gli autori, gli anni d'uscita e la durata delle canzoni.
     */
    @FXML
    private TableColumn<Canzone, String> titoloCol, autoreCol, durataCol;
    /**
     * Colonna per la rimozione delle playlist.
     */
    @FXML
    private TableColumn<Canzone, Void> eliminaCol;
    /**
     * La playlist corrente selezionata precedentemente.
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
     * Metodo che inizializza il controller quando viene creato.
     * Popola la tabella delle canzoni per la playlist selezionata precedentemente.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        userLabel.setText("User: "+ connectServer.getUserLoggato());
        playlistCorrente = connectServer.getPlaylistCorrente();
        labelPlaylist.setText(playlistCorrente.getNomePlaylist());
        ArrayList<Canzone> listaCanzoni = null;
        try {
            listaCanzoni = connectServer.getCanzoniPlaylist();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (listaCanzoni != null) {
            ObservableList<Canzone> canzoniTabella = FXCollections.observableArrayList(listaCanzoni);
            TableUtil.defaultTableSongs(idCol, titoloCol, autoreCol, annoCol,durataCol);
            //Aggiunge la colonna per i pulsanti "Elimina canzone"
            TableUtil.setupTabellaEliminaCol(canzoniTable, eliminaCol, "canzone", this::eliminaCanzoneDaPlaylist);
            canzoniTable.setItems(canzoniTabella);
        }
    }

    /**
     * Metodo per eliminare una canzone dalla playlist corrente.
     * @param canzone l'oggetto Canzone da eliminare.
     */
    private void eliminaCanzoneDaPlaylist(Canzone canzone) {
        boolean eliminazioneRiuscita = false;
        try {
            eliminazioneRiuscita = connectServer.checkRimuoviCanzoneDaPlaylist(canzone.getIdCanzone()); //Rimuove dal server
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if(eliminazioneRiuscita) {
            System.out.println("Canzone eliminata dalla playlist.");
            AlertDialogUtil.showSuccessDialog("Canzone eliminata dalla playlist.");
        } else {
            String errore = "Errore durante l'eliminazione della canzone dalla playlist.";
            System.out.println(errore);
            AlertDialogUtil.showErrorDialog(errore);
        }
    }

    /**
     * Gestisce l'evento del doppio click su una canzone.
     * Quando viene selezionata una canzone, viene caricata la scena
     * con la tabella delle emozioni ai quali inserire il punteggio.
     * @param event L'evento generato dal click.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void selezionaCanzoneByClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2 && canzoniTable.getSelectionModel().getSelectedItem() != null) {
            Canzone canzoneSelezionata = canzoniTable.getSelectionModel().getSelectedItem();
            connectServer.setCanzoneCorrente(canzoneSelezionata);
            ConsultaCanzoniController.visualizzaEmozioneBrano(connectServer);
        }
    }

    /**
     * Dopo aver selezionato una canzone avvia la scena
     * con la tabella delle emozioni ai quali inserire il punteggio.
     * @param event L'evento generato sul pulsante "Inserisci emozioni".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void selezionaCanzoneByButton(ActionEvent event) throws IOException {
        if (canzoniTable.getSelectionModel().getSelectedItem() != null) {
            mainController = new MainController();
            Canzone canzoneSelezionata = canzoniTable.getSelectionModel().getSelectedItem();
            connectServer.setCanzoneCorrente(canzoneSelezionata);
            mainController.scenaListaEmozioni();
        }else {
            wrongCanzone.setText("Devi selezionare una canzone!");
        }
    }

    /**
     * Dopo aver selezionato una canzone, avvia la scena che mostra le statistiche
     * delle emozioni associate a quella canzone.
     * @param actionEvent L'evento generato sul pulsante "Visualizza emozioni".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void visualizzaMyEmotions(ActionEvent actionEvent) throws IOException {
        if (canzoniTable.getSelectionModel().getSelectedItem() != null) {
            Canzone canzoneSelezionata = canzoniTable.getSelectionModel().getSelectedItem();
            connectServer.setCanzoneCorrente(canzoneSelezionata);
            ConsultaCanzoniController.visualizzaEmozioneBrano(connectServer);
        }else {
            wrongCanzone.setText("Devi selezionare una canzone!");
        }
    }

    /**
     * Mostra la scena per aggiungere una nuova canzone alla playlist.
     * quando l'utente preme il pulsante "Aggiungi brani".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void aggiungiCanzone(ActionEvent event) throws IOException {
        mainController = new MainController();
        mainController.scenaInsertCanzone();
    }

    /**
     * Torna alla scena delle playlist dopo aver premuto sul pulsante "<--".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void tornaPlaylist(ActionEvent event) throws IOException {
        connectServer.setPlaylistCorrente(null);
        mainController = new MainController();
        mainController.scenaListaPlaylist();
    }
}
