package ru.mygames.rickandmortyapi.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mygames.rickandmortyapi.data.CharacterDao
import ru.mygames.rickandmortyapi.data.CharacterEntity
import ru.mygames.rickandmortyapi.model.CharacterDto
import ru.mygames.rickandmortyapi.nerwork.ApiService

class CharacterRepository(
    private val api: ApiService,
    private val dao: CharacterDao
) {
    fun getCachedCharacters(): Flow<List<CharacterEntity>> = dao.getAllCharacters()

    suspend fun fetchCharacters(): List<CharacterDto> {
        val characters = api.getCharacters().results
        dao.clearAll()
        dao.insertAll(characters.map {
            CharacterEntity(
                it.id, it.name, it.status, it.species, it.gender, it.image
            )
        })
        return characters
    }

    suspend fun getCharacters(
        status: String? = null,
        gender: String? = null,
        species: String? = null
    ): List<CharacterEntity> {
        return getCachedCharactersOnce().filter { character ->
            (status == null || character.status.equals(status, ignoreCase = true)) &&
                    (gender == null || character.gender.equals(gender, ignoreCase = true)) &&
                    (species == null || character.species.contains(species, ignoreCase = true))
        }
    }

    suspend fun getCharacterById(id: Int): CharacterDto = api.getCharacterById(id)

    private suspend fun getCachedCharactersOnce(): List<CharacterEntity> {
        return getCachedCharacters().first()
    }
}