package UserProduct.userproductapplication.Service;

import UserProduct.userproductapplication.Model.Product;
import UserProduct.userproductapplication.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
  @Autowired
   ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setImgUrl(updatedProduct.getImgUrl());
                    return productRepository.save(product);
                }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public boolean deleteProduct(Long id) {
        productRepository.deleteById(id);
        return false;
    }
}
