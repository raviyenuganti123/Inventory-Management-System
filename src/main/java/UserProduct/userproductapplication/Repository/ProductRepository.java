package UserProduct.userproductapplication.Repository;

import UserProduct.userproductapplication.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
