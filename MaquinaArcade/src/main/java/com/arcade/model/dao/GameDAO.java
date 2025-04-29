package com.arcade.model.dao;

import java.util.List;

import com.arcade.model.entity.GameRecord;
import com.arcade.service.GameFactory.GameType;

/**
 * Interfaz DAO (Data Access Object) para las operaciones de persistencia de juegos
 * Define los métodos para interactuar con la base de datos
 *
 * Parte del patrón DAO que separa la lógica de acceso a datos de la lógica de negocio
 */
public interface GameDAO {

    /**
     * Guarda un registro de juego en la base de datos
     * @param record registro a guardar
     * @return registro guardado con id asignado
     */
    GameRecord save(GameRecord record);

    /**
     * Actualiza un registro existente
     * @param record registro a actualizar
     * @return registro actualizado
     */
    GameRecord update(GameRecord record);

    /**
     * Obtiene un registro por su id
     * @param id identificador del registro
     * @return registro encontrado o null si no existe
     */
    GameRecord findById(Long id);

    /**
     * Obtiene todos los registros
     * @return lista con todos los registros
     */
    List<GameRecord> findAll();

    /**
     * Obtiene los registros de un tipo de juego específico
     * @param gameType tipo de juego
     * @return lista con los registros del tipo indicado
     */
    List<GameRecord> findByGameType(GameType gameType);

    /**
     * Obtiene los registros de partidas completadas
     * @return lista con los registros de partidas completadas
     */
    List<GameRecord> findCompleted();

    /**
     * Obtiene los registros de partidas no completadas
     * @return lista con los registros de partidas no completadas
     */
    List<GameRecord> findIncomplete();

    /**
     * Elimina un registro
     * @param record registro a eliminar
     */
    void delete(GameRecord record);

    /**
     * Elimina un registro por su id
     * @param id identificador del registro a eliminar
     */
    void deleteById(Long id);

    /**
     * Elimina todos los registros
     */
    void deleteAll();

    /**
     * Cuenta el total de registros
     * @return número de registros
     */
    long count();

    /**
     * Cuenta los registros de un tipo de juego específico
     * @param gameType tipo de juego
     * @return número de registros del tipo indicado
     */
    long countByGameType(GameType gameType);

    /**
     * Cuenta los registros de partidas completadas
     * @return número de registros de partidas completadas
     */
    long countCompleted();

    /**
     * Cierra los recursos del DAO
     */
    void close();
}