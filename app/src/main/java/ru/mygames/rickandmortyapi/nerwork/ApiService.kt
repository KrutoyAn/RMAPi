package ru.mygames.rickandmortyapi.nerwork

import retrofit2.http.GET
import retrofit2.http.Path
import ru.mygames.rickandmortyapi.model.CharacterDto
import ru.mygames.rickandmortyapi.model.CharacterResponse

interface ApiService {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse

    @GET("character/{id}")
        suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}