# EmotionalSongs

Applicazione client-server che permette agli utenti di annotare e organizzare emozioni associate a brani musicali. Gli utenti possono valutare le emozioni provate per ciascun brano e creare playlist personalizzate. L'applicazione fornisce anche un riepilogo delle emozioni aggregate per ogni brano.

## Tecnologie Utilizzate
- **Linguaggi:** Java, SQL
- **Framework e Librerie:** JavaFX 17, PostgreSQL JDBC, jBCrypt 0.4
- **Database:** PostgreSQL
- **Architettura:** Client-Server con Java RMI
- **Strumenti:** IntelliJ IDEA, Maven, Scene Builder, pgAdmin 4, draw.io

## Struttura del Progetto
- **Client:** Applicazione Java con interfaccia grafica sviluppata in JavaFX.
- **Server:** Gestisce le richieste del client e interagisce con il database.
- **Database:** Memorizza le informazioni relative ai brani, utenti e valutazioni.
- **DBCreator:** Script per la creazione e gestione del database.
