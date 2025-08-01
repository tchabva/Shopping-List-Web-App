package uk.project.shoppinglistwebapp.service;

import uk.project.shoppinglistwebapp.model.ShoppingItem;
import uk.project.shoppinglistwebapp.model.User;

import java.util.List;

public interface ShoppingListService {
    List<ShoppingItem> getShoppingItemsByUser(User user);
    ShoppingItem addShoppingItem(String itemName, User user);
    void deleteShoppingItem(Long itemId);
    void clearShoppingList(User user);
    User getUserByEmail(String email, String name);
}