package vn.khanhduc.shoppingbackendservice.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.shoppingbackendservice.dto.request.ProductCreationRequest;
import vn.khanhduc.shoppingbackendservice.dto.response.PageResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductCreationResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductDetailResponse;
import vn.khanhduc.shoppingbackendservice.entity.Product;
import vn.khanhduc.shoppingbackendservice.entity.User;
import vn.khanhduc.shoppingbackendservice.exception.AppException;
import vn.khanhduc.shoppingbackendservice.exception.ErrorCode;
import vn.khanhduc.shoppingbackendservice.mapper.ProductMapper;
import vn.khanhduc.shoppingbackendservice.repository.ProductRepository;
import vn.khanhduc.shoppingbackendservice.service.CloudinaryService;
import vn.khanhduc.shoppingbackendservice.service.ProductService;
import vn.khanhduc.shoppingbackendservice.service.specification.ProductBuilder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @CacheEvict(value = "productsCache", allEntries = true)
    public ProductCreationResponse create(ProductCreationRequest request, MultipartFile file) {
        log.info("Create Product");
        Product product = ProductMapper.toProduct(request);

        String thumbnail = cloudinaryService.uploadImage(file);
        product.setThumbnail(thumbnail);
        productRepository.save(product);

        log.info("Save Product Successfully");
        return ProductMapper.toProductResponse(product);
    }

    @Override
//    @Cacheable(value = "productsCache", key = "#page + '-' + #size")
    public PageResponse<ProductCreationResponse> findAll(int page, int size) {
        log.info("Fetching data from the database");
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductCreationResponse> responses = products.getContent()
                .stream().map(ProductMapper::toProductResponse)
                .toList();

        return PageResponse.<ProductCreationResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .data(responses)
                .build();
    }

    @Override
    public ProductDetailResponse getById(Long id) {
        log.info("Fetching product detail from the database");
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return ProductMapper.toProductDetailResponse(product);
    }

    @Override
    public PageResponse<ProductDetailResponse> search(int page, int size, String[] products, String sortBy) {
        ProductBuilder productBuilder = new ProductBuilder();
        if(products != null) {
           for(String product : products) {
               Pattern pattern = Pattern.compile("(\\w+?)([:><~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
               Matcher matcher = pattern.matcher(product);
               if(matcher.find()) {
                   productBuilder.build(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
               }
           }
        }
        Sort sort = Sort.unsorted();
        if(sortBy != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                String columnName = matcher.group(1);
                String direction = matcher.group(3);

                sort = direction.equalsIgnoreCase("asc") ?
                        Sort.by(columnName).ascending()
                        : Sort.by(columnName).descending();
            }
        }
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> listSearch = productRepository.findAll(productBuilder.build(), pageable);

        List<ProductDetailResponse> response = listSearch.getContent()
                .stream().map(product -> ProductDetailResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .author(product.getAuthor().getUsername())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .thumbnail(product.getThumbnail())
                        .build())
                .toList();

        return PageResponse.<ProductDetailResponse>builder()
                .currentPage(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(listSearch.getTotalElements())
                .totalPages(listSearch.getTotalPages())
                .data(response)
                .build();
    }

    @Override
    public PageResponse<ProductDetailResponse> findByAuthor(int page, int size, String author) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = builder.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        Join<Product, User> authorJoin = root.join("author");

        String searchPattern = "%" + author.toLowerCase() + "%";
        Predicate predicate = builder.like(builder.lower(authorJoin.get("name")), searchPattern);
        query.where(predicate);

        List<Product> productList = entityManager.createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        // Truy vấn đếm số lượng kết quả
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        Join<Product, User> countAuthorJoin = countRoot.join("author"); // Tạo mới Join
        Predicate countPredicate = builder.like(builder.lower(countAuthorJoin.get("name")), searchPattern);
        countQuery.select(builder.count(countRoot));
        countQuery.where(countPredicate);

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ProductDetailResponse> response = productList.stream()
                .map(product -> ProductDetailResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .author(product.getAuthor().getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .thumbnail(product.getThumbnail())
                        .build())
                .toList();

        return PageResponse.<ProductDetailResponse>builder()
                .data(response)
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

}
