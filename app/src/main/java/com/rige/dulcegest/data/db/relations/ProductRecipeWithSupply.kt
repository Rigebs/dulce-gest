package com.rige.dulcegest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.db.entities.Supply
import com.rige.dulcegest.data.db.entities.ProductRecipe

data class ProductRecipeWithSupply(
    @Embedded val recipe: ProductRecipe,
    @Relation(
        parentColumn = "supply_id",
        entityColumn = "id"
    )
    val supply: Supply
)
