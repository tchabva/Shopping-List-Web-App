package uk.project.shoppinglistwebapp.service;

import org.assertj.core.api.Assertions;
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
        User result = shoppingListService.getUsersByEmail(email, name);

        // Assert
        assertAll("Confirms the returned User has the same email and name",
                () -> Assertions.assertThat(result.getEmail()).isEqualTo(email),
                () -> Assertions.assertThat(result.getName()).isEqualTo(name)
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
        User result = shoppingListService.getUsersByEmail(email, name);

        // Assert
        assertAll("Confirms the returned User has the same email and name",
                () -> Assertions.assertThat(result.getEmail()).isEqualTo(email),
                () -> Assertions.assertThat(result.getName()).isEqualTo(name)
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
        assertAll("Confirms the returned User has the same email and name",
                () -> Assertions.assertThat(result.getName()).isEqualTo(itemName),
                () -> Assertions.assertThat(result.getUser().getName()).isEqualTo(userName),
                () -> Assertions.assertThat(result.getUser().getEmail()).isEqualTo(email)
        );
    }
}