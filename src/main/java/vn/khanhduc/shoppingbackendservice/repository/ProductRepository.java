package vn.khanhduc.shoppingbackendservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.khanhduc.shoppingbackendservice.dto.response.PageResponse;
import vn.khanhduc.shoppingbackendservice.dto.response.ProductDetailResponse;
import vn.khanhduc.shoppingbackendservice.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

}
