package ru.mygames.rickandmortyapi.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.mygames.rickandmortyapi.viewmodel.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: CharacterViewModel, onCharacterClick: (Int) -> Unit) {
    val characters by viewModel.characters.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing)

    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rick and Morty") },
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск по имени") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                SwipeRefresh(
                    state = refreshState,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.loadCharacters()
                        isRefreshing = false
                    }
                ) {
                    val filteredCharacters = characters.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredCharacters.isEmpty()) {
                        Text(
                            text = "Персонажи не найдены.",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCharacters) { character ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCharacterClick(character.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(1.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(character.image),
                                            contentDescription = character.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(170.dp)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            character.name,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        )
                                        Text("Вид: ${character.species}")
                                        Text("Статус: ${character.status}")
                                        Text("Пол: ${character.gender}")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Экран фильтров
            if (showFilters) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    FilterScreen(
                        onApplyFilters = { status, gender, species ->
                            viewModel.loadCharacters(status, gender, species)
                        },
                        onClose = { showFilters = false }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterScreen(
    onApplyFilters: (status: String?, gender: String?, species: String?) -> Unit,
    onClose: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var speciesInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Фильтры", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Статус:")
        DropdownMenuComponent(
            items = listOf("alive", "dead", "unknown"),
            selectedItem = selectedStatus,
            onItemSelected = { selectedStatus = it }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Пол:")
        DropdownMenuComponent(
            items = listOf("male", "female", "genderless", "unknown"),
            selectedItem = selectedGender,
            onItemSelected = { selectedGender = it }
        )


        Spacer(modifier = Modifier.height(8.dp))
        Text("Вид:")
        OutlinedTextField(
            value = speciesInput,
            onValueChange = { speciesInput = it },
            label = { Text("Вид (species)") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { onClose() }) {
                Text("Отмена")
            }
            Button(onClick = {
                onApplyFilters(selectedStatus, selectedGender, speciesInput.ifBlank { null })
                onClose()
            }) {
                Text("Применить")
            }
        }
    }
}

@Composable
fun DropdownMenuComponent(
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedItem ?: "Выбрать")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}