package emotionalsongs;

import client.ConnessioneServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import util.AlertDialogUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Classe Controller per la scena di login.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class LoginController implements Initializable{
    /**
     * Label per visualizzare un messaggio di errore in caso di login non riuscito.
     */
    @FXML
    private Label wrongLoginLabel;
    /**
     * Campo di testo per inserire il nome utente durante il login.
     */
    @FXML
    private TextField userTextField;
    /**
     * Campo di testo per inserire la password durante il login.
     */
    @FXML
    private PasswordField passwordField;
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
     * Effettua la connessione al server e verifica se la connessione e' stata stabilita correttamente.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        connesso = connectServer.testConnessione();
    }

    /**
     * Verifica le credenziali dell'utente dopo aver clickato sul pulsante "Accedi"
     * @throws IOException Se si verifica un errore durante la comunicazione con il server
     * o durante il cambio di scena.
     */
    public void userLogin() throws IOException {
        String username = userTextField.getText();
        String password = passwordField.getText();
        boolean esitoLogin = connectServer.verificaLogin(username, password);
        //System.out.println(esitoLogin);
        mainController = new MainController();
        if (esitoLogin) {
            UUID sessionId = connectServer.sessionUser(username);
            if (sessionId != null) {
                System.out.println("Login effettuato con successo.");
                connectServer.setSessionID(sessionId);
                //System.out.println(sessionId.toString());
                mainController.scenaListaPlaylist();
            }
        } else {
            wrongLoginLabel.setText("Accesso non riuscito. Riprova.");
        }
    }

    /**
     * Verifica le credenziali dell'utente dopo aver premuto il tasto enter.
     * @param keyEvent Il tasto enter.
     * @throws IOException  Se si verifica un errore durante la comunicazione con il server
     * o durante il cambio di scena.
     */
    public void pressedEnter(KeyEvent keyEvent) throws IOException{
        if (keyEvent.getCode() == KeyCode.ENTER) {
            userLogin();
        }
    }

    /**
     * Mostra la scena per consultare il catalogo dei brani musicali
     * quando l'utente preme il pulsante "Consulta informazioni di canzoni".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il cambio di scena.
     */
    public void consultaCanzoni(ActionEvent event) throws IOException {
        connesso = connectServer.testConnessione();
        if(connesso) {
            mainController = new MainController();
            mainController.scenaConsultaCanzone();
        } else AlertDialogUtil.showErroreConnessioneDialog();
    }

    /**
     * Mostra la scena di registrazione quando l'utente preme il pulsante "Registrati".
     * @param event L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il cambio di scena.
     */
    public void userRegistrazione(ActionEvent event) throws IOException {
        connesso = connectServer.testConnessione();
        if(connesso) {
            mainController = new MainController();
            mainController.scenaRegistrazione();
        }else AlertDialogUtil.showErroreConnessioneDialog();
    }

    /**
     * Mostra la scena di configurazione server quando l'utente
     * preme il pulsante "Configurazione Server".
     * @param actionEvent L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il cambio di scena.
     */
    public void configServer(ActionEvent actionEvent) throws IOException {
        mainController = new MainController();
        mainController.scenaConfigServer("configserver.fxml");
    }
}
