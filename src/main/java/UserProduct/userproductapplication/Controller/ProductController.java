package UserProduct.userproductapplication.Controller;

import UserProduct.userproductapplication.JWT.JwtUtil;
import UserProduct.userproductapplication.Model.Product;
import UserProduct.userproductapplication.Model.User;
import UserProduct.userproductapplication.Repository.UserRepository;
import UserProduct.userproductapplication.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
@Autowired
UserRepository userRepository;
@Autowired
    JwtUtil jwtUtil;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Get all products
    @GetMapping("/getallproduct")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(products, HttpStatus.OK); // 200 OK
    }

    // Create a new product
    //http://localhost:8082/api/products/addproduct
//    @PostMapping("/addproduct")
//    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
//        if (product == null || product.getName() == null || product.getPrice() <= 0) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
//        }
//
//        Product createdProduct = productService.createProduct(product);
//        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // 201 Created
//    }
    @PostMapping("/addproduct")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @RequestHeader("Authorization") String token) {
        // Extract the username from the JWT token
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " from token

        // Fetch the user from the database using the UserRepository
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // User not found
        }

        User user = userOptional.get();

        // Set the user for the product
        product.setUser(user);

        // Validate the product (check name, price)
        if (product.getName() == null || product.getPrice() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid product
        }

        // Save the product and return the created product
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // Return 201 Created
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existingProduct = productService.updateProduct(id, product);
        if (existingProduct == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
        return new ResponseEntity<>(existingProduct, HttpStatus.OK); // 200 OK
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        boolean isDeleted = productService.deleteProduct(id);
        if (!isDeleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}
