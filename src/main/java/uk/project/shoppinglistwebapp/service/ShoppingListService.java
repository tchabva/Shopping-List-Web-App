package uk.project.shoppinglistwebapp.service;

import uk.project.shoppinglistwebapp.model.ShoppingItem;
import uk.project.shoppinglistwebapp.model.User;

import java.util.List;

public interface ShoppingListService {
    List<ShoppingItem> getShoppingItemsByUser(String email);
    ShoppingItem addShoppingItem(String itemName, User user);
    void deleteShoppingItem(Long itemId);
    void clearShoppingList(User user);
    User getUsersByEmail(String email, String name);
}