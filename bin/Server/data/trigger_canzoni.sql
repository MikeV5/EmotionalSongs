-- Crea una funzione trigger per registrare le operazioni di modifica sulla tabella Canzoni all’interno della tabella Log_Canzoni
CREATE OR REPLACE FUNCTION aggiorna_modifiche()
RETURNS TRIGGER AS $$
BEGIN
    -- Inserimento nella tabella Log_Canzoni per INSERT
    IF TG_OP = 'INSERT' THEN
        INSERT INTO Log_Canzoni (tipoOperazione)
        VALUES ('INSERT');
    END IF;
    
    -- Inserimento nella tabella Log_Canzoni per UPDATE
    IF TG_OP = 'UPDATE' THEN
        INSERT INTO Log_Canzoni (tipoOperazione)
        VALUES ('UPDATE');
    END IF;

    -- Inserimento nella tabella Log_Canzoni per DELETE
    IF TG_OP = 'DELETE' THEN
        INSERT INTO Log_Canzoni (tipoOperazione)
        VALUES ('DELETE');
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Collegamento del nuovo trigger alle operazioni di INSERT, UPDATE e DELETE sulla tabella Canzoni
CREATE TRIGGER trigger_aggiorna_modifiche
AFTER INSERT OR UPDATE OR DELETE ON Canzoni
FOR EACH ROW
EXECUTE FUNCTION aggiorna_modifiche();