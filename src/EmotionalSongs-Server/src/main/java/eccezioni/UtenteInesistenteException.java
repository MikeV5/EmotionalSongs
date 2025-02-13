package eccezioni;
/**
 * Eccezione che rappresenta il caso in cui un utente non esiste nel database.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class UtenteInesistenteException extends Exception {
    private static final long serialVersionUID = 1L;
    /**
     * Costruttore della classe.
     * @param messaggio Il messaggio di errore specifico relativo all'utente non esistente.
     */
    public UtenteInesistenteException(String messaggio) {
        super(messaggio);
    }
}
