package emotionalsongs;

import client.ConnessioneServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import util.AlertDialogUtil;
import util.Utente;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Classe Controller per la finestra di registrazione utente.
 * Questa classe gestisce l'interazione dell'utente con il form di registrazione e la validazione dei dati inseriti.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class RegistrazioneController implements Initializable{
    /**
     * Campi di testo per la registrazione utente.
     */
    @FXML
    private TextField nomeField, cognomeField,
            codFiscaleField, indirizzoField, emailField, userIDField;
    /**
     * Campi di password per la registrazione utente.
     */
    @FXML
    private PasswordField passwordField, confPasswordField;

    /**
     * Gruppo di etichette di errore per ogni campo del form.
     */
    @FXML
    private Label wrongUserIDLabel, wrongPassLabel, wrongConfPassLabel, wrongNomeLabel,
            wrongCognomeLabel, wrongEmailLabel, wrongCodFiscLabel, wrongIndLabel;
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
     * Pattern per la validazione di un indirizzo email.
     * L'indirizzo email deve seguire il formato standard e includere un nome utente, una @ e un dominio.
     */
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
                    "@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    /**
     * Pattern per la validazione del codice fiscale.
     * Il codice fiscale deve essere composto esclusivamente da caratteri alfanumerici.
     */
    private static final String CODFISC_PATTERN = "^[a-zA-Z0-9]*$";
    /**
     * Interfaccia remota per la connessione al servizio EmotSongs.
     * Rappresenta l'interfaccia tramite la quale il controller interagisce con il servizio remoto
     * per eseguire operazioni riguardanti la registrazione utente e altre funzionalita'.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
    }

    /**
     * Gestisce la registrazione di un nuovo utente.
     * Verifica i dati inseriti nel form di registrazione.
     * @param event L'evento click del mouse
     * @throws IOException Se si verifica qualche errore durante la registrazione.
     */
    public void registrazione(ActionEvent event) throws IOException {
        String[] datiU = getInputData();
        ArrayList<String> campiErrati = verificaDatiInseriti(datiU);
        if (campiErrati.isEmpty()) {
            Utente nuovoUtente = Utente.creaUtenteFromData(datiU);
            connectServer.richiestaInsertUtente(nuovoUtente);
            gestisciSuccesso();
        } else {
            gestisciErrori(campiErrati);
        }
    }

    /**
     * Controlla la lunghezza del campo "Codice Fiscale" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthCodFisc(KeyEvent keyEvent) {
        limiteCaratteri(codFiscaleField, 16);
    }

    /**
     * Controlla la lunghezza del campo "Nome" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthNome(KeyEvent keyEvent) {
        limiteCaratteri(nomeField, 30);
    }

    /**
     * Controlla la lunghezza del campo "Cognome" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthCognome(KeyEvent keyEvent) {
        limiteCaratteri(cognomeField, 30);
    }

    /**
     * Controlla la lunghezza del campo "Indirizzo" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthIndirizzo(KeyEvent keyEvent) {
        limiteCaratteri(indirizzoField, 100);
    }

    /**
     * Controlla la lunghezza del campo "E-mail" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthEmail(KeyEvent keyEvent) {
        limiteCaratteri(emailField, 100);
    }

    /**
     * Controlla la lunghezza del campo "UserID" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthUserID(KeyEvent keyEvent) {
        limiteCaratteri(userIDField, 20);
    }

    /**
     * Controlla la lunghezza del campo "Password" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthPassword(KeyEvent keyEvent) {
        limiteCaratteri(passwordField, 60);
    }

    /**
     * Controlla la lunghezza del campo "Conferma Password" e limita il numero di caratteri al valore specificato.
     * @param keyEvent L'evento generato quando viene premuto un tasto.
     */
    public void maxLengthConfPassword(KeyEvent keyEvent) {
        limiteCaratteri(confPasswordField, 60);
    }

    /**
     * Limita la lunghezza di un campo di input.
     * @param tf Il campo di input (TextField) da limitare.
     * @param lunghezzaMassima La lunghezza massima consentita.
     */
    public void limiteCaratteri(final TextField tf, final int lunghezzaMassima) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > lunghezzaMassima) {
                String s = tf.getText().substring(0, lunghezzaMassima);
                tf.setText(s);
            }
        });
    }

    /**
     * Recupera le stringhe dai campi di input del form e li restituisce come un array
     * @return Un array di stringhe contenente i dati inseriti dall'utente nel form di registrazione.
     */
    private String[] getInputData() {
        String nomeUt = nomeField.getText();
        String cognomeUt = cognomeField.getText();
        String codFisc = codFiscaleField.getText();
        String indirizzoUt = indirizzoField.getText();
        String emailUt = emailField.getText();
        String userid = userIDField.getText();
        String pass = passwordField.getText();
        String confP = confPasswordField.getText();
        return new String[]{nomeUt, cognomeUt, codFisc, indirizzoUt, emailUt, userid, pass, confP};
    }

    /**
     * Verifica la validita' dei dati inseriti nel form di registrazione.
     * Imposta i messaggi di errore per i campi non validi.
     * @param datiU Un array di stringhe contenente i dati inseriti nel form.
     * @return Una lista contenente i nomi dei campi non validi.
     * @throws RemoteException se si verifica un errore durante la comunicazione con il server
     */
    private ArrayList<String> verificaDatiInseriti(String[] datiU) throws RemoteException {
        ArrayList<Label> listaLabel = new ArrayList<>();
        listaLabel.add(wrongNomeLabel);
        listaLabel.add(wrongCognomeLabel);
        listaLabel.add(wrongCodFiscLabel);
        listaLabel.add(wrongIndLabel);
        listaLabel.add(wrongEmailLabel);
        listaLabel.add(wrongUserIDLabel);
        listaLabel.add(wrongPassLabel);
        listaLabel.add(wrongConfPassLabel);
        ArrayList<String> campiErrati = new ArrayList<>();
        String[] campiForm = {"Nome", "Cognome", "Codice fiscale", "Indirizzo fisico",
                "Email", "UserID", "Password", "Password di conferma"};
        boolean[] checkCampo = checkCampi(datiU);
        boolean[] scritte = {false, false, false, false, true, false, true, true};
        for (int i = 0; i < datiU.length; i++) {
            if (!checkCampo[i]) {
                String genere = scritte[i] ? " non valida" : " non valido";
                listaLabel.get(i).setText(campiForm[i] + genere + ". Riprova.");
                // Aggiungi il testo "min 8 caratteri" al label per il campo "Password" (indice 6)
                if (i == 6) {
                    listaLabel.get(i).setText(listaLabel.get(i).getText() + " Min 8 caratteri.");
                }
                campiErrati.add(campiForm[i]);
            } else {
                listaLabel.get(i).setText("");
            }
        }
        return campiErrati;
    }

    /**
     * Verifica se l'userID e' disponibile e formatto correttamente.
     * @param user L'userID del nuovo utente
     * @return True se l'userID e' disponibile e
     * rispetta le condizioni di un userID, altrimenti false
     * @throws RemoteException se si verifica un errore durante la comunicazione con il server.
     */
    private boolean isValidUser(String user) throws RemoteException {
        boolean checkUserID = connectServer.verificaUserID(user);
        return (checkUserID && checkFormat(user));
    }

    /**
     * Verifica la lunghezza minima della password.
     * @param pass La password da verificare.
     * @return True se la password e' valida, false altrimenti.
     */
    private boolean isValidPassword(String pass) {
        return pass.trim().length() >= 8;
    }

    /**
     * Verifica che la password e quella di conferma coincidano.
     * @param pass La password scelta dall'utente.
     * @param confPass La conferma della password scelta dall'utente.
     * @return True se le password coincidono, false altrimenti
     */
    private boolean isValidConfermaPass(String pass, String confPass) {
        return (confPass.equals(pass) && checkFormat(confPass));
    }

    /**
     * Verifica se l'indirizzo email e' valido.
     * @param email L'indirizzo email da verificare.
     * @return True se l'indirizzo email e' valido, false altrimenti.
     */
    private boolean isValidEmail(String email) {
        return (email.trim().matches(EMAIL_PATTERN));
    }

    /**
     * Verifica se il codice fiscale e' formattato correttamente e ha il numero di caratteri necessari.
     * @param codFisc Il codice fiscale da verificare.
     * @return True rispetta le condizioni di un codice fiscale, altrimenti false.
     */
    private boolean isValidCodFisc(String codFisc) {
        return (codFisc.trim().length() == 16 && codFisc.matches(CODFISC_PATTERN));
    }

    /**
     * Verifica se il campo dato e' nel formato corretto (non nullo e con almeno un carattere).
     * @param campo Il campo da verificare.
     * @return True se formattato correttamente, false altrimenti.
     */
    private boolean checkFormat(String campo) {
        return (campo != null && campo.trim().length() > 0);
    }

    /**
     * Verifica tutti i campi del form di registrazione e restituisce un array di boolean che indica se i campi
     * sono corretti o meno.
     * @param campo Un array contenente i campi inseriti dall'utente.
     * @return L'array di boolean che indica per ogni campo se e' corretto o meno.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server
     */
    public boolean[] checkCampi(String[] campo) throws RemoteException {
        boolean[] boolDato = new boolean[8];
        String pass = campo[campo.length - 2];
        String confPass = campo[campo.length - 1];
        for (int i = 0; i < boolDato.length; i++) {
            switch (i) {
                case 0, 1, 3 -> boolDato[i] = checkFormat(campo[i]); //nome - cognome - indirizzo
                case 2 -> boolDato[i] = isValidCodFisc(campo[i]);
                case 4 -> boolDato[i] = isValidEmail(campo[i]);
                case 5 -> boolDato[i] = isValidUser(campo[i]);
                case 6 -> boolDato[i] = isValidPassword(campo[i]);
                case 7 -> boolDato[i] = isValidConfermaPass(pass, confPass);
            }
        }
        return boolDato;
    }

    /**
     * Gestisce l'azione in caso di successo della registrazione, mostrando un messaggio di conferma e
     * reindirizzando l'utente alla scena principale {@link LoginController}.
     * @throws IOException Se si verifica un errore durante il cambio di scena.
     */
    private void gestisciSuccesso() throws IOException {
        String messaggio = "Registrazione avvenuta con successo!";
        boolean confermato = AlertDialogUtil.showSuccessDialog(messaggio);
        if (confermato) {
            mainController = new MainController();
            System.out.println(messaggio);
            mainController.scenaMenuPrincipale();
        }
    }

    /**
     * Gestisce l'azione in caso di errori nella registrazione, mostrando un messaggio di errore con i campi errati.
     * @param campiErrati Un ArrayList di stringhe contenente i nomi dei campi con dati errati.
     */
    private void gestisciErrori(ArrayList<String> campiErrati) {
        String errorMessage = "I seguenti campi non sono stati inseriti correttamente:\n";
        for (String campo : campiErrati) {
            errorMessage += "- " + campo + "\n";
        }
        AlertDialogUtil.showErrorDialog(errorMessage);
    }

    /**
     * Ritorna alla scena principale {@code loginMenu.fxml} controllata da {@link LoginController}.
     * @param event l'evento generato dal click sul pulsante.
     * @throws IOException Se si verifica qualche errore durante il cambio di scena.
     */
    public void tornaLogin(ActionEvent event) throws IOException {
        mainController = new MainController();
        mainController.scenaMenuPrincipale();
    }
}
