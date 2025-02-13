package emotionalsongs;

import client.ConnessioneServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.AlertDialogUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe Controller che permette creare una nuova playlist.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class NomePlaylistController implements  Initializable{
    /**
     * Label per visualizzare il nome dell'utente corrente ed eventuali errori.
     */
    @FXML
    private Label userLabel, erroreLabel;
    /**
     * Campo di testo per scrivere il nome della nuova playlist.
     */
    @FXML
    private TextField playlistTextField;
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
     * Metodo che inizializza il controller quando viene creato.
     * Effettua il collegamento con il server e visualizza il nome utente loggato.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        userLabel.setText("User: "+ connectServer.getUserLoggato());
    }

    /**
     * Salva il nome della nuova playlist e ritorna alla scena {@code listaPlatlist.fxml}
     * controllata da {@link ListaPlaylistController}.
     * @param event L'evento generato dal click sul pulsante "Salva".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void registraPlaylist(ActionEvent event) throws IOException{
        mainController = new MainController();
        String nomePlaylist = playlistTextField.getText();
        boolean checkVuoto = nomePlaylist.isEmpty();
        if(!checkVuoto) {
            connectServer.richiestaInsertPlaylist(nomePlaylist);
            mainController.scenaListaPlaylist();
        }
        else erroreLabel.setText("Inserisci un nome alla playlist.");
    }

    /**
     * Annulla la creazione di una playlist e ritorna alla scena {@code listaPlatlist.fxml} controllata da {@link ListaPlaylistController}.
     * @param event L'evento generato dal click sul pulsante "Annulla".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void annullaNamePlaylist(ActionEvent event) throws IOException {
        connesso = connectServer.testConnessione();
        if(connesso) {
            mainController = new MainController();
            mainController.scenaListaPlaylist();
        }else AlertDialogUtil.showErroreConnessioneDialog();
    }
}
