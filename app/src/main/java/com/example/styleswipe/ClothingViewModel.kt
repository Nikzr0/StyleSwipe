package com.example.styleswipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.styleswipe.data.ClothingItem
import com.example.styleswipe.data.ClothingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClothingViewModel(private val repository: ClothingRepository) : ViewModel() {

    val allItems: StateFlow<List<ClothingItem>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addClothingItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun updateClothingItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteClothingItem(item: ClothingItem) {
        viewModelScope.launch {
            repository.delete(item.id)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StyleSwipeApp)
                ClothingViewModel(application.repository)
            }
        }
    }
}