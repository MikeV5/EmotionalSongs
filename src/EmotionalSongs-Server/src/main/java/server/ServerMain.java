package server;
import interfacce.EmotSongsInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * La classe {@code ServerMain} rappresenta il punto di ingresso principale del server RMI.
 * Quando il server e' avviato, crea un'istanza dell'implementazione dell'interfaccia {@link EmotSongsInterface}
 * e la registra nel registro RMI. Questo permette ai client di ottenere lo stub dell'oggetto remoto,
 * consentendo loro di inviare richieste al server e ricevere risposte tramite RMI.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class ServerMain {
    /**
     * La porta di default utilizzata per il registro RMI.
     */
    private static final int defaultPORT = 1099;
    /**
     * Crea un'istanza dell'implementazione dell'interfaccia {@code EmotSongsInterface}
     * e la registra nel RMI.
     * @param args gli argomenti passati (non richiesti).
     * @throws RemoteException se si verifica un errore RMI durante la creazione del registro o la registrazione
     * dell'oggetto remoto.
     * @throws SQLException Se si verifica un errore durante la connessione al database.
     */
    public static void main(String[] args) throws RemoteException, SQLException {
        System.out.println( "Hello from Server!" );
        EmotSongsInterface obj = new ServerImplementation();
        Registry registry = LocateRegistry.createRegistry(defaultPORT);
        registry.rebind("EmotSongsInterface", obj);
        System.err.println("Server ready");
    }
}
