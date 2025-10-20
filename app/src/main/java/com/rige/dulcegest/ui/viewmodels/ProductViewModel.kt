package com.rige.dulcegest.ui.viewmodels

import androidx.lifecycle.*
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductPresentation
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: ProductRepository
) : ViewModel() {

    val products = repo.allProducts

    val productsWithPresentations = repo.getProductsWithPresentations()

    fun getProductById(id: Long): LiveData<Product?> = repo.getById(id)

    fun saveProduct(
        product: Product,
        recipeList: List<ProductRecipe>,
        presentations: List<ProductPresentation>
    ): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        viewModelScope.launch {
            try {
                val id = if (product.id == 0L) {
                    repo.insert(product)
                } else {
                    repo.update(product)
                    product.id
                }

                repo.setRecipe(id, recipeList.map { it.copy(productId = id) })
                repo.setPresentations(id, presentations.map { it.copy(productId = id) })

                result.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                result.postValue(false)
            }
        }

        return result
    }

    fun update(product: Product) = viewModelScope.launch { repo.update(product) }

    fun delete(product: Product) = viewModelScope.launch { repo.delete(product) }

    fun getRecipeWithIngredients(productId: Long): LiveData<List<ProductRecipeWithIngredient>> {
        return repo.getRecipeWithIngredients(productId)
    }

    fun getPresentationsByProduct(productId: Long): LiveData<List<ProductPresentation>> {
        return repo.getPresentationsByProduct(productId)
    }

    fun setRecipe(productId: Long, recipe: List<ProductRecipe>) = viewModelScope.launch {
        repo.setRecipe(productId, recipe)
    }

    fun adjustStock(id: Long, qtyDelta: Double) = viewModelScope.launch {
        repo.adjustStock(id, qtyDelta)
    }
}
