package util;
import java.io.Serializable;

/**
 * Le istanze della classe {@code Utente} contengono informazioni relative alle persone specificate.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * L'userID dell'utente.
     */
    private String userid;
    /**
     * Las password del'utente.
     */
    private String password;
    /**
     * Il nome dell'utente.
     */
    private String nome;
    /**
     * Il cognome dell'utente.
     */
    private String cognome;
    /**
     * Il codice fiscale dell'utente.
     */
    private String codFisc;
    /**
     * L'indirizzo fisico dell'utente.
     */
    private String indirizzo;
    /**
     * L'e-mail del dell'utente.
     */
    private String email;

    /**
     * Costruttore per inizializzare un oggetto Utente con tutti i dettagli specificati.
     * @param userid L'ID dell'utente.
     * @param password La password dell'utente.
     * @param nome Il nome dell'utente.
     * @param cognome Il cognome dell'utente.
     * @param codFisc Il codice fiscale dell'utente.
     * @param indirizzo L'indirizzo dell'utente.
     * @param email L'email dell'utente.
     */
    public Utente(String userid, String password, String nome, String cognome, String codFisc, String indirizzo,
                  String email) {
        super();
        this.userid = userid;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.codFisc = codFisc;
        this.indirizzo = indirizzo;
        this.email = email;
    }

    /**
     * Costruttore per inizializzare un oggetto Utente con i dettagli di base.
     * @param userid   L'ID dell'utente.
     * @param nome     Il nome dell'utente.
     * @param cognome  Il cognome dell'utente.
     */
    public Utente(String userid,String nome, String cognome) {
        super();
        this.userid = userid;
        this.nome = nome;
        this.cognome = cognome;
    }

    /**
     * Restituisce l'ID dell'utente.
     * @return L'ID dell'utente.
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Modifica l'ID dell'utent con il valore specificato.
     * @param userid Il nuovo ID dell'utente.
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Restituisce la password dell'utente.
     * @return La password dell'utente.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Modifica la password dell'utente con il valore specificato.
     * @param password La nuova password dell'utente.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce il nome dell'utente.
     * @return Il nome dell'utente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Modifica il nome dell'utente con il valore specificato.
     * @param nome Il nuovo nome dell'utente.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     * @return Il cognome dell'utente.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Modifica il cognome dell'utente con il valore specificato.
     * @param cognome Il nuovo cognome dell'utente.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce il codice fiscale dell'utente.
     * @return Il codice fiscale dell'utente
     */
    public String getCodFisc() {
        return codFisc;
    }

    /**
     * Modifica il codice fiscale dell'utente.
     * @param codFisc Il nuovo codice fiscale dell'utente.
     */
    public void setCodFisc(String codFisc) {
        this.codFisc = codFisc;
    }

    /**
     * Restituisce l'indirizzo dell'utente.
     * @return L'indirizzo fisico dell'utente.
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Modifica l'indirizzo dell'utente con il valore specificato.
     * @param indirizzo Il nuovo indirizzo fisico dell'utente.
     */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * Restituisce la mail dell'utente.
     * @return L'e-mail dell'utente.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Modifica la mail dell'utente con il valore specificato.
     * @param email La nuova mail dell'utente.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Crea e restituisce un oggetto Utente in base ai dati forniti.
     * @param datiU L'array contenente i dati dell'utente.
     * @return Un nuovo oggetto Utente creato dai dati forniti.
     */
    public static Utente creaUtenteFromData(String[] datiU) {
        if (datiU == null || datiU.length != 8) {
            throw new IllegalArgumentException("datiU deve avere lunghezza 8");
        }
        String userid = datiU[5];
        String pass = datiU[6];
        String nomeUt = datiU[0];
        String cognomeUt = datiU[1];
        String codFisc = datiU[2];
        String indirizzoUt = datiU[3];
        String emailUt = datiU[4];
        return new Utente(userid, pass, nomeUt, cognomeUt, codFisc, indirizzoUt, emailUt);
    }
}
