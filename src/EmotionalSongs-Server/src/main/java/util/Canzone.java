package util;
import java.io.Serializable;

/**
 * Le istanze della classe {@code Canzone} contengono informazioni di base relative
 * a una canzone, come ID, nome, autore, data di rilascio e durata.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class Canzone implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * L'ID della canzone.
     */
    private int idCanzone;

    /**
     * Il nome della canzone.
     */
    private String nomeCanzone;

    /**
     * Il nome dell'autore della canzone.
     */
    private String nomeAutore;

    /**
     * La data di rilascio della canzone.
     */
    private int dataCanzone;

    /**
     * La durata della canzone in secondi rappresentata come tipo stringa.
     * Questo tipo e' utilizzato per consentire l'interazione con la tabella
     * (verrà poi convertito nel formato "MM:SS").
     */
    private String durataCanzone;

    /**
     * Costruttore della classe Canzone.
     * @param idCanzone L'ID della canzone.
     * @param nomeCanzone Il nome della canzone.
     * @param nomeAutore Il nome dell'autore della canzone.
     * @param dataCanzone La data di rilascio della canzone.
     * @param durataCanzone La durata della canzone nel formato "MM:SS".
     */
    public Canzone(int idCanzone, String nomeCanzone, String nomeAutore, int dataCanzone, String durataCanzone) {
        super();
        this.idCanzone = idCanzone;
        this.nomeCanzone = nomeCanzone;
        this.nomeAutore = nomeAutore;
        this.dataCanzone = dataCanzone;
        this.durataCanzone = durataCanzone;
    }

    /**
     * Restituisce l'ID della canzone.
     * @return L'ID della canzone.
     */
    public int getIdCanzone() {
        return idCanzone;
    }

    /**
     * Modifica l'ID della canzone con il valore specificato.
     * @param idCanzone La nuova ID della canzone.
     */
    public void setIdCanzone(int idCanzone) {
        this.idCanzone = idCanzone;
    }

    /**
     * Restituisce il nome della canzone.
     * @return Il nome della canzone.
     */
    public String getNomeCanzone() {
        return nomeCanzone;
    }

    /**
     * Modifica il nome della canzone con il valore specificato.
     * @param nomeCanzone Il nuovo nome della canzone.
     */
    public void setNomeCanzone(String nomeCanzone) {
        this.nomeCanzone = nomeCanzone;
    }

    /**
     * Restituisce il nome dell'autore della canzone.
     * @return Il nome dell'autore della canzone.
     */
    public String getNomeAutore() {
        return nomeAutore;
    }

    /**
     * Modifica il nome dell'autore della canzone con il valore specificato.
     * @param nomeAutore Il nuovo nome dell'autore della canzone.
     */
    public void setNomeAutore(String nomeAutore) {
        this.nomeAutore = nomeAutore;
    }

    /**
     * Restituisce la data di rilascio della canzone.
     * @return La data di rilascio della canzone.
     */
    public int getDataCanzone() {
        return dataCanzone;
    }

    /**
     * Modifica la data di rilascio della canzone con il valore specificato.
     * @param dataCanzone La nuova data di rilascio della canzone.
     */
    public void setDataCanzone(int dataCanzone) {
        this.dataCanzone = dataCanzone;
    }

    /**
     * Restituisce la durata della canzone nel formato "MM:SS".
     * @return La durata della canzone nel formato "MM:SS".
     */
    public String getDurataCanzone() {
        return durataCanzone;
    }

    /**
     * Modifica la durata della canzone nel formato "MM:SS" con il valore specificato.
     * @param durataCanzone La durata della canzone da impostare nel formato "MM:SS".
     */
    public void setDurataCanzone(String durataCanzone) {
        this.durataCanzone = durataCanzone;
    }

    /**
     * Converte la durata in secondi nel formato "MM:SS" per la visualizzazione
     * sulla tabella.
     * @param durata La durata in secondi da convertire nel formato "MM:SS".
     * @return Una stringa che rappresenta la durata nel formato "MM:SS".
     */
    public static String convertiAFormatMinuti(String durata) {
        int secondi = Integer.parseInt(durata);
        int minuti = secondi / 60;
        int secondiRestanti = secondi % 60;
        return String.format("%02d:%02d", minuti, secondiRestanti);
    }

    /**
     * Converte la durata della canzone dal formato "MM:SS" al numero totale di secondi.
     * @param durataFormato La durata della canzone nel formato "MM:SS".
     * @return Il numero totale di secondi corrispondente alla durata della canzone,
     * o -1 se il formato non e' valido.
     */
    public static int convertiInSecondi(String durataFormato) {
        try {
            String[] componenti = durataFormato.split(":");
            int minuti = Integer.parseInt(componenti[0]);
            int secondi = Integer.parseInt(componenti[1]);
            int totaleSecondi = (minuti * 60) + secondi;
            return totaleSecondi;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }
}
