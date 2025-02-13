package emotionalsongs;

import client.ConnessioneServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import util.AlertDialogUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe Controller per la configurazione del server EmotionalSongs.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ConfigServerController implements Initializable {
    /**
     * Campi di testo per l'inserimento dell'indirizzo IP e la porta del server.
     */
    @FXML
    private TextField ipTextField, portaTextField;
    /**
     * Oggetto di tipo {@link ConnessioneServer}, utilizzato per gestire la connessione al server.
     */
    private ConnessioneServer connectServer;
    /**
     * Indirizzo IP corrente del server.
     */
    private String ipCorrente;
    /**
     * Porta corrente del server.
     */
    private int portaCorrente;
    /**
     * Oggetto di tipo {@link MainController}, utilizzato per impostare
     * la scena che si vuole visualizzare.
     */
    private MainController mainController;

    /**
     * Metodo che inizializza il controller quando viene creato.
     * Inizializza i {@code TextField} con l'indirizzo IP e la porta correnti del server.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        ipCorrente = connectServer.getHostAddress();
        portaCorrente = connectServer.getPort();
        ipTextField.setText(ipCorrente);
        portaTextField.setText(Integer.toString(portaCorrente));
    }

    /**
     * Verifica sia l'indirizzo IP che la porta e tenta di stabilire una connessione con il server
     * dopo aver clickato sul pulsante "Connetti"
     * @param actionEvent L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante la comunicazione con il server.
     */
    public void connettiServer(ActionEvent actionEvent) throws IOException {
        String indirizzoIP = ipTextField.getText();
        String portaStr = portaTextField.getText();

        //Verifica se l'indirizzo IP è valido
        if (!isValidIPAddress(indirizzoIP) && !indirizzoIP.equals("localhost")) {
            AlertDialogUtil.showErrorDialog("Indirizzo IP non valido");
            return;
        }
        //Verifica se la porta è valida
        if (!isValidPort(portaStr)) {
            AlertDialogUtil.showErrorDialog("Porta non valida");
            return;
        }
        connectServer.setHostAddress(indirizzoIP);
        connectServer.setPort(Integer.parseInt(portaStr));
        mainController = new MainController();
        if(connectServer.testConnessione()) {
            System.out.println(indirizzoIP +" - "+portaStr+". Connessione al server riuscita.");
            mainController.scenaMenuPrincipale();
        }else AlertDialogUtil.showErrorDialog("Connessione al server fallita. Riprova!");
    }

    /**
     * Verifica se l'indirizzo IP e' valido.
     * @param ipAddress L'indirizzo IP da verificare.
     * @return True se l'indirizzo IP e' valido, altrimenti false.
     */
    private boolean isValidIPAddress(String ipAddress) {
        String ipPattern =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return ipAddress.matches(ipPattern);
    }

    /**
     * Verifica se la porta e' valida.
     * @param port La porta da verificare.
     * @return True se la porta e' valida, altrimenti false.
     */
    private boolean isValidPort(String port) {
        String portPattern = "^\\d{1,5}$"; //Solo numeri positivi fino a 5 cifre
        return port.matches(portPattern) && Integer.parseInt(port) >= 1 && Integer.parseInt(port) <= 65535;
    }

    /**
     * Ritorna alla scena principale {@code loginMenu.fxml} controllata da {@link LoginController}
     * quando l'utente preme il pulsante "Annulla".
     * @param actionEvent L'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica un errore durante il cambio di scena.
     */
    public void annullaConfigServer(ActionEvent actionEvent) throws IOException {
        MainController m = new MainController();
        m.scenaMenuPrincipale();
    }

    /**
     * Ripristina i campi di testo con l'indirizzo IP e la porta predefiniti del server
     * quando l'utente preme il pulsante "Ripristina impostazioni predefinite".
     * @param actionEvent L'evento generato dal click sul pulsante.
     */
    public void ripristinaImpostazioniPredefinite(ActionEvent actionEvent) {
        ipTextField.setText(connectServer.getDefaultHostAddress());
        portaTextField.setText(Integer.toString(connectServer.getDefaultPORT()));
    }
}
