package com.rige.dulcegest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.ProductRecipe

data class ProductRecipeWithIngredient(
    @Embedded val recipe: ProductRecipe,
    @Relation(
        parentColumn = "ingredient_id",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)
