package ru.mygames.rickandmortyapi.model

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val origin: LocationDto,
    val location: LocationDto
)

data class LocationDto(
    val name: String
)