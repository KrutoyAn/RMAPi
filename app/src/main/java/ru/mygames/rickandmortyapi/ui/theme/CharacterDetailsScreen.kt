package ru.mygames.rickandmortyapi.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import ru.mygames.rickandmortyapi.model.CharacterDto
import ru.mygames.rickandmortyapi.nerwork.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(characterId: Int, onBackClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    var character by remember { mutableStateOf<CharacterDto?>(null) }

    LaunchedEffect(characterId) {
        scope.launch {
            character = RetrofitInstance.api.getCharacterById(characterId)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(character?.name ?: "Детали персонажа") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        character?.let { c ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(c.image),
                    contentDescription = c.name,
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Имя: ${c.name}", fontWeight = FontWeight.Bold)
                Text("Статус: ${c.status}")
                Text("Вид: ${c.species}")
                Text("Пол: ${c.gender}")
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
