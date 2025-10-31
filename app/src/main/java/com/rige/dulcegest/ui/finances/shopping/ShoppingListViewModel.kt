package com.rige.dulcegest.ui.finances.shopping

import androidx.lifecycle.*
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val repository: ShoppingListRepository
) : ViewModel() {

    val items = repository.getItemsWithSupply().asLiveData()

    fun addItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun deleteItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.delete(item)
    }

    fun deleteItemBySupplyId(supplyId: Long) {
        viewModelScope.launch {
            repository.deleteItemBySupplyId(supplyId)
        }
    }

    fun togglePurchased(item: ShoppingListItem) = viewModelScope.launch {
        repository.update(item.copy(purchased = !item.purchased))
    }
}