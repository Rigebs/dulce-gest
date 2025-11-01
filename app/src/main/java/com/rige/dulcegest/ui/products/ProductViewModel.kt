package com.rige.dulcegest.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.ProductVariant
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.data.repository.ProductRepository
import com.rige.dulcegest.domain.usecases.products.SaveProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val saveProductUseCase: SaveProductUseCase
) : ViewModel() {

    val products = repo.allProducts
    val productsWithPresentations = repo.getProductsWithPresentationsAndVariants()

    fun getProductById(id: Long): LiveData<Product?> = repo.getById(id)

    suspend fun getProductByIdOnce(productId: Long): Product? {
        return repo.getProductByIdSuspend(productId)
    }

    fun getRecipeWithSupplies(productId: Long): LiveData<List<ProductRecipeWithSupply>> {
        return repo.getRecipeWithSupplies(productId)
    }

    fun getPresentationsByProduct(productId: Long): LiveData<List<ProductPresentation>> {
        return repo.getPresentationsByProduct(productId)
    }

    fun getVariantsByProduct(productId: Long): LiveData<List<ProductVariant>> {
        return repo.getVariantsByProduct(productId)
    }

    fun update(product: Product) = viewModelScope.launch { repo.update(product) }

    fun delete(product: Product) = viewModelScope.launch { repo.delete(product) }

    fun saveProduct(
        currentProduct: Product?,
        name: String,
        unit: String,
        price: Double,
        notes: String?,
        imagePath: String?,
        recipeList: List<ProductRecipe>,
        presentations: List<ProductPresentation>,
        variants: List<ProductVariant>
    ): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                val productId = saveProductUseCase.execute(
                    currentProduct,
                    name,
                    unit,
                    price,
                    notes,
                    imagePath,
                    recipeList,
                    presentations,
                    variants
                )
                result.postValue(productId > 0)
            } catch (e: IllegalArgumentException) {
                result.postValue(false)
            } catch (e: Exception) {
                result.postValue(false)
            }
        }
        return result
    }
}