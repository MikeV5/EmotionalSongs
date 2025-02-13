# Requisiti: È preferibile posizionare l'intero progetto o almeno la cartella "Server" in un'unità o directory diversa dal disco di sistema, in modo da poter importare con i permessi necessari tutti i dati dal file 'canzoni.csv'.

# Nota: Se si verificano problemi di autorizzazioni, assicurarsi che il server PostgreSQL abbia i permessi di lettura sia per il file CSV che per la cartella in cui è contenuto. 
In seguito, i comandi per concedere i privilegi necessari per il file CSV:

	# Su Windows (sostituire "/percorso/del/file/" con il percorso completo del file csv)
		icacls "/percorso/del/file/canzoni.csv" /grant Everyone:(RX)

==================================================
## Creazione del database e avvio del Server
==================================================

# Dopo che il file csv abbia i permessi necessari, possiamo far partire i file jar:
1. Eseguire da riga di comando (dopo aver raggiunto questa cartella): 

			java -jar EmotionalSongs-DBCreator.jar

oppure (sostituire "path_javafx" e "path_postgresql" con il percorso completo dei file):
java --module-path="path_javafx/lib" --add-modules=ALL-MODULE-PATH -cp "path_postgresql/postgresql-42.6.0.jar" -jar EmotionalSongs-DBCreator.jar

2. Inserire le credenziali superuser del server PostgreSQL.
3. Se le credenziali sono corrette, il database "emotionalsongs" verrà creato e i file "creaTabelle.sql" - "canzoni.csv" verranno caricati automaticamente.
4. Ora è possibile eseguire il jar del server per stabilire la connessione (sempre da riga di comando):

			java -jar EmotionalSongs-Server.jar

oppure (sostituire "path_javafx" e "path_postgresql" con il percorso completo dei file):
java --module-path="path_javafx/lib" --add-modules=ALL-MODULE-PATH -cp "path_postgresql/postgresql-42.6.0.jar" -jar EmotionalSongs-DBCreator.jar

==================================================
## Creazione manuale del database con pgAdmin 4  
==================================================

In alcuni casi, potrebbe verificarsi un errore nel processo di creazione del database o nel caricamento del file sql-csv. In tal caso, è possibile effettuare la creazione manualmente seguendo le istruzioni riportate di seguito (verificare innanzitutto che il database non sia già stato creato):

1. Creare il database
	- Click destro su "Databases" e selezionare "Create" > "Database...". Assegnare il nome
	 "emotionalsongs" al nuovo database e confermare.

2. Creare le tabelle
Per creare tutte le tabelle necessarie è possibile farlo in due modi diversi:

2.1. Utilizzando Query tool
	- Click destro sul database "emotionalsongs" e selezionare "Query Tool".
	- Copiare il contenuto del file "creaTabelle.sql" ed eseguire la query.
	
2.2. Caricando direttamente il file "creaTabelle.sql" (solo su pgadmin4 v6.21)
	- Click destro sul database "emotionalsongs" e selezionare "Restore...".
	- Su "Filename" selezionare il file "creaTabelle.sql" per creare le tabelle e poi "Restore". 

3. Importare le canzoni nel database
Dopo aver creato tutte le tabelle necessarie, è possibile popolare la tabella "canzoni" utilizzando un file CSV. Ecco come farlo:

3.1. Utilizzando pgadmin
	- Click destro sulla tabella "canzoni" e selezionare "Import/Export data...".
	- Su "Filename" specificare il percorso del file csv.
	- Su "Options":
		Abilitare l'opzione "Header" e impostare "Delimiter" su ";".
	- Su "Columns":
		Rimuovere la colonna "idCanzone" dalle colonne selezionate per importare solo le colonne 
		necessarie (titolo, autore, anno, album, durata) dal file CSV.

3.2. Utilizzando il terminale SQL shell(psql)
Se si preferisce l'approccio da linea di comando, è possibile eseguire il seguente comando tramite il terminale SQL (psql). Assicurarsi di sostituire "path" con il percorso completo del file "canzoni.csv":

\copy Canzoni(titolo, autore, anno, album, durata) FROM 'path\canzoni.csv' WITH (FORMAT csv, HEADER true, DELIMITER ';', ENCODING 'UTF-8');

4. Aggiungere il trigger
	- Click destro sul database "emotionalsongs" e selezionare "Query Tool".
	- Copiare il contenuto del file "trigger_canzoni.sql" ed eseguire la query.




