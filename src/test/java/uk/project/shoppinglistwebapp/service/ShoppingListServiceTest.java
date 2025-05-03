package uk.project.shoppinglistwebapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.project.shoppinglistwebapp.model.ShoppingItem;
import uk.project.shoppinglistwebapp.model.User;
import uk.project.shoppinglistwebapp.repository.ShoppingItemRepository;
import uk.project.shoppinglistwebapp.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
class ShoppingListServiceTest {

    @Mock
    private ShoppingItemRepository mockShoppingItemRepository;

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    @Test
    @DisplayName("Returns a user when provided with an email")
    void testGetUser() {
        // Arrange
        String email = "user@test.com";
        String name = "user";
        User user = new User(email, name);

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = shoppingListService.getUserByEmail(email, name);

        // Assert
        assertAll("Confirms the returned User has the same email and name",
                () -> assertThat(result.getEmail()).isEqualTo(email),
                () -> assertThat(result.getName()).isEqualTo(name)
        );
    }

    @Test
    @DisplayName("Returns a user when provided with an email not in the database")
    void testSaveUser() {
        // Arrange
        String email = "user@test.com";
        String name = "user";
        User user = new User(email, name);

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(mockUserRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = shoppingListService.getUserByEmail(email, name);

        // Assert
        assertAll("Confirms the returned User has the same email and name as the new saved item",
                () -> assertThat(result.getEmail()).isEqualTo(email),
                () -> assertThat(result.getName()).isEqualTo(name)
        );
    }

    @Test
    @DisplayName("Returns a ShoppingItem when provided with an itemName and user")
    void testSaveShoppingItem() {
        // Arrange
        String itemName = "apple";
        String email = "user@test.com";
        String userName = "user";
        User user = new User(email, userName);
        ShoppingItem item = new ShoppingItem(itemName, user);

        when(mockShoppingItemRepository.save(any(ShoppingItem.class))).thenReturn(item);

        // Act
        ShoppingItem result = shoppingListService.addShoppingItem(itemName, user);

        // Assert
        assertAll("Confirms the returned ShoppingItem has the same, itemName, email and userName",
                () -> assertThat(result.getName()).isEqualTo(itemName),
                () -> assertThat(result.getUser().getName()).isEqualTo(userName),
                () -> assertThat(result.getUser().getEmail()).isEqualTo(email)
        );
    }

    @Test
    @DisplayName("Deletes a item from the DB when provided an itemId")
    void testDeleteShoppingItem() {
        // Arrange
        String itemName = "apple";
        String email = "user@test.com";
        String userName = "user";
        User user = new User(email, userName);
        ShoppingItem item = new ShoppingItem(0L, itemName, user);

        when(mockShoppingItemRepository.findById(0L)).thenReturn(Optional.of(item));

        // Act
        shoppingListService.deleteShoppingItem(item.getId());
        Boolean result = mockShoppingItemRepository.existsById(item.getId());

        // Assert
        assertThat(result).isEqualTo(false);
    }
}