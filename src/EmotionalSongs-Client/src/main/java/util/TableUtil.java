package util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.rmi.RemoteException;
import java.util.*;
import java.util.function.Consumer;

/**
 * La classe {@code TableUtils} fornisce metodi di utilità per la gestione delle tabelle e i filtri dinamici.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class TableUtil {
    /**
     * La codifica dei caratteri ASCII per il filtro di ricerca.
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
     * Codifica dei caratteri UNICODE per il filtro di ricerca.
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
     * Il nome dell'emozione.
     */
    private String emotionalCategory;
    /**
     * La breve spiegazione dell'emozione
     */
    private String explanation;
    /**
     * Contiene i punteggi dall'1 al 5.
     */
    private final ComboBox score = new ComboBox(FXCollections.observableArrayList("1", "2", "3", "4", "5"));;
    /**
     * Area degli eventuali  commenti.
     */
    private TextArea notes;

    /**
     * Costruisce un'istanza della classe Emozione, ricevendo come argomenti:
     * nome dell'emozione, breve spiegazione dell'emozione, punteggio e commenti
     * @param emotionalCategory Il nome dell'emozione.
     * @param explanation La spiegazione dell'emozione.
     */
    public TableUtil(String emotionalCategory, String explanation) {
        super();
        this.emotionalCategory = emotionalCategory;
        this.explanation = explanation;
        this.notes = new TextArea("");
        this.notes.setWrapText(true);
    }

    /**
     * Restituisce il nome dell'istanza Emozione
     * @return Il nome dell'emozione
     */
    public String getEmotionalCategory() {
        return emotionalCategory;
    }

    /**
     * Modifica il nome dell'istanza Emozione
     * @param emotionalCategory Il nuovo nome dell'emozione
     */
    public void setEmotionalCategory(String emotionalCategory) {
        this.emotionalCategory = emotionalCategory;
    }

    /**
     * Restituisce la spiegazione dell'istanza Emozione
     * @return La breve spiegazione dell'emozione
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Modifica la spiegazione dell'istanza Emozione
     * @param explanation La nuova spiegazione dell'emozione
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * Restituisce il punteggio dell'istanza Emozione
     * @return Il punteggio dell'emozione
     */
    public ComboBox getScore() {
        return score;
    }

    /**
     * Restituisce gli eventuali commenti dell'istanza Emozione
     * @return I commenti dell'emozione
     */
    public TextArea getNotes() {
        return notes;
    }

    /**
     * Modifica gli eventuali commenti dell'istanza Emozione
     * @param notes I nuovi commenti dell'emozione
     */
    public void setNotes(TextArea notes) {
        this.notes = notes;
    }

    /**
     * Metodo utilizzato per creare una lista filtrata di brani musicali e visualizzarla all'interno della {@code TableView}.
     * Consente di filtrare dinamicamente i brani musicali in base al testo inserito dall'utente.
     * @param songTable La Tabella in cui visualizzare la lista di brani musicali.
     * @param idCol La colonna che visualizza l'ID del brano musicale.
     * @param titoloCol La colonna che visualizza il titolo del brano musicale.
     * @param autoreCol La colonna che visualizza l'autore del brano musicale.
     * @param annoCol La colonna che visualizza l'anno di rilascio del brano musicale.
     * @param durataCol La colonna che visualizza la durata del brano musicale nel formato "MM:SS".
     * @param textFieldNomeCanzone  Il TextField in cui l'utente inserisce il testo di ricerca per filtrare i brani musicali.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    public static void cercaBranoMusicale(TableView<Canzone> songTable, TableColumn<Canzone, Integer> idCol,
                                           TableColumn<Canzone, String> titoloCol, TableColumn<Canzone, String> autoreCol,
                                           TableColumn<Canzone, Integer> annoCol, TableColumn<Canzone, String> durataCol,
                                           TextField textFieldNomeCanzone) throws RemoteException {
        Collection<Canzone> canzoni = SongManager.getIstanza().getAllSongs();
        defaultTableSongs(idCol, titoloCol, autoreCol, annoCol,durataCol);
        List<Canzone> canzoniOriginali = new ArrayList<>(canzoni);
        ObservableList<Canzone> canzoniObservable = FXCollections.observableArrayList(canzoniOriginali);
        canzoniObservable.sort(Comparator.comparing(Canzone::getIdCanzone));
        songTable.setItems(canzoniObservable);
        //Filtro dinamico
        FilteredList<Canzone> filteredList = new FilteredList<>(canzoniObservable, p -> true);
        //Aggiunge un listener alla proprietà text del textField
        textFieldNomeCanzone.textProperty().addListener((observable, oldValue, newValue) -> {
            //Imposta il predicato di filtro in base al testo digitato dall'utente
            filteredList.setPredicate(canzone -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Nessuna ricerca, mostra tutti gli elementi
                }
                String textRicerca = convertiAscii(newValue.toLowerCase()); // Testo di ricerca nel label
                String nomeCanzone = canzone.getNomeCanzone().toLowerCase();
                String nomeAutore = canzone.getNomeAutore().toLowerCase();
                String dataCanzone = String.valueOf(canzone.getDataCanzone());
                String autoreAnno = nomeAutore + " " + dataCanzone;
                //Esegue la ricerca in base alla categoria selezionata
                if (nomeCanzone.contains(textRicerca)) {
                    return true;
                } else if (nomeAutore.contains(textRicerca)) {
                    return true;
                } else if (autoreAnno.contains(textRicerca)) {
                    return true;
                } else if (dataCanzone.contains(textRicerca)) {
                    return true;
                }
                return false; // Non trovata
            });
        });
        //Imposta la lista filtrata come sorgente per la tabella
        songTable.setItems(filteredList);
    }

    /**
     * Configura le colonne della tabella per la visualizzazione dei brani musicali.
     * Associa le proprietà delle colonne alle proprietà degli oggetti Canzone.
     * @param idCol La colonna che visualizza l'ID del brano musicale.
     * @param titoloCol La colonna che visualizza il titolo del brano musicale.
     * @param autoreCol La colonna che visualizza l'autore del brano musicale.
     * @param annoCol La colonna che visualizza l'anno di rilascio del brano musicale.
     * @param durataCol La colonna che visualizza la durata del brano musicale nel formato "MM:SS".
     */
    public static void defaultTableSongs(TableColumn<Canzone, Integer> idCol, TableColumn<Canzone, String> titoloCol, TableColumn<Canzone, String> autoreCol,
                                         TableColumn<Canzone, Integer> annoCol, TableColumn<Canzone, String> durataCol) {
        idCol.setCellValueFactory(new PropertyValueFactory<Canzone, Integer>("idCanzone"));
        titoloCol.setCellValueFactory(new PropertyValueFactory<Canzone, String>("nomeCanzone"));
        autoreCol.setCellValueFactory(new PropertyValueFactory<Canzone, String>("nomeAutore"));
        annoCol.setCellValueFactory(new PropertyValueFactory<Canzone, Integer>("dataCanzone"));
        durataCol.setCellValueFactory(new PropertyValueFactory<Canzone, String>("durataCanzone"));
    }

    /**
     * Configura le colonne della tabella per la visualizzazione di emozioni.
     * Associa le proprietà delle colonne alle proprietà degli oggetti TableUtils (rappresentanti le emozioni).
     * @param emotionTable La colonna in cui visualizzare la lista di emozioni.
     * @param categoryCol La colonna che visualizza la categoria emotiva dell'emozione.
     * @param explanationCol La colonna che visualizza una spiegazione dell'emozione.
     * @param scoreCol La colonna che visualizza una ComboBox per selezionare il punteggio associato all'emozione.
     * @param notesCol La colonna che visualizza una TextArea per inserire eventuali note sull'emozione.
     * @param emotionDataList L'ObservableList contenente i dati delle emozioni da visualizzare.
     */
    public static void defaultTableEmotions(TableView<TableUtil> emotionTable, TableColumn<TableUtil, String> categoryCol, TableColumn<TableUtil, String> explanationCol,
                                            TableColumn<TableUtil, ComboBox> scoreCol, TableColumn<TableUtil, TextArea> notesCol,
                                            ObservableList<TableUtil> emotionDataList) {
        categoryCol.setCellValueFactory(new PropertyValueFactory<TableUtil, String>("emotionalCategory"));
        explanationCol.setCellValueFactory(new PropertyValueFactory<TableUtil, String>("explanation"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<TableUtil, ComboBox>("score"));
        notesCol.setCellValueFactory(new PropertyValueFactory<TableUtil, TextArea>("notes"));
        emotionTable.setFixedCellSize(45);
        categoryCol.setResizable(false);
        explanationCol.setResizable(false);
        scoreCol.setResizable(false);
        emotionTable.setItems(emotionDataList );
    }

    /**
     * Configura una colonna per eliminare elementi dalla tabella "ListaPlaylist" o "ListaCanzone".
     * @param <T> Il tipo degli elementi nella tabella.
     * @param tableView la TableView a cui aggiungere la colonna.
     * @param deleteColumn La TableColumn che conterrà il pulsante di eliminazione.
     * @param nomeElemento Il nome dell'elemento da visualizzare nel messaggio di conferma.
     * @param deleteAction L'azione da eseguire quando il pulsante di eliminazione viene premuto.
     */
    public static <T> void setupTabellaEliminaCol(TableView<T> tableView, TableColumn<T, Void> deleteColumn, String nomeElemento, Consumer<T> deleteAction) {
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("x");
            {
                deleteButton.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(30,153,179,0.27), rgba(86,126,64,0.27)); " +
                        "-fx-text-fill: black;");
                deleteButton.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    String messaggio = "Sei sicuro di voler eliminare la " + nomeElemento + "?";
                    boolean confirmed = AlertDialogUtil.showConfirmationDialog(messaggio);
                    if (confirmed) {
                        tableView.getItems().remove(item); //Rimuove dalla lista visuale
                        deleteAction.accept(item); //Esegue l'azione di eliminazione appropriata
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    /**
     * Converte una stringa contenente caratteri UNICODE in una stringa contenente
     * solo caratteri ASCII "puliti" (senza caratteri speciali o accentati).
     * Metodo utilizzato per effettuare la ricerca ignorando le differenze di formattazione.
     * @param s La stringa in formato UNICODE da convertire in formato ASCII.
     * @return La stringa convertita in formato ASCII.
     */
    public static String convertiAscii(String s) {
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
