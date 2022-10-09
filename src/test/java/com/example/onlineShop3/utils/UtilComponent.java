package com.example.onlineShop3.utils;

import com.example.onlineShop3.controllers.entities.Address;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.enums.Roles;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static com.example.onlineShop3.enums.Currencies.RON;

@Component
@RequiredArgsConstructor
public class UtilComponent {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public static final String LOCALHOST = "http://localhost:";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User saveUserWithRole(Roles role) {
        User userEntity = new User();
        userEntity.setFirstname("IonutC");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(role);
        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("Crizantemelor");
        address.setNumber(2);
        address.setZipcode("11");
        userEntity.setAddress(address);
        userRepository.save(userEntity);
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setDescription("good");
        product.setCurrency(RON);
        product.setPrice(55.15);
        product.setValid(true);
        product.setStock(24);
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);
        productRepository.saveAll(products);
        return product;
    }
}
