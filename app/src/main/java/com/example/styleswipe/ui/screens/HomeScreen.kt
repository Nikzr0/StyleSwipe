package com.example.styleswipe.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.styleswipe.ClothingViewModel

@Composable
fun HomeScreen(
    viewModel: ClothingViewModel,
    onNavigateToAdd: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Тук ще бъдат дрехите за Swipe!")
    }
}