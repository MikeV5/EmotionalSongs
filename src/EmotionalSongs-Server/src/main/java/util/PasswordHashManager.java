package util;
import org.mindrot.jbcrypt.BCrypt;

/**
 * La classe {@code PasswordHashManager} fornisce metodi per generare e verificare
 * l'hash delle password utilizzando l'algoritmo della libreria BCrypt.
 */
public class PasswordHashManager {
    /**
     * Genera l'hash della password utilizzando l'algoritmo BCrypt.
     * @param password La password in chiaro da hashare.
     * @return L'hash della password.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica se una password in chiaro corrisponde all'hash della password.
     * @param inputPassword La password in chiaro da verificare.
     * @param hashedPassword L'hash della password memorizzata.
     * @return True se la password in chiaro corrisponde all'hash della password, false altrimenti.
     */
    public static boolean verificaPassword(String inputPassword, String hashedPassword) {
        return BCrypt.checkpw(inputPassword, hashedPassword);
    }
}
