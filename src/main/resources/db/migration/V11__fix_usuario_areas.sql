-- Corrige a tabela usuario_areas

-- Remove a tabela existente
DROP TABLE IF EXISTS usuario_areas;

-- Recria a tabela com as configurações corretas
CREATE TABLE IF NOT EXISTS usuario_areas (
    usuario_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, area_id),
    CONSTRAINT fk_usuario_areas_usuario 
        FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_usuario_areas_area 
        FOREIGN KEY (area_id) 
        REFERENCES areas_atuacao(id)
        ON DELETE CASCADE
); 