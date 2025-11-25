package org.l5g7.mealcraft.app.shoppingitem;

import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.RecipeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingItemServiceImpl implements ShoppingItemService {

    private final ShoppingItemRepository shoppingItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private static final String ENTITY_NAME_PRODUCT = "Product";

    @Autowired
    public ShoppingItemServiceImpl(ShoppingItemRepository shoppingItemRepository, ProductRepository productRepository, UserRepository userRepository, RecipeProvider recipeProvider) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ShoppingItemDto> getAllShoppingItems() {
        List<ShoppingItem> entities = shoppingItemRepository.findAll();

        return entities.stream().map(entity -> ShoppingItemDto.builder()
                    .id(entity.getId())
                    .name(entity.getProduct().getName())
                    .userOwnerId(entity.getUserOwner().getId())
                    .productId(entity.getProduct().getId())
                    .requiredQty(entity.getRequiredQty())
                    .status(entity.getStatus())
                    .unitName(entity.getProduct().getDefaultUnit().getName())
                    .build()).toList();
    }

    @Override
    public List<ShoppingItemDto> getUserShoppingItems(Long userId) {
        List<ShoppingItem> entities = shoppingItemRepository.findByUserOwnerId(userId);

        return entities.stream().map(entity -> ShoppingItemDto.builder()
                    .id(entity.getId())
                    .name(entity.getProduct().getName())
                    .userOwnerId(entity.getUserOwner().getId())
                    .productId(entity.getProduct().getId())
                    .requiredQty(entity.getRequiredQty())
                    .status(entity.getStatus())
                    .unitName(entity.getProduct().getDefaultUnit().getName())
                    .build()).toList();
    }

    @Override
    public ShoppingItemDto getShoppingItemById(Long id) {
        Optional<ShoppingItem> shoppingItem = shoppingItemRepository.findById(id);

        if (shoppingItem.isPresent()) {
            ShoppingItem entity = shoppingItem.get();

            return new ShoppingItemDto(
                    entity.getId(),
                    entity.getProduct().getName(),
                    entity.getUserOwner().getId(),
                    entity.getProduct().getId(),
                    entity.getRequiredQty(),
                    entity.getStatus(),
                    entity.getProduct().getDefaultUnit().getName(),
                    null
            );
        } else {
            throw new EntityDoesNotExistException("ShoppingItem", String.valueOf(id));
        }
    }

    @Override
    public void createShoppingItem(ShoppingItemDto shoppingItemDto) {
        User user = userRepository.findById(shoppingItemDto.getUserOwnerId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(shoppingItemDto.getUserOwnerId())));
        Product product = productRepository.findById(shoppingItemDto.getProductId())
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME_PRODUCT, String.valueOf(shoppingItemDto.getProductId())));


        ShoppingItem entity = ShoppingItem.builder()
                .id(shoppingItemDto.getId())
                .userOwner(user)
                .product(product)
                .requiredQty(shoppingItemDto.getRequiredQty())
                .status(shoppingItemDto.getStatus())
                .boughtAt(shoppingItemDto.getBoughtAt())
                .build();

        shoppingItemRepository.save(entity);

    }

    @Override
    public void updateShoppingItem(Long id, ShoppingItemDto shoppingItemDto) {
        Optional<ShoppingItem> existing = shoppingItemRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("ShoppingItem", String.valueOf(id));
        }
        User user = userRepository.findById(shoppingItemDto.getUserOwnerId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(shoppingItemDto.getUserOwnerId())));
        Product product = productRepository.findById(shoppingItemDto.getProductId())
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME_PRODUCT, String.valueOf(shoppingItemDto.getProductId())));

        existing.ifPresent(shoppingItem -> {
            shoppingItem.setProduct(product);
            shoppingItem.setUserOwner(user);
            shoppingItem.setRequiredQty(shoppingItemDto.getRequiredQty());
            shoppingItem.setStatus(shoppingItemDto.getStatus());
            shoppingItemRepository.save(shoppingItem);
        });
    }

    @Override
    public void patchShoppingItem(Long id, ShoppingItemDto patch) {
        Optional<ShoppingItem> existing = shoppingItemRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("ShoppingItem", String.valueOf(id));
        }
        existing.ifPresent(shoppingItem -> {
            if (patch.getRequiredQty() != null) {
                shoppingItem.setRequiredQty(patch.getRequiredQty());
            }
            if (patch.getStatus() != null) {
                shoppingItem.setStatus(patch.getStatus());
            }
            if (patch.getProductId() != null) {
                shoppingItem.setProduct(productRepository.findById(patch.getProductId())
                        .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME_PRODUCT, String.valueOf(patch.getProductId()))));
            }
            if (patch.getUserOwnerId() != null) {
                shoppingItem.setUserOwner(userRepository.findById(patch.getUserOwnerId())
                        .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(patch.getUserOwnerId()))));
            }
            if (patch.getBoughtAt() != null) {
                shoppingItem.setBoughtAt(patch.getBoughtAt());
            }
            shoppingItemRepository.save(shoppingItem);
        });
    }

    @Override
    public void deleteShoppingItemById(Long id) {
        shoppingItemRepository.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id) {
        Optional<ShoppingItem> existing = shoppingItemRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("ShoppingItem", String.valueOf(id));
        }
        existing.ifPresent(shoppingItem -> {
            shoppingItem.setStatus(!shoppingItem.getStatus());
            shoppingItem.setBoughtAt(new Date(System.currentTimeMillis()));
            shoppingItemRepository.save(shoppingItem);
        });
    }

}