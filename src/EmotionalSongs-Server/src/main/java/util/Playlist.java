package util;
import java.io.Serializable;

/**
 * Le istanze della classe {@code Playlist} contengono informazioni
 * relative alle playlist create dagli utenti.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * L'ID della playlist.
     */
    private int idPlaylist;
    /**
     * Il nome della playlist.
     */
    private String nomePlaylist;

    /**
     * Costruttore della classe Playlist.
     * @param idPlaylist l'ID della playlist.
     * @param nomePlaylist Il nome della playlist.
     */
    public Playlist(int idPlaylist, String nomePlaylist) {
        super();
        this.idPlaylist= idPlaylist;
        this.nomePlaylist = nomePlaylist;
    }

    /**
     * Restituisce l'ID della playlist.
     * @return l'ID della playlist
     */
    public int getIdPlaylist() {
        return idPlaylist;
    }
    /**
     * Modifica l'ID della playlist con il valore specificato.
     * @param idPlaylist Il nuovo 'ID della playlist.
     */
    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    /**
     * Restituisce il nome dell'istanza Playlist
     * @return Il nome della playlist
     */
    public String getNomePlaylist() {
        return nomePlaylist;
    }

    /**
     * Modifica il nome della playlist con il valore specificato.
     * @param nomePlaylist Il nuovo nome della playlist.
     */
    public void setNomePlaylist(String nomePlaylist) {
        this.nomePlaylist = nomePlaylist;
    }
}
