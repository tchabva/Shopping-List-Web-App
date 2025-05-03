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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("Retrieves all the shopping items from a list by user")
    void testGetShoppingItemsByUser() {

        // Arrange
        String email = "user@test.com";
        String userName = "user";
        User user = new User(email, userName);
        List<ShoppingItem> itemList = List.of(
                new ShoppingItem(0L, "item1", user),
                new ShoppingItem(0L, "item2", user),
                new ShoppingItem(0L, "item3", user),
                new ShoppingItem(0L, "item4", user)
        );

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockShoppingItemRepository.findByUser(user)).thenReturn(itemList);

        // Act
        List<ShoppingItem> result = shoppingListService.getShoppingItemsByUser(user);

        // Assert
        assertAll("Confirms the returned that the return list of items matches the saved list",
                () -> assertThat(result.size()).isEqualTo(4),
                () -> assertThat(result).isEqualTo(itemList)
        );
    }

    @Test
    @DisplayName("Returns an Empty List when the user does not have any items in the DB")
    void testGetShoppingItemsByUserWithoutItems() {

        // Arrange
        String email = "user@test.com";
        String userName = "user";
        User user = new User(email, userName);

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        List<ShoppingItem> result = shoppingListService.getShoppingItemsByUser(user);

        // Assert
        assertAll("Confirms the returned that the return list of items is empty",
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isEqualTo(List.of())
        );
    }

    @Test
    @DisplayName("Clears all of the ShoppingItems associated with a user")
    void testClearShoppingList() {

        // Arrange
        String email = "user@test.com";
        String userName = "user";
        User user = new User(email, userName);
        List<ShoppingItem> itemList = List.of(
                new ShoppingItem(0L, "item1", user),
                new ShoppingItem(0L, "item2", user),
                new ShoppingItem(0L, "item3", user),
                new ShoppingItem(0L, "item4", user)
        );
        List<ShoppingItem> emptyList = List.of();

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockShoppingItemRepository.findByUser(user))
                .thenReturn(itemList) // First call returns the itemList
                .thenReturn(emptyList); // Second call returns the emptyList

        // Verify the items exist
        List<ShoppingItem> preMethodCall = shoppingListService.getShoppingItemsByUser(user);
        assertAll("Confirms that the list of items of exist",
                () -> assertThat(preMethodCall.size()).isEqualTo(4),
                () -> assertThat(preMethodCall).isEqualTo(itemList)
        );

        // Act
        shoppingListService.clearShoppingList(user);
        List<ShoppingItem> result = shoppingListService.getShoppingItemsByUser(user);

        // Assert
        assertAll("Confirms the returned that the return list of items is empty",
                () -> assertThat(result.size()).isEqualTo(0),
                () -> assertThat(result).isEqualTo(List.of())
        );

        // Verify the deleteAllByUser method was called
        verify(mockShoppingItemRepository).deleteAllByUser(user);
    }
}