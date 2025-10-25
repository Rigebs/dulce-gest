package com.rige.dulcegest.data.local.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.Supply

data class ProductRecipeWithSupply(
    @Embedded val recipe: ProductRecipe,
    @Relation(
        parentColumn = "supply_id",
        entityColumn = "id"
    )
    val supply: Supply
)
