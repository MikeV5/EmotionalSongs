package emotionalsongs;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;
import client.ConnessioneServer;
import util.AlertDialogUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * La classe principale dell'applicazione Emotional Songs. Gestisce la creazione e l'avvio delle scene.
 * Inoltre, collega tutte le altre classi controller con il rispettivo fxml.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class MainController extends Application {
    /**
     * Crea la finestra principale dell'applicazione.
     * Lo stage e' responsabile per il contenimento e la visualizzazione delle diverse scene.
     */
    private static Stage stg;
    /**
     * L'oggetto per la connessione al server RMI.
     */
    private ConnessioneServer connessioneServer;

    /**
     * Avvia la scena principale dell'applicazione.
     * @param primaryStage Finestra principale dell'applicazione.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            stg = primaryStage;
            connessioneServer = ConnessioneServer.getIstanza();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginMenu.fxml")));
            Scene scene = new Scene(root, 450, 650);
            primaryStage.setTitle("Emotional Songs");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            aggiungiConfermaChiusura(primaryStage); //Conferma la chiusura
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un'istanza di MainController.
     */
    public MainController() {
    }

    /**
     * Conferma alla chiusura della finestra.
     * Quando l'utente cerca di chiudere la finestra, viene visualizzato
     * un dialogo di conferma. Se l'utente conferma la chiusura, la scena viene chiusa;
     * altrimenti, l'evento di chiusura viene consumato e la scena rimane aperta.
     * @param primaryStage La scena(finiestra) principale dell'applicazione.
     */
    public void aggiungiConfermaChiusura(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); //Impedisce la chiusura della finestra
            String messaggio = "Sei sicuro di voler uscire dal programma?";
            boolean confirmed = AlertDialogUtil.showConfirmationDialog(messaggio);
            if (confirmed) {
                StatsEmozioneBranoController.cancelTimer();
                try {
                    uscitaApplicazione();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                primaryStage.close(); //Chiude la finestra principale
            }
        });
    }

    /**
     * Carica la scena {@code configserver.fxml} controllata da {@link ConfigServerController}
     * e la imposta come scena attuale.
     * Carica e mostra una nuova scena con il layout specificato.
     * @param fxml Il percorso del file FXML per la nuova scena.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaConfigServer(String fxml) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.setWidth(400);
        stg.setHeight(400);
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code loginMenu.fxml} controllata da {@link LoginController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaMenuPrincipale() throws IOException {
        String fxml = "loginMenu.fxml";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        stg.setWidth(470);
        stg.setHeight(690);
        stg.getScene().setRoot(root);
    }
    /**
     * Carica la scena {@code registrazione.fxml} controllata da {@link RegistrazioneController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaRegistrazione() throws IOException {
        String fxml = "registrazione.fxml";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        stg.getScene().setRoot(root);
    }

    /**
     * Carica la scena {@code listaPlaylist.fxml} controllata da {@link ListaPlaylistController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaListaPlaylist() throws IOException {
        String fxml = "listaPlaylist.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code consultaCanzoni.fxml} controllata da {@link ConsultaCanzoniController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaConsultaCanzone() throws IOException {
        String fxml = "consultaCanzoni.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.setWidth(465);
        stg.setHeight(690);
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code nomePlaylist.fxml} controllata da{@link NomePlaylistController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaInsertPlaylist() throws IOException {
        String fxml = "nomePlaylist.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code nomeCanzone.fxml} controllata da {@link NomeCanzoneController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaInsertCanzone() throws IOException {
        String fxml = "nomeCanzone.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code listaCanzoni.fxml} controllata da {@link ListaCanzoniController}
     * e la imposta come scena attuale.
     * @throws IOException  Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaListaCanzoni() throws IOException {
        String fxml = "listaCanzoni.fxml";
        stg.setWidth(465);
        stg.setHeight(690);
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.getScene().setRoot(root);
        stg.show();
    }

    /**
     * Carica la scena {@code listaEmozioni.fxml} controllata da {@link  ListaEmozioniController}
     * e la imposta come scena attuale.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaListaEmozioni() throws IOException {
        String fxml = "listaEmozioni.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.setWidth(810);
        stg.setHeight(690);
        stg.getScene().setRoot(root);
    }
    /**
     * Carica la scena {@code statsEmozioneBrano.fxml} controllata da {@link StatsEmozioneBranoController}
     * e la imposta come scena attuale.
     * @param wt La larghezza desiderata per la finestra.
     * @param ht L'altezza desiderata per la finestra.
     * @throws IOException Se si verifica un errore durante il caricamento della scena.
     */
    public void scenaStatsBrano(int wt, int ht) throws IOException {
        String fxml = "statsEmozioneBrano.fxml";
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        root = loader.load();
        stg.setWidth(wt);
        stg.setHeight(ht);
        stg.getScene().setRoot(root);
    }

    /**
     * Gestisce l'uscita dall'applicazione.
     * Disconnette il client dal server e rimuove la sessione dell'utente, se presente.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public void uscitaApplicazione() throws RemoteException {
        if(connessioneServer.isLogged())
            connessioneServer.rimuoviSessione();
       connessioneServer.disconnetti();
    }
}
