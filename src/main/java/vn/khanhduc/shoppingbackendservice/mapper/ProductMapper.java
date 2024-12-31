package vn.khanhduc.shoppingbackendservice.mapper;

import vn.khanhduc.shoppingbackendservice.dto.request.ProductCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductDetailResponse;
import vn.khanhduc.shoppingbackendservice.entity.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static Product toProduct(ProductCreationRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
    }

    public static ProductCreationResponse toProductResponse(Product product) {
        return ProductCreationResponse.builder()
                .id(product.getId())
                .author(product.getAuthor().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .thumbnail(product.getThumbnail())
                .build();
    }

    public static ProductDetailResponse toProductDetailResponse(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .author(product.getAuthor().getUsername())
                .price(product.getPrice())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .thumbnail(product.getThumbnail())
                .build();
    }
}
