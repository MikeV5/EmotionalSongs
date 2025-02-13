package emotionalsongs;

import client.ConnessioneServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import util.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Classe Controller che riporta e gestisce le statistiche relative alle emozioni per il brano selezionato.
 * @author Ashley Chudory, Matricola 746423, Sede CO
 * @author Maria Vittoria Ratti, Matricola 748825, Sede CO
 * @author Bogdan Tsitsiurskyi, Matricola 748685, Sede CO
 * @author Miguel Alfredo Valerio Aquino, Matricola 748704, Sede CO
 */
public class StatsEmozioneBranoController implements Initializable {
    /**
     * Label per la simulazione della riproduzione di un brano e
     * per visualizzare le statistiche delle emozioni.
     */
    @FXML
    private Label minsLabel, secondsLabel, durataSongLabel, canzoneLabel, labelUserEmotions,
            userLabel, myEmotionsLabel, scrittaEmotionsLabel,infoMyEmotionsLabel;
    /**
     * Tabella per visualizzare le statistiche relative alle emozioni.
     */
    @FXML
    private TableView<StatsBrano> mediaEmotionTable;
    /**
     * Colonna per visualizzare il nome dell'emozione.
     */
    @FXML
    private TableColumn<StatsBrano, String> emotionCol;
    /**
     * Colonna per visualizzare la media di punteggi dell'emozione.
     */
    @FXML
    private TableColumn<StatsBrano, Double> mediaCol;
    /**
     * Colonna con il numero di utenti che hanno associato
     * almeno un tag emozionale.
     */
    @FXML
    private TableColumn<StatsBrano, Integer> numUtentiCol;
    /**
     * Colonna con i commenti associati all'emozione.
     * Il TextArea e' disabilitato in modo che non si possa scrivere.
     */
    @FXML
    private TableColumn<StatsBrano, TextArea> commentiCol;
    /**
     * ProgressBar per simulare la riproduzione della canzone.
     */
    @FXML
    private ProgressBar progressBarSong;
    /**
     * ScrollPane contenente il label delle emozioni per l'utente loggato.
     */
    @FXML
    private ScrollPane scrollPaneEmotions;
    /**
     * Pulsanti per avviare la riproduzione della canzone e
     * per rimuovere i punteggi.
     */
    @FXML
    private Button btnPlay, rimuoviEmotionsButton;
    /**
     * Grafico a barre per visualizzare la distribuzione delle emozioni
     * associate alla canzone corrente.
     */
    @FXML
    private BarChart<String, Number> barChartEmotions;
    /**
     * Il nome dell'utente loggato.
     */
    private String username;
    /**
     * La playlist corrente.
     */
    private Playlist playlistCorrente;
    /**
     * La canzone corrente di cui vengono
     * visualizzate le statistiche relative alle emozioni.
     */
    private Canzone canzoneCorrente;
    /**
     * Un array contenente il nome delle 9 emozioni.
     */
    private String[] listaEmozioni;
    /**
     * Timer per controllare la durata della riproduzione della canzone.
     */
    private static Timer timer;
    /**
     * Task del timer per eseguire l'aggiornamento dei dati durante la riproduzione della canzone.
     */
    private TimerTask task;
    /**
     * Variabile booleana per indicare se il timer e' in esecuzione.
     */
    private boolean isTimerRunning = false;
    /**
     * Variabile booleana per indicare se la riproduzione della canzone e' completata.
     */
    private boolean isSongCompleted = false;
    /**
     * Tempo trascorso durante la riproduzione della canzone.
     */
    private double tempoTrascorso = 0.0;
    /**
     * Durata della canzone corrente in secondi.
     */
    private int secondiTotali;
    /**
     * Oggetto di tipo {@link MainController}, utilizzato per impostare
     * la scena che si vuole visualizzare.
     */
    private MainController mainController;
    /**
     * Oggetto di tipo {@link ConnessioneServer}, utilizzato per gestire la connessione al server.
     */
    private ConnessioneServer connectServer;

    /**
     * Metodo che inizializza il controller quando viene creato.
     * Imposta i dati iniziali per la visualizzazione delle statistiche.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectServer = ConnessioneServer.getIstanza();
        username = connectServer.getUserLoggato();
        playlistCorrente = connectServer.getPlaylistCorrente();
        canzoneCorrente = connectServer.getCanzoneCorrente();
        userLabel.setText(username);
        canzoneLabel.setText(canzoneCorrente.getNomeCanzone());
        durataSongLabel.setText(canzoneCorrente.getDurataCanzone());
        listaEmozioni = Emozione.getListaEmozioni();
        try {
            richiediDati();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Richiede i dati relativi alle statistiche delle emozioni per la canzone corrente al server
     * e popola la tabella e il grafico con i dati ottenuti.
     * @throws RemoteException Se si verifica un errore durante la comunicazione con il server.
     */
    private void richiediDati() throws RemoteException {
        //Richiesta al server dei dati relativi alle emozioni
        int[] punteggioEmozioniTotali = connectServer.richiestaPunteggioEmozioniTotali();
        int[] punteggioEmozioniPerUtente = connectServer.richiestaPunteggioEmozioniPerUtente();
        int[] numUtentiPerEmozione = connectServer.richiestaNumUtentiPerEmozione();
        int numUtentiConTagEmozionali = connectServer.richiestaNumUtentiConTagEmozionali();
        StatsBrano.resizeEmotionsLabel(myEmotionsLabel, rimuoviEmotionsButton, scrittaEmotionsLabel,
                                  scrollPaneEmotions, username, infoMyEmotionsLabel, punteggioEmozioniPerUtente);
        String[] commentiUsers = connectServer.richiestaCommentiPerEmozione();
        //Calcolo delle medie delle emozioni e popolazione della tabella e del grafico
        double[] mediaEmozioni = calcolaMediaEmozioni(numUtentiPerEmozione, numUtentiConTagEmozionali,
                punteggioEmozioniTotali);
        popolaEmotionsStatsTable(numUtentiPerEmozione, numUtentiConTagEmozionali, mediaEmozioni, commentiUsers);
        creaGraficoBarreEmotions(numUtentiPerEmozione, numUtentiConTagEmozionali, mediaEmozioni);
    }

    /**
     * Calcola le medie dei punteggi per ciascuna emozione.
     * @param numUtentiPerEmozione Un vettore di interi contenente il numero di utenti con tag emozionali per ciascuna emozione.
     * @param numUtentiConTagEmozionali Il numero totale di utenti con almeno un tag emozionale.
     * @param punteggioEmozioniTotali Un array di interi contenente i punteggi totali di emozione per ciascuna emozione.
     * @return Un vettore di double contenente le medie dei punteggi di emozione per ciascuna emozione.
     */
    private double[] calcolaMediaEmozioni(int[] numUtentiPerEmozione, int numUtentiConTagEmozionali, int[] punteggioEmozioniTotali) {
        double[] mediaEmozioni = new double[listaEmozioni.length]; //Ogni emozione inizializzata a 0
        if (numUtentiConTagEmozionali != 0) {
            labelUserEmotions.setText("Utenti: " + numUtentiConTagEmozionali);
            if(username == null){
                scrollPaneEmotions.setVisible(false);
                scrittaEmotionsLabel.setVisible(false);
            }
            for (int i = 0; i < listaEmozioni.length; i++) {
                if (numUtentiPerEmozione[i] != 0) {
                    mediaEmozioni[i] = (double) punteggioEmozioniTotali[i] / (double) numUtentiPerEmozione[i];
                }
            }
        } else {
            nascondiGraficoETabella();
        }
        return mediaEmozioni;
    }

    /**
     * Popola la tabella delle statistiche con i dati forniti.
     * @param numUtentiPerEmozione Un vettore di interi contenente il numero di utenti per ciascuna emozione.
     * @param numUtentiConTagEmozionali Il numero di utenti con tag emozionali.
     * @param mediaEmozioni Un array contenente la media delle emozioni per ciascuna emozione.
     * @param commentiUsers Un vettore di interi contenente i commenti degli utenti per ciascuna emozione.
     */
    private void popolaEmotionsStatsTable(int[] numUtentiPerEmozione, int numUtentiConTagEmozionali, double[] mediaEmozioni, String[] commentiUsers) {
        if (numUtentiConTagEmozionali != 0) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Emotions");
            for (int i = 0; i < listaEmozioni.length; i++) {
                if (numUtentiPerEmozione[i] != 0) {
                    series.getData().add(new XYChart.Data<>(listaEmozioni[i], mediaEmozioni[i]));
                    emotionCol.setCellValueFactory(new PropertyValueFactory<>("nomeEmozione"));
                    mediaCol.setCellValueFactory(new PropertyValueFactory<>("media"));
                    numUtentiCol.setCellValueFactory(new PropertyValueFactory<>("numUtenti"));
                    commentiCol.setCellValueFactory(new PropertyValueFactory<>("commenti"));
                    //Configura la tabella
                    setupTable(commentiCol);
                    mediaEmotionTable.getItems().add(new StatsBrano(listaEmozioni[i], Math.round(mediaEmozioni[i] * 100.0) / 100.0,
                            numUtentiPerEmozione[i], commentiUsers[i]));
                } else {
                    series.getData().add(new XYChart.Data<>(listaEmozioni[i], 0));
                }
            }
            barChartEmotions.getData().add(series);
        }
    }

    /**
     * Nasconde il grafico a barre e la tabella delle statistiche, se non
     * sono presenti emozioni valutate per la canzone corrente.
     */
    private void nascondiGraficoETabella() {
        barChartEmotions.setVisible(false);
        mediaEmotionTable.setVisible(false);
        scrollPaneEmotions.setVisible(false);
        scrittaEmotionsLabel.setVisible(false);
        labelUserEmotions.setText("Non ci sono tag emozionali.");
    }

    /**
     * Crea il grafico a barre delle emozioni.
     * @param numUtentiPerEmozione Un vettore di interi contenente il numero di utenti per ciascuna emozione.
     * @param numUtentiConTagEmozionali Il numero di utenti con tag emozionali.
     * @param mediaEmozioni Un array contenente la media delle emozioni per ciascuna emozione.
     */
    private void creaGraficoBarreEmotions(int[] numUtentiPerEmozione, int numUtentiConTagEmozionali, double[] mediaEmozioni) {
        if (numUtentiConTagEmozionali != 0) {
            barChartEmotions.getData().clear(); //Rimuovi tutte le serie esistenti dal grafico
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Emotions");
            for (int i = 0; i < listaEmozioni.length; i++) {
                if (numUtentiPerEmozione[i] != 0) {
                    series.getData().add(new XYChart.Data<>(listaEmozioni[i], mediaEmozioni[i]));
                } else {
                    series.getData().add(new XYChart.Data<>(listaEmozioni[i], 0));
                }
            }
            barChartEmotions.getData().add(series);
        }
    }


    /**
     * Configura la tabella delle statistiche.
     * Disattiva il {@code TextArea} per i commenti in modo che non sia possibile
     * scrivere alcun testo.
     * @param commentiCol Colonna dei commenti della tabella.
     */
    private void setupTable(TableColumn<StatsBrano, TextArea> commentiCol) {
        mediaEmotionTable.setFixedCellSize(65);
        emotionCol.setResizable(false);
        mediaCol.setResizable(false);
        numUtentiCol.setResizable(false);
        commentiCol.setResizable(false);
        commentiCol.setCellFactory(column -> new TableCell<StatsBrano, TextArea>() {
            @Override
            protected void updateItem(TextArea item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    item.setEditable(false);
                    item.setWrapText(true);
                    setGraphic(item);
                }
            }
        });
    }

    /**
     * Rimuove i punteggi emozionali relativi alla canzone per l'utente corrente.
     * @param actionEvent L'evento generato dal click sul pulsante "Rimuovi i miei punteggi".
     * @throws IOException Se si verifica un errore durante la comunicazione con il server
     * o durante il ricaricamento della scena.
     */
    public void rimuoviEmotions(ActionEvent actionEvent) throws IOException {
        String messaggio = "Sei sicuro di voler rimuovere i punteggi?";
        String risposta = "Punteggio delle emozioni rimosse.";
        boolean confirmed = AlertDialogUtil.showConfirmationDialog(messaggio);
        if (confirmed) {
            boolean eliminazioneRiuscita = connectServer.checkRimuoviPunteggiEmozioni();
            if (eliminazioneRiuscita) {
                mainController = new MainController();
                int numUtentiConTagEmozionali = connectServer.richiestaNumUtentiConTagEmozionali();
                if(numUtentiConTagEmozionali!=0)
                    mainController.scenaStatsBrano(730, 750);
                else  mainController.scenaStatsBrano(730, 230);
            } else {
                String errore = "Errore durante l'eliminazione dei punteggi.";
                AlertDialogUtil.showErrorDialog(errore);
                System.out.println(errore);
            }
        }
    }

    /**
     * Avvia un timer che aggiorna la ProgressBar e le label dei minuti e dei secondi
     * in tempo reale. Quando la canzone e' completata, il timer viene fermato.
     * @param event L'evento generato dal click sul pulsante "Play".
     */
    public void playSong(MouseEvent event) {
        if (isSongCompleted) {
            btnPlay.setDisable(true);
        }
        secondiTotali = Canzone.convertiInSecondi(durataSongLabel.getText());
        //Avvia il timer solo se non è già in esecuzione
        if (!isTimerRunning) {
            btnPlay.setDisable(true);
            isTimerRunning = true; // Imposta lo stato del timer "in esecuzione"
            timer = new Timer();
            task = new TimerTask() {
                public void run() {
                    Platform.runLater(() -> {
                        tempoTrascorso += 1.0;
                        aggiornaLabels((int) tempoTrascorso);
                        aggiornaProgressBar(tempoTrascorso, secondiTotali);
                        // Controlla se la canzone è finita
                        if (tempoTrascorso >= secondiTotali) {
                            isSongCompleted = true; // Imposta il flag a true quando la canzone è completata
                            cancelTimer();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000);
        }
    }

    /**
     * Aggiorna il valore della ProgressBar in base al tempo trascorso della canzone.
     * @param tempoTrascorso Il tempo trascorso in secondi durante la riproduzione della canzone.
     * @param secondiTotali  La durata totale della canzone in secondi.
     */
    private void aggiornaProgressBar(double tempoTrascorso, int secondiTotali) {
        //Calcola il progresso della ProgressBar come un valore tra 0 e 1
        double progress = tempoTrascorso / (double) secondiTotali;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        //Imposta il valore della ProgressBar
        progressBarSong.setProgress(progress);
    }

    /**
     * Aggiorna le label dei minuti e dei secondi con i valori
     * correnti del tempo trascorso.
     * @param tempoTrascorso Il tempo trascorso in secondi durante la riproduzione della canzone.
     */
    private void aggiornaLabels(int tempoTrascorso) {
        int mins = tempoTrascorso / 60;
        int seconds = tempoTrascorso % 60;
        //Aggiorna le label dei minuti e dei secondi
        minsLabel.setText(String.format("%02d", mins));
        secondsLabel.setText(String.format("%02d", seconds));
    }

    /**
     * Cancella il timer in esecuzione, se presente.
     */
    public static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Interrompe il timer in esecuzione e salva il tempo trascorso
     * per poterlo ripristinare quando si riprende la riproduzione.
     * @param mouseEvent L'evento generato dal click sul pulsante "Pause".
     */
    public void pauseSong(MouseEvent mouseEvent) {
        //Restituisci false al flag che indica se la canzone è completata
        isSongCompleted = false;
        if (isTimerRunning) { // Controlla se il timer è in esecuzione
            isTimerRunning = false;
            btnPlay.setDisable(false);
            //Salva il tempo trascorso per poterlo ripristinare quando si riprende
            tempoTrascorso = progressBarSong.getProgress() * secondiTotali;
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    /**
     * Resetta la progressBar e riabilita il pulsante "Play" per permettere una nuova riproduzione.
     * @param mouseEvent L'evento generato dal click sul pulsante "Reset".
     */
    public void resetSong(MouseEvent mouseEvent) {
        //Restituisci false al flag che indica se la canzone è completata
        isSongCompleted = false;
        //Interrompi il timer e reimposta su false
        isTimerRunning = false;
        cancelTimer();
        tempoTrascorso = 0.0;
        //Aggiorna la ProgressBar e le label dei minuti e dei secondi con i nuovi valori
        aggiornaLabels((int) tempoTrascorso);
        aggiornaProgressBar(tempoTrascorso, secondiTotali);
        //Riabilita il pulsante Play
        btnPlay.setDisable(false);
    }

    /**
     * Verifica se l'utente e' loggato e se e' presente una playlist corrente.
     * Se entrambe le condizioni sono verificate, visualizza la scena {@code listaCanzoni.fxml} controllata da {@link ListaCanzoniController}
     * Se invece la playlist corrente e' nulla, viene visualizzata la scena {@code consultaCanzoni.fxml} controllata da {@link ConsultaCanzoniController}.
     * @param actionEvent L'evento generato dal click sul pulsante "<--".
     * @throws IOException Se si verifica un errore durante il caricamente della scena.
     */
    public void tornaScena(ActionEvent actionEvent) throws IOException{
        mainController = new MainController();
        if (username!=null && playlistCorrente!=null) {
            ArrayList<Canzone> canzoniPlaylist = connectServer.getCanzoniPlaylist();
            mainController.scenaListaCanzoni();
        }else if(playlistCorrente == null){
            mainController.scenaConsultaCanzone();
        }else System.out.println("Errore durante il caricamente della scena.");
    }
}
