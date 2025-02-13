CREATE TABLE utentiregistrati(
userid VARCHAR(20) NOT NULL,
password VARCHAR(60) NOT NULL,
nome VARCHAR(30) NOT NULL,
cognome VARCHAR(30) NOT NULL,
codiceFiscale VARCHAR(16) NOT NULL,
indirizzo VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL,
PRIMARY KEY (userid)
);

CREATE TABLE Playlist(
 idPlaylist SERIAL,
 nome_playlist varchar(256) not null,
 userid varchar(20) references utentiregistrati(userid), 
PRIMARY KEY (idPlaylist));

CREATE TABLE Canzoni(
idCanzone SERIAL,
titolo VARCHAR(256) NOT NULL,
autore VARCHAR(256) NOT NULL,
anno numeric (4) NOT NULL,
album VARCHAR(256),
durata numeric (10),
PRIMARY KEY (idCanzone)
); 

-- Crea la tabella Log_Canzoni per tenere traccia delle modifiche fatte sulla tabella Canzoni.
CREATE TABLE Log_Canzoni (
    idModifica SERIAL PRIMARY KEY,
    timestampModifica TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Data e ora della modifica
    tipoOperazione VARCHAR(10) NOT NULL -- Tipo di operazione eseguita (INSERT, UPDATE, DELETE)
);



CREATE TABLE Playlist_Canzoni(	
  idPlaylist integer NOT NULL,
  idCanzone integer NOT NULL,
   FOREIGN KEY (idPlaylist) REFERENCES Playlist(idPlaylist) ON DELETE CASCADE,
  FOREIGN KEY (idCanzone) REFERENCES Canzoni(idCanzone) ON DELETE CASCADE, 
PRIMARY KEY (idPlaylist,idCanzone));

CREATE TABLE CategoriaEmozione(
  nomeEmozione VARCHAR(30),
  spiegazioneEmozione VARCHAR(100) NOT NULL,
  PRIMARY KEY (nomeEmozione)
);

INSERT INTO CategoriaEmozione (nomeEmozione, spiegazioneEmozione) VALUES
('Amazement', 'Feeling of wonder or happiness'),
('Solemnity', 'Feeling of transcendence, inspiration. Thrills.'),
('Tenderness', 'Sensuality, affect, feeling of love'),
('Nostalgia', 'Dreamy, melancholic, sentimental feelings'),
('Calmness', 'Relaxation, serenity, meditativeness'),
('Power', 'Feeling strong, heroic, triumphant, energetic'),
('Joy', 'Feels like dancing, bouncy feeling, animated, amused'),
('Tension', 'Feeling Nervous, impatient, irritated'),
('Sadness', 'Feeling Depressed, sorrowful');

CREATE TABLE Emozioni (
userid VARCHAR(20) NOT NULL,
idCanzone integer NOT NULL,
nomeEmozione VARCHAR(30) NOT NULL,
score numeric(1) NOT NULL,			
noteTestuali VARCHAR(256),
PRIMARY KEY (userid,idCanzone,nomeEmozione),
FOREIGN KEY (userid) REFERENCES UtentiRegistrati(userid),
 FOREIGN KEY (idCanzone) REFERENCES Canzoni(idCanzone),
 FOREIGN KEY (nomeEmozione) REFERENCES CategoriaEmozione(nomeEmozione)
);


