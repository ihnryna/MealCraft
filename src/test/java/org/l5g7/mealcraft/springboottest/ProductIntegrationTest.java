package org.l5g7.mealcraft.springboottest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.units.Unit;
import org.l5g7.mealcraft.app.units.UnitRepository;
import org.l5g7.mealcraft.app.user.PasswordHasher;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("java:S5786")
public class ProductIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    PasswordHasher passwordHasher;

    @Value("${jwt.cookie-name}")
    String authCookieName;

    String productsBase;
    String cookieHeader;
    Long unitId;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        productRepository.deleteAll();
        unitRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .username("vika")
                .email("vika@mealcraft.org")
                .password(passwordHasher.hashPassword("vika123"))
                .role(Role.USER)
                .avatarUrl(null)
                .createdAt(new Date())
                .build();
        userRepository.save(user);

        Unit unit = new Unit();
        unit.setName("g");
        Unit savedUnit = unitRepository.save(unit);
        unitId = savedUnit.getId();

        String loginUrl = "http://localhost:" + port + "/auth/login";
        LoginUserDto creds = new LoginUserDto("vika@mealcraft.org", "vika123");

        ResponseEntity<String> loginResp = rest.postForEntity(loginUrl, creds, String.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> setCookies = loginResp.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).isNotNull().hasSize(1);

        String only = setCookies.get(0);
        cookieHeader = only.split(";", 2)[0];
        assertThat(cookieHeader).startsWith(authCookieName + "=");

        productsBase = "http://localhost:" + port + "/products";
    }

    @Test
    void createProduct_returns200_andPersists() {
        ProductDto body = ProductDto.builder()
                .name("Sugar")
                .defaultUnitId(unitId)
                .imageUrl("sugar.jpg")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> req = new HttpEntity<>(body, headers);

        ResponseEntity<Void> resp = rest.postForEntity(productsBase, req, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);

        ProductDto saved = products.get(0);
        assertThat(saved.getName()).isEqualTo("Sugar");
        assertThat(saved.getDefaultUnitId()).isEqualTo(unitId);
        assertThat(saved.getDefaultUnitName()).isEqualTo("g");
        assertThat(saved.getImageUrl()).isEqualTo("sugar.jpg");
        assertThat(saved.getOwnerUserId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void createProduct_withoutName_returns400_andNotPersisted() {
        ProductDto body = ProductDto.builder()
                .defaultUnitId(unitId)
                .imageUrl("no-name.jpg")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = rest.postForEntity(productsBase, req, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(productRepository.count()).isZero();
    }

    @Test
    void getProductById_returns200_andBody() {
        ProductDto body = ProductDto.builder()
                .name("Milk")
                .defaultUnitId(unitId)
                .imageUrl("milk.jpg")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> createReq = new HttpEntity<>(body, headers);

        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);

        Long id = products.get(0).getId();

        ResponseEntity<ProductDto> byIdResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.GET,
                listReq,
                ProductDto.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductDto dto = byIdResp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Milk");
        assertThat(dto.getImageUrl()).isEqualTo("milk.jpg");
        assertThat(dto.getDefaultUnitId()).isEqualTo(unitId);
        assertThat(dto.getDefaultUnitName()).isEqualTo("g");
    }

    @Test
    void getAllProducts_returns200_andArray() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto first = ProductDto.builder()
                .name("Apple")
                .defaultUnitId(unitId)
                .imageUrl("apple.jpg")
                .build();

        HttpEntity<ProductDto> firstReq = new HttpEntity<>(first, headers);
        ResponseEntity<Void> firstResp = rest.postForEntity(productsBase, firstReq, Void.class);
        assertThat(firstResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto second = ProductDto.builder()
                .name("Banana")
                .defaultUnitId(unitId)
                .imageUrl("banana.jpg")
                .build();

        HttpEntity<ProductDto> secondReq = new HttpEntity<>(second, headers);
        ResponseEntity<Void> secondResp = rest.postForEntity(productsBase, secondReq, Void.class);
        assertThat(secondResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).anyMatch(p -> p.getName().equals("Apple"));
        assertThat(products).anyMatch(p -> p.getName().equals("Banana"));
    }

    @Test
    void updateProduct_returns200_andUpdates() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto createBody = ProductDto.builder()
                .name("Flour")
                .defaultUnitId(unitId)
                .imageUrl("flour.jpg")
                .build();

        HttpEntity<ProductDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        Long id = products.get(0).getId();

        ProductDto updateBody = ProductDto.builder()
                .id(id)
                .name("Updated flour")
                .defaultUnitId(unitId)
                .imageUrl("updated-flour.jpg")
                .build();

        HttpEntity<ProductDto> updateReq = new HttpEntity<>(updateBody, headers);

        ResponseEntity<Void> updateResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.PUT,
                updateReq,
                Void.class,
                id
        );
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ProductDto> byIdResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.GET,
                listReq,
                ProductDto.class,
                id
        );
        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductDto dto = byIdResp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Updated flour");
        assertThat(dto.getImageUrl()).isEqualTo("updated-flour.jpg");
        assertThat(dto.getDefaultUnitId()).isEqualTo(unitId);
    }

    @Test
    void patchProduct_updatesOnlyProvidedFields() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto createBody = ProductDto.builder()
                .name("Cheese")
                .defaultUnitId(unitId)
                .imageUrl("cheese.jpg")
                .build();

        HttpEntity<ProductDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        ProductDto before = products.get(0);
        Long id = before.getId();

        ProductDto patchBody = ProductDto.builder()
                .imageUrl("hard-cheese.jpg")
                .build();

        HttpEntity<ProductDto> patchReq = new HttpEntity<>(patchBody, headers);

        ResponseEntity<Void> patchResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.PATCH,
                patchReq,
                Void.class,
                id
        );
        assertThat(patchResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ProductDto> byIdResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.GET,
                listReq,
                ProductDto.class,
                id
        );
        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductDto dto = byIdResp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Cheese");
        assertThat(dto.getDefaultUnitId()).isEqualTo(unitId);
        assertThat(dto.getImageUrl()).isEqualTo("hard-cheese.jpg");
    }

    @Test
    void deleteProduct_returns200_andRemovesFromDb() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto createBody = ProductDto.builder()
                .name("Oil")
                .defaultUnitId(unitId)
                .imageUrl("oil.jpg")
                .build();

        HttpEntity<ProductDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        Long id = products.get(0).getId();

        HttpEntity<Void> deleteReq = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.DELETE,
                deleteReq,
                Void.class,
                id
        );
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<ProductDto>> afterDeleteResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(afterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> after = afterDeleteResp.getBody();
        assertThat(after).isNotNull();
        assertThat(after).isEmpty();
    }

    @Test
    void searchProductsByPrefix_returnsOnlyMatching() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto salt = ProductDto.builder()
                .name("Salt")
                .defaultUnitId(unitId)
                .imageUrl("salt.jpg")
                .build();

        ProductDto sugar = ProductDto.builder()
                .name("Sugar")
                .defaultUnitId(unitId)
                .imageUrl("sugar.jpg")
                .build();

        ProductDto butter = ProductDto.builder()
                .name("Butter")
                .defaultUnitId(unitId)
                .imageUrl("butter.jpg")
                .build();

        rest.postForEntity(productsBase, new HttpEntity<>(salt, headers), Void.class);
        rest.postForEntity(productsBase, new HttpEntity<>(sugar, headers), Void.class);
        rest.postForEntity(productsBase, new HttpEntity<>(butter, headers), Void.class);

        HttpEntity<Void> searchReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> resp = rest.exchange(
                productsBase + "/search?prefix=Su",
                HttpMethod.GET,
                searchReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> found = resp.getBody();
        assertThat(found).isNotNull();
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Sugar");
    }

    @Test
    void userCannotGetProductOfAnotherUser_returns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto createBody = ProductDto.builder()
                .name("Private sugar")
                .defaultUnitId(unitId)
                .imageUrl("private-sugar.jpg")
                .build();

        HttpEntity<ProductDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        Long id = products.get(0).getId();

        User other = User.builder()
                .username("other")
                .email("other@mealcraft.org")
                .password(passwordHasher.hashPassword("other123"))
                .role(Role.USER)
                .avatarUrl(null)
                .createdAt(new Date())
                .build();
        userRepository.save(other);

        String loginUrl = "http://localhost:" + port + "/auth/login";
        LoginUserDto otherCreds = new LoginUserDto("other@mealcraft.org", "other123");

        ResponseEntity<String> otherLoginResp = rest.postForEntity(loginUrl, otherCreds, String.class);
        assertThat(otherLoginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> otherSetCookies = otherLoginResp.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(otherSetCookies).isNotNull().hasSize(1);
        String otherCookie = otherSetCookies.get(0).split(";", 2)[0];

        HttpHeaders otherHeaders = new HttpHeaders();
        otherHeaders.add(HttpHeaders.COOKIE, otherCookie);
        otherHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> getReqAsOther = new HttpEntity<>(otherHeaders);

        ResponseEntity<String> byIdResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.GET,
                getReqAsOther,
                String.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminCannotGetProductOfAnotherUser_returns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductDto createBody = ProductDto.builder()
                .name("User product")
                .defaultUnitId(unitId)
                .imageUrl("user-product.jpg")
                .build();

        HttpEntity<ProductDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(productsBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDto>> listResp = rest.exchange(
                productsBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDto> products = listResp.getBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(1);
        Long id = products.get(0).getId();

        User admin = User.builder()
                .username("admin")
                .email("admin@mealcraft.org")
                .password(passwordHasher.hashPassword("admin123"))
                .role(Role.ADMIN)
                .avatarUrl(null)
                .createdAt(new Date())
                .build();
        userRepository.save(admin);

        String loginUrl = "http://localhost:" + port + "/auth/login";
        LoginUserDto adminCreds = new LoginUserDto("admin@mealcraft.org", "admin123");

        ResponseEntity<String> adminLoginResp = rest.postForEntity(loginUrl, adminCreds, String.class);
        assertThat(adminLoginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> adminSetCookies = adminLoginResp.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(adminSetCookies).isNotNull().hasSize(1);
        String adminCookie = adminSetCookies.get(0).split(";", 2)[0];

        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.add(HttpHeaders.COOKIE, adminCookie);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> getReqAsAdmin = new HttpEntity<>(adminHeaders);

        ResponseEntity<String> byIdResp = rest.exchange(
                productsBase + "/{id}",
                HttpMethod.GET,
                getReqAsAdmin,
                String.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
