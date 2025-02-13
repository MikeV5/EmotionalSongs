package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Classe per la gestione delle finestre di dialogo(alert).
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class AlertDialogUtil {

    /**
     * Mostra una finestra di dialogo di conferma con il messaggio specificato.
     * @param messaggio Il messaggio da visualizzare nella finestra di dialogo.
     * @return True se l'utente ha premuto il pulsante "ok", altrimenti false.
     */
    public static boolean showConfirmationDialog(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma");
        alert.setHeaderText(messaggio);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Mostra una finestra di dialogo di successo con il messaggio specificato.
     * @param messaggio Il messaggio da visualizzare nella finestra di dialogo.
     * @return True se l'utente ha premuto il pulsante "ok", altrimenti false.
     */
    public static boolean showSuccessDialog(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione completata");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Mostra una finestra di dialogo di errore con il messaggio specificato.
     * @param messaggio Il messaggio da visualizzare nella finestra di dialogo.
     */
    public static void showErrorDialog(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Mostra una finestra di dialogo di avviso con il messaggio specificato.
     * @param messaggio Il messaggio da visualizzare nella finestra di dialogo.
     */
    public static void showWarningDialog(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avviso");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Mostra una finestra di dialogo di errore di connessione al server.
     */
    public static void showErroreConnessioneDialog(){
        AlertDialogUtil.showErrorDialog("""
                    Errore di connessione.
                    Impossibile stabilire una connessione al server.
                    Verifica la connessione di rete e riprova.""");
    }

    /**
     * Mostra una finestra di dialogo d'informazione per l'aggiornamento delle canzoni.
     */
    public static void showReloadTabellaDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ricaricamento canzoni");
        alert.setHeaderText("Aggiornamento canzoni");
        alert.setContentText("La tabella delle canzoni e' stata aggiornata.");
        alert.show();
    }
}
