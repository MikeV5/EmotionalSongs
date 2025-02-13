package util;

/**
 * Classe utilizzata per gestire la conversione di caratteri speciali e accentati presenti nelle lingue
 * internazionali in caratteri ASCII standard.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class CaratteriASCII {
    /**
     * La codifica dei caratteri ASCII.
     */
    private static final String PLAIN_ASCII =
            "AaEeIiOoUu"    // grave
                    + "AaEeIiOoUuYy"  // acute
                    + "AaEeIiOoUuYy"  // circumflex
                    + "AaOoNn"        // tilde
                    + "AaEeIiOoUuYy"  // umlaut
                    + "Aa"            // ring
                    + "Cc"            // cedilla
                    + "OoUu";         // double acute

    /**
     * Codifica dei caratteri UNICODE.
     */
    private static final String UNICODE =
            "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
                    + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
                    + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
                    + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
                    + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
                    + "\u00C5\u00E5"
                    + "\u00C7\u00E7"
                    + "\u0150\u0151\u0170\u0171";

    /**
     * Converte una stringa contenente caratteri UNICODE in una stringa contenente
     * solo caratteri ASCII "puliti" (senza caratteri speciali o accentati).
     * Metodo utilizzato per effettuare la ricerca ignorando le differenze di formattazione.
     * @param s La stringa in formato UNICODE da convertire in formato ASCII.
     * @return La stringa convertita in formato ASCII.
     */
    public static String convertAscii(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
