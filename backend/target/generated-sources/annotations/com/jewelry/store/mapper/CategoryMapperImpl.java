package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.CategoryResponse;
import com.jewelry.store.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-08T19:42:08+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryResponse toCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.parentId( categoryParentId( category ) );
        categoryResponse.children( toCategoryResponseList( category.getChildren() ) );
        categoryResponse.description( category.getDescription() );
        categoryResponse.id( category.getId() );
        categoryResponse.imageUrl( category.getImageUrl() );
        categoryResponse.name( category.getName() );
        categoryResponse.promotionPercent( category.getPromotionPercent() );
        categoryResponse.slug( category.getSlug() );
        categoryResponse.status( category.getStatus() );

        return categoryResponse.build();
    }

    @Override
    public List<CategoryResponse> toCategoryResponseList(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryResponse> list = new ArrayList<CategoryResponse>( categories.size() );
        for ( Category category : categories ) {
            list.add( toCategoryResponse( category ) );
        }

        return list;
    }

    private Long categoryParentId(Category category) {
        if ( category == null ) {
            return null;
        }
        Category parent = category.getParent();
        if ( parent == null ) {
            return null;
        }
        Long id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
