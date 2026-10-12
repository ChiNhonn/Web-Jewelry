package com.aura.jewelry.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aura.jewelry.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun clearCategories()
}
