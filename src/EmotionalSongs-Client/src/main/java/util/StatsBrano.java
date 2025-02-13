package util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che contiene le statistiche delle emozioni dei brani
 * per costruire la tabella riassuntiva.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class StatsBrano {
    /**
     * Il nome dell'emozione.
     */
    private String nomeEmozione;
    /**
     * Il punteggio medio (numero utenti / punteggio totale).
     */
    private double media;
    /**
     * Il numero di utenti che hanno lasciato un punteggio per l'emozione.
     */
    private int numUtenti;
    /**
     * Area degli eventuali commenti.
     */
    private TextArea commenti;

    /**
     * Costruisce un'istanza della classe StatsBrano, ricevendo come argomenti:
     * nome dell'emozione, punteggio medio, numero di utenti e commenti.
     * @param nomeEmozione Il nome dell'emozione.
     * @param media Il punteggio medio.
     * @param numUtenti Il numero di utenti che percepiscono quell'emozione.
     * @param commenti Gli eventuali commenti lasciati dagli utenti.
     */
    public StatsBrano(String nomeEmozione, double media, int numUtenti, String commenti) {
        super();
        this.nomeEmozione = nomeEmozione;
        this.media = media;
        this.numUtenti = numUtenti;
        this.commenti = new TextArea(commenti);
    }

    /**
     * Restituisce il nome dell'istanza StatsBrano.
     * @return Il nome dell'emozione.
     */
    public String getNomeEmozione() {
        return nomeEmozione;
    }

    /**
     * Modifica il nome dell'istanza StatsBrano.
     * @param nomeEmozione Il nuovo nome dell'emozione.
     */
    public void setNomeEmozione(String nomeEmozione) {
        this.nomeEmozione = nomeEmozione;
    }

    /**
     * Restituisce il punteggio medio dell'istanza StatsBrano.
     * @return Il punteggio medio dell'emozione.
     */
    public double getMedia() {
        return media;
    }

    /**
     * Modifica il punteggio medio dell'istanza dell'emozione.
     * @param media Il nuovo punteggio medio dell'istanza dell'emozione.
     */
    public void setMedia(double media) {
        this.media = media;
    }

    /**
     * Restituisce il numero di utenti dell'istanza StatsBrano.
     * @return Il numero utenti dell'emozione.
     */
    public int getNumUtenti() {
        return numUtenti;
    }

    /**
     * Modifica il numero utenti dell'istanza StatsBrano.
     * @param numUtenti Il nuovo numero utenti dell'emozione.
     */
    public void setNumUtenti(int numUtenti) {
        this.numUtenti = numUtenti;
    }

    /**
     * Restituisce gli eventuali commenti dell'istanza StatsBrano
     * @return Gli eventuali commenti lasciati dagli utenti
     */
    public TextArea getCommenti() {
        return commenti;
    }

    /**
     * Modifica i commenti dell'istanza StatsBrano.
     * @param commenti I nuovi commenti dell'emozione.
     */
    public void setCommenti(TextArea commenti) {
        this.commenti = commenti;
    }

    /**
     * Ridimensiona il label delle emozioni in base al numero di emozioni valutate per l'utente corrente.
     * @param punteggioEmozioniPerUtente Un vettore di interi contenente il punteggio delle emozioni per l'utente corrente.
     */
    public static void resizeEmotionsLabel(Label myEmotionsLabel, Button rimuoviEmotionsButton, Label scrittaEmotionsLabel,
                                           ScrollPane scrollPaneEmotions, String username, Label infoMyEmotionsLabel,
                                           int[] punteggioEmozioniPerUtente) {
        String myEmotions = getEmozioniConPunteggioNonZero(punteggioEmozioniPerUtente);
        int countMyEmotions = countEmozioniConPunteggioNonZero(punteggioEmozioniPerUtente);
        if(countMyEmotions!=0){
            int emotionsInEccesso;
            if (countMyEmotions > 3) {
                emotionsInEccesso = countMyEmotions - 3;
            } else {
                emotionsInEccesso = 0;
            }
            double altezzaLabelEmotions = myEmotionsLabel.getPrefHeight();
            double altezzaAggiuntiva = emotionsInEccesso * 20;
            myEmotionsLabel.setPrefHeight(altezzaLabelEmotions + altezzaAggiuntiva);
            myEmotionsLabel.setText(myEmotions);
            infoMyEmotionsLabel.setVisible(false);
        }else {
            myEmotionsLabel.setVisible(false);
            rimuoviEmotionsButton.setVisible(false);
            scrittaEmotionsLabel.setVisible(false);
            scrollPaneEmotions.setVisible(false);
            if(username != null)
                infoMyEmotionsLabel.setText("Non hai ancora valutato questa canzone.");
            else infoMyEmotionsLabel.setVisible(false);
        }
    }

    /**
     * Restituisce una stringa contenente le emozioni con punteggio non zero.
     * @param punteggioEmozioniPerUtente Un vettore di interi contenente il punteggio delle emozioni per l'utente corrente.
     * @return Una stringa contenente le emozioni e i relativi punteggi non nulli dell'utente.
     */
    public static String getEmozioniConPunteggioNonZero(int[] punteggioEmozioniPerUtente) {
        List<String> emozioniNonNulle = new ArrayList<>();
        String[] listaEmozioni = Emozione.getListaEmozioni();
        for (int i = 0; i < listaEmozioni.length; i++) {
            if (punteggioEmozioniPerUtente[i] != 0) {
                emozioniNonNulle.add(listaEmozioni[i] + ": " + punteggioEmozioniPerUtente[i]);
            }
        }
        return String.join("\n", emozioniNonNulle);
    }

    /**
     * Conta il numero di emozioni con punteggio non zero per l'utente.
     * @param punteggioEmozioniPerUtente Un vettore di interi contenente il punteggio delle emozioni per l'utente corrente.
     * @return Il numero di emozioni con punteggio non zero per l'utente.
     */
    public static int countEmozioniConPunteggioNonZero(int[] punteggioEmozioniPerUtente) {
        int count = 0;
        for (int score : punteggioEmozioniPerUtente) {
            if (score != 0) {
                count++;
            }
        }
        return count;
    }
}
