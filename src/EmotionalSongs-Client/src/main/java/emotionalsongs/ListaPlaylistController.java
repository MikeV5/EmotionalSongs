package emotionalsongs;
import client.ConnessioneServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import util.AlertDialogUtil;
import util.Canzone;
import util.Playlist;
import util.TableUtil;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Classe Controller che mostra la scena con tutte le playlist create dall'utente.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ListaPlaylistController implements Initializable {
    /**
     * Label per visualizzare il nome dell'utente corrente.
     */
    @FXML
    private Label userLabel;
    /**
     * Tabella per visualizzare la lista delle playlist dell'utente corrente.
     */
    @FXML
    private TableView<Playlist> playlistTable;
    /**
     * Colonna per l'ID delle playlist.
     * Questa colonna e' invisibile agli utenti, ma utilizzata internamente per il recupero dei dati.
     */
    @FXML
    private TableColumn<Playlist, Integer> idPlaylistCol;
    /**
     * Colonna per visualizzare il nome delle playlist.
     */
    @FXML
    private TableColumn<Playlist, String> playlistCol;
    /**
     * Colonna per la rimozione delle playlist.
     */
    @FXML
    private TableColumn<Playlist, Void> eliminaCol;
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
     * Popola la tabella delle playlist associate all'utente corrente.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        userLabel.setText("User: "+ connectServer.getUserLoggato());
        ArrayList<Playlist> listaPlaylist = null;
        try {
            listaPlaylist = connectServer.getUserPlaylists();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (listaPlaylist != null) {
            ObservableList<Playlist> playlistTabella = FXCollections.observableArrayList(listaPlaylist);
            idPlaylistCol.setCellValueFactory(new PropertyValueFactory<Playlist, Integer>("idPlaylist")); //colonna invisibile
            playlistCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("nomePlaylist"));
            //Aggiunge la colonna per i pulsanti "Elimina playlist"
            TableUtil.setupTabellaEliminaCol(playlistTable, eliminaCol, "playlist", this::eliminaPlaylist);
            playlistTable.setItems(playlistTabella);
        }
    }

    /**
     * Metodo per eliminare una playlist.
     * @param playlist l'oggetto Playlist da eliminare.
     */
    private void eliminaPlaylist(Playlist playlist) {
        boolean eliminazioneRiuscita = false;
        try {
            eliminazioneRiuscita = connectServer.checkRimuoviPlaylist(playlist.getIdPlaylist()); // Rimuovi dal server
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (eliminazioneRiuscita) {
            System.out.println("Playlist eliminata.");
            AlertDialogUtil.showSuccessDialog("Playlist eliminata.");
        } else {
            String errore = "Errore durante l'eliminazione della playlist.";
            System.out.println(errore);
            AlertDialogUtil.showErrorDialog(errore);
        }
    }

    /**
     * Gestisce l'evento del doppio click su una playlist.
     * Quando viene selezionata una playlist, viene caricata la scena delle canzoni
     * associate a quella playlist.
     * @param event L'evento generato dal click.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void selezionaPlaylist(MouseEvent event) throws IOException{
        if (event.getClickCount() == 2 && playlistTable.getSelectionModel().getSelectedItem() != null) {
            Playlist playlistSelezionata = playlistTable.getSelectionModel().getSelectedItem();
            mainController = new MainController();
            connectServer.setPlaylistCorrente(playlistSelezionata);
            mainController.scenaListaCanzoni();
        }
    }

    /**
     * Mostra la scena per inserire il nome della nuova playlist
     * quando l'utente preme il pulsante "Crea playlist".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void aggiungiPlaylist(ActionEvent event) throws IOException {
        mainController = new MainController();
        mainController.scenaInsertPlaylist();
    }

    /**
     *
     * Mostra la scena per consultare il catalogo dei brani musicali
     * quando l'utente preme il pulsante "Consulta canzoni".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void consultaCanzoni(ActionEvent event) throws IOException {
        mainController = new MainController();
        mainController.scenaConsultaCanzone();
    }

    /**
     * Torna alla schermata principale di login dopo aver premuto il pulsante "Log out".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void userLogout(ActionEvent event) throws IOException {
        connectServer.rimuoviSessione();
        connectServer.disconnetti();
        mainController = new MainController();
        mainController.scenaMenuPrincipale();
    }
}
