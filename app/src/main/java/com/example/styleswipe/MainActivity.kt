package com.example.styleswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.styleswipe.ui.screens.AddClothingScreen
import com.example.styleswipe.ui.screens.HomeScreen
import com.example.styleswipe.ui.theme.StyleSwipeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StyleSwipeTheme {
                StyleSwipeAppMain()
            }
        }
    }
}

@Composable
fun StyleSwipeAppMain() {
    val navController = rememberNavController()

    val viewModel: ClothingViewModel = viewModel(factory = ClothingViewModel.Factory)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { navController.navigate("add") }
                )
            }

            composable("add") {
                AddClothingScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}