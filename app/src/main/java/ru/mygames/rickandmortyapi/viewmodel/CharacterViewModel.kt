package ru.mygames.rickandmortyapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mygames.rickandmortyapi.data.CharacterEntity
import ru.mygames.rickandmortyapi.repository.CharacterRepository

class CharacterViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> = _characters.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters(
        status: String? = null,
        gender: String? = null,
        species: String? = null
    ) {
        viewModelScope.launch {
            try {
                repository.fetchCharacters()
            } catch (_: Exception) {}

            if (status == null && gender == null && species == null) {
                // Нет фильтра просто отображаем всё из базы
                repository.getCachedCharacters().collect {
                    _characters.value = it
                }
            } else {
                // Есть хотя бы один фильтр  фильтруем и не подписываемся на поток
                val filtered = repository.getCharacters(status, gender, species)
                _characters.value = filtered
            }
        }
    }
}