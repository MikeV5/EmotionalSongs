package util;

import java.io.Serializable;
/**
 * Le istanze della classe Emozione contengono informazioni relative
 * alle emozioni percepite dagli utenti.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class Emozione implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * La categoria dell'emozione (9 in totale)
     */
    private String emotionalCategory;
    /**
     * Il punteggio associato all'emozione(dall'1 al 5).
     */
    private int punteggio;
    /**
     * Rappresenta le eventuale note aggiuntive sull'emozione.
     */
    private String notes;

    /**
     * Array contenente la lista delle emozioni disponibili.
     */
    private static final String[] listaEmozioni = {
            "Amazement", "Solemnity", "Tenderness", "Nostalgia", "Calmness", "Power", "Joy", "Tension", "Sadness"
    };

    /**
     * Restituisce la lista delle emozioni disponibili.
     * @return L'array contenente la lista delle emozioni.
     */
    public static String[] getListaEmozioni() {
        return listaEmozioni;
    }

    /**
     * Costruttore della classe Emozione.
     * @param emotionalCategory La categoria emotiva dell'emozione
     * @param punteggio Il punteggio associato all'emozione
     * @param notes Le note aggiuntive sull'emozione
     */
    public Emozione(String emotionalCategory, int punteggio, String notes) {
        this.emotionalCategory = emotionalCategory;
        this.punteggio = punteggio;
        this.notes = notes;
    }

    /**
     * Restituisce la categoria dell'emozione.
     * @return la categoria dell'emozione.
     */
    public String getEmotionalCategory() {
        return emotionalCategory;
    }

    /**
     * Modifica la categoria dell'emozione con il valore specificato.
     * @param emotionalCategory La nuova categoria dell'emozione.
     */
    public void setEmotionalCategory(String emotionalCategory) {
        this.emotionalCategory = emotionalCategory;
    }

    /**
     * Restituisce il punteggio associato all'emozione.
     * @return Il punteggio associato all'emozione.
     */
    public int getPunteggio() {
        return punteggio;
    }

    /**
     * Modifica il punteggio associato all'emozione con il valore specificato.
     * @param punteggio Il nuovo punteggio dell'emozione.
     */
    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    /**
     * Restituisce le note aggiuntive sull'emozione.
     * @return Le note aggiuntive sull'emozione.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Modifica le note aggiuntive sull'emozione con il valore specificato.
     * @param notes Le nuove note aggiuntive sull'emozione.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
