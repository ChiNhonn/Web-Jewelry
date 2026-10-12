package com.aura.jewelry.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aura.jewelry.core.network.model.CategoryDto

/**
 * Room Entity caching jewelry categories locally for instant navigation loading.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val slug: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val parentId: Long? = null,
    val displayOrder: Int = 0
)

fun CategoryDto.toEntity(order: Int = 0): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        slug = slug,
        description = description,
        imageUrl = imageUrl,
        parentId = parentId,
        displayOrder = order
    )
}

fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        id = id,
        name = name,
        slug = slug,
        description = description,
        imageUrl = imageUrl,
        parentId = parentId
    )
}
