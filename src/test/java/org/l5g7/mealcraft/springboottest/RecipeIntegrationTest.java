package org.l5g7.mealcraft.springboottest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientDto;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("java:S5786")
public class RecipeIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    PasswordHasher passwordHasher;

    @Value("${jwt.cookie-name}")
    String authCookieName;

    String recipesBase;
    String cookieHeader;
    Long productId;

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

        Product product = Product.builder()
                .name("Sugar")
                .defaultUnit(savedUnit)
                .createdAt(new Date())
                .imageUrl(null)
                .ownerUser(null)
                .build();
        Product savedProduct = productRepository.save(product);
        productId = savedProduct.getId();

        String loginUrl = "http://localhost:" + port + "/auth/login";
        LoginUserDto creds = new LoginUserDto("vika@mealcraft.org", "vika123");

        ResponseEntity<String> loginResp = rest.postForEntity(loginUrl, creds, String.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<String> setCookies = loginResp.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).isNotNull().hasSize(1);

        String only = setCookies.get(0);
        cookieHeader = only.split(";", 2)[0];
        assertThat(cookieHeader).startsWith(authCookieName + "=");

        recipesBase = "http://localhost:" + port + "/recipes";
    }

    @Test
    void createRecipe_returns200_andPersists() {
        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(2.5)
                .build();

        RecipeDto body = RecipeDto.builder()
                .name("Pancakes")
                .imageUrl("pancakes.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDto> req = new HttpEntity<>(body, headers);

        ResponseEntity<Void> resp = rest.postForEntity(recipesBase, req, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> getReq = new HttpEntity<>(headers);

        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                getReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);

        RecipeDto saved = recipes.get(0);
        assertThat(saved.getName()).isEqualTo("Pancakes");
        assertThat(saved.getIngredients()).isNotNull();
        assertThat(saved.getIngredients()).hasSize(1);
        RecipeIngredientDto savedIng = saved.getIngredients().get(0);
        assertThat(savedIng.getProductId()).isEqualTo(productId);
        assertThat(savedIng.getAmount()).isEqualTo(2.5);
    }

    @Test
    void createRecipe_withoutIngredients_returns400_andNotPersisted() {
        RecipeDto body = RecipeDto.builder()
                .name("Empty recipe")
                .imageUrl("empty.jpg")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDto> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = rest.postForEntity(recipesBase, req, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(recipeRepository.count()).isZero();
    }

    @Test
    void getRecipeById_returns200_andBody() {
        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto createBody = RecipeDto.builder()
                .name("Omelette")
                .imageUrl("omelette.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDto> createReq = new HttpEntity<>(createBody, headers);

        ResponseEntity<Void> createResp = rest.postForEntity(recipesBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> getReq = new HttpEntity<>(headers);

        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                getReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);

        Long id = recipes.get(0).getId();

        ResponseEntity<RecipeDto> byIdResp = rest.exchange(
                recipesBase + "/{id}",
                HttpMethod.GET,
                getReq,
                RecipeDto.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        RecipeDto dto = byIdResp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Omelette");
        assertThat(dto.getImageUrl()).isEqualTo("omelette.jpg");
        assertThat(dto.getIngredients()).isNotNull();
        assertThat(dto.getIngredients()).hasSize(1);
        assertThat(dto.getIngredients().get(0).getProductId()).isEqualTo(productId);
    }

    @Test
    void getAllRecipes_returns200_andArray() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto first = RecipeDto.builder()
                .name("First recipe")
                .imageUrl("first.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> firstReq = new HttpEntity<>(first, headers);
        ResponseEntity<Void> firstResp = rest.postForEntity(recipesBase, firstReq, Void.class);
        assertThat(firstResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<RecipeDto>> firstListResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(firstListResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> firstList = firstListResp.getBody();
        assertThat(firstList).isNotNull();
        assertThat(firstList).hasSize(1);
        Long baseId = firstList.get(0).getId();

        RecipeDto second = RecipeDto.builder()
                .name("Second recipe")
                .imageUrl("second.jpg")
                .baseRecipeId(baseId)
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> secondReq = new HttpEntity<>(second, headers);
        ResponseEntity<Void> secondResp = rest.postForEntity(recipesBase, secondReq, Void.class);
        assertThat(secondResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(2);
        assertThat(recipes).anyMatch(r -> r.getName().equals("First recipe"));
        assertThat(recipes).anyMatch(r -> r.getName().equals("Second recipe"));
        Optional<RecipeDto> secondOpt = recipes.stream()
                .filter(r -> r.getName().equals("Second recipe"))
                .findFirst();
        assertThat(secondOpt).isPresent();
        assertThat(secondOpt.get().getBaseRecipeId()).isEqualTo(baseId);
    }

    @Test
    void updateRecipe_returns200_andUpdates() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto createBody = RecipeDto.builder()
                .name("Salad")
                .imageUrl("salad.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(recipesBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);
        Long id = recipes.get(0).getId();

        RecipeIngredientDto updatedIng = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(3.0)
                .build();

        RecipeDto updateBody = RecipeDto.builder()
                .id(id)
                .name("Updated salad")
                .imageUrl("updated-salad.jpg")
                .ingredients(List.of(updatedIng))
                .build();

        HttpEntity<RecipeDto> updateReq = new HttpEntity<>(updateBody, headers);

        ResponseEntity<Void> updateResp = rest.exchange(
                recipesBase + "/{id}",
                HttpMethod.PUT,
                updateReq,
                Void.class,
                id
        );
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<RecipeDto> byIdResp = rest.exchange(
                recipesBase + "/{id}",
                HttpMethod.GET,
                listReq,
                RecipeDto.class,
                id
        );
        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        RecipeDto dto = byIdResp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Updated salad");
        assertThat(dto.getImageUrl()).isEqualTo("updated-salad.jpg");
        assertThat(dto.getIngredients()).isNotNull();
        assertThat(dto.getIngredients()).hasSize(1);
        assertThat(dto.getIngredients().get(0).getAmount()).isEqualTo(3.0);
    }

    @Test
    void deleteRecipe_returns200_andRemovesFromDb() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto createBody = RecipeDto.builder()
                .name("Soup")
                .imageUrl("soup.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(recipesBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);
        Long id = recipes.get(0).getId();

        HttpEntity<Void> deleteReq = new HttpEntity<>(headers);

        ResponseEntity<Void> deleteResp = rest.exchange(
                recipesBase + "/{id}",
                HttpMethod.DELETE,
                deleteReq,
                Void.class,
                id
        );
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<RecipeDto>> afterDeleteResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(afterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> after = afterDeleteResp.getBody();
        assertThat(after).isNotNull();
        assertThat(after).isEmpty();
    }

    @Test
    void userCannotGetRecipeOfAnotherUser_returns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto createBody = RecipeDto.builder()
                .name("Private recipe")
                .imageUrl("private.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(recipesBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);
        Long id = recipes.get(0).getId();

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
                recipesBase + "/{id}",
                HttpMethod.GET,
                getReqAsOther,
                String.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminCannotGetRecipeOfAnotherUser_returns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RecipeIngredientDto ingredient = RecipeIngredientDto.builder()
                .productId(productId)
                .amount(1.0)
                .build();

        RecipeDto createBody = RecipeDto.builder()
                .name("User recipe")
                .imageUrl("user-recipe.jpg")
                .ingredients(List.of(ingredient))
                .build();

        HttpEntity<RecipeDto> createReq = new HttpEntity<>(createBody, headers);
        ResponseEntity<Void> createResp = rest.postForEntity(recipesBase, createReq, Void.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpEntity<Void> listReq = new HttpEntity<>(headers);
        ResponseEntity<List<RecipeDto>> listResp = rest.exchange(
                recipesBase,
                HttpMethod.GET,
                listReq,
                new ParameterizedTypeReference<List<RecipeDto>>() {}
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RecipeDto> recipes = listResp.getBody();
        assertThat(recipes).isNotNull();
        assertThat(recipes).hasSize(1);
        Long id = recipes.get(0).getId();

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
                recipesBase + "/{id}",
                HttpMethod.GET,
                getReqAsAdmin,
                String.class,
                id
        );

        assertThat(byIdResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
