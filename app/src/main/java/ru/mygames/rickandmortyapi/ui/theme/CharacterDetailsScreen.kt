package ru.mygames.rickandmortyapi.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = rememberAsyncImagePainter(c.image),
                    contentDescription = c.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Имя: ${c.name}", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Статус: ${c.status}", fontSize = 18.sp)
                Text("Вид: ${c.species}", fontSize = 18.sp)
                Text("Пол: ${c.gender}", fontSize = 18.sp)

                if (c.type.isNotEmpty()) {
                    Text("Тип: ${c.type}", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Происхождение: ${c.origin.name}", fontSize = 18.sp)
                Text("Локация: ${c.location.name}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Эпизодов: ${c.episode.size}", fontSize = 18.sp)
                //Text("Добавлен в API: ${c.created.take(10)}", fontSize = 16.sp)
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
