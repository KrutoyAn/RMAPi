package ru.mygames.rickandmortyapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.mygames.rickandmortyapi.data.AppDatabase
import ru.mygames.rickandmortyapi.nerwork.RetrofitInstance
import ru.mygames.rickandmortyapi.repository.CharacterRepository
import ru.mygames.rickandmortyapi.ui.theme.CharacterDetailsScreen
import ru.mygames.rickandmortyapi.ui.theme.MainScreen
import ru.mygames.rickandmortyapi.viewmodel.CharacterViewModel
import ru.mygames.rickandmortyapi.viewmodel.CharacterViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getDatabase(this).characterDao()
        val repository = CharacterRepository(RetrofitInstance.api, dao)
        val factory = CharacterViewModelFactory(repository)


        setContent {
            val navController = rememberNavController()
            val viewModel: CharacterViewModel = viewModel(factory = factory)

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(viewModel) { id ->
                        navController.navigate("details/$id")
                    }
                }
                composable("details/{id}") {
                    val id = it.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                    CharacterDetailsScreen(characterId = id) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}