package vn.khanhduc.shoppingbackendservice.service;

import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.shoppingbackendservice.dto.request.ProductCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.PageResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductDetailResponse;

public interface ProductService {
    ProductCreationResponse create(ProductCreationRequest request, MultipartFile file);
    PageResponse<ProductCreationResponse> findAll(int page, int size);
    ProductDetailResponse getById(Long id);
    PageResponse<ProductDetailResponse> search(int page, int size, String[] product, String sortBy);
    PageResponse<ProductDetailResponse> findByAuthor(int page, int size, String author);
}
