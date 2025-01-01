package vn.khanhduc.shoppingbackendservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.shoppingbackendservice.dto.request.ProductCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.PageResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ResponseData;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductDetailResponse;
import vn.khanhduc.shoppingbackendservice.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products/save")
    ResponseData<ProductCreationResponse> save(@RequestPart @Valid ProductCreationRequest request,
                                               @RequestPart(value = "file") MultipartFile file) {
        var result = productService.create(request, file);
        return ResponseData.<ProductCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product created")
                .data(result)
                .build();
    }

    @GetMapping("/products")
    ResponseData<PageResponse<ProductCreationResponse>> findAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "2") int size) {
        var result = productService.findAll(page, size);
        return ResponseData.<PageResponse<ProductCreationResponse>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @GetMapping("/products/{id}")
    ResponseData<ProductDetailResponse> getProductById(@PathVariable
                                                       @Min(value = 1,
                                                               message = "Id must be greater than 0") Long id) {
        var result = productService.getById(id);

        return ResponseData.<ProductDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @GetMapping("/products/search")
    ResponseData<PageResponse<ProductDetailResponse>> search(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(value = 1, message = "Page must greater than 0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "search", required = false) String ... search
    ) {
        var result = productService.search(page, size, search, sortBy);
        return ResponseData.<PageResponse<ProductDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

    @GetMapping("/products/search-author")
    ResponseData<PageResponse<ProductDetailResponse>> searchByAuthor(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(value = 1, message = "Page must greater than 0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "author", required = false) String author
    ) {
        var result = productService.findByAuthor(page, size, author);
        return ResponseData.<PageResponse<ProductDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .data(result)
                .build();
    }

}
