package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestore delle canzoni utilizzato per gestire l'elenco delle canzoni nella
 * classe e comunicare con il server remoto.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class SongManager {
    private static SongManager istanza;
    private Map<Integer, Canzone> songsMap;
    private long ultimoTimeStampClient;

    /**
     * Costruttore privato che inizializza l'HashMap per l'elenco delle canzoni.
     */
    private SongManager() {
        //Inizializza l'HashMap quando viene creato il singleton
        songsMap = new HashMap<>();
    }

    /**
     * Ottiene l'istanza singola del SongManager(singleton).
     * @return L'istanza del SongManager.
     */
    public static synchronized SongManager getIstanza() {
        if (istanza == null) {
            istanza = new SongManager();
        }
        return istanza;
    }

    /**
     * Aggiunge una canzone all'elenco.
     * @param song La canzone da aggiungere.
     */
    public void addSong(Canzone song) {
        songsMap.put(song.getIdCanzone(), song);
    }

    /**
     * Ottiene una canzone dall'elenco in base all'ID.
     * @param id L'ID della canzone da cercare.
     * @return La canzone corrispondente all'ID, o null se non trovata.
     */
    public Canzone getSong(int id) {
        return songsMap.get(id);
    }

    /**
     * Ottiene tutte le canzoni dall'elenco.
     * @return Una collezione contenente tutte le canzoni.
     */
    public Collection<Canzone> getAllSongs() {
        return songsMap.values();
    }

    /**
     * Ottiene l'ultimo timestamp del client per le modifiche alle canzoni.
     * @return L'ultimo timestamp del client.
     */
    public long getUltimoTimeStampClient() {
        return ultimoTimeStampClient;
    }

    /**
     * Imposta l'ultimo timestamp del client per le modifiche alle canzoni.
     * @param ultimoTimeStampClient Il nuovo timestamp del client.
     */
    public void setUltimoTimeStampClient(long ultimoTimeStampClient) {
        this.ultimoTimeStampClient = ultimoTimeStampClient;
    }

    /**
     * Cancella tutte le canzoni dall'elenco.
     */
    public void clearTableSongs() {
        songsMap.clear();
    }
}
