package uk.project.shoppinglistwebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.project.shoppinglistwebapp.model.ShoppingItem;
import uk.project.shoppinglistwebapp.model.User;
import uk.project.shoppinglistwebapp.repository.ShoppingItemRepository;
import uk.project.shoppinglistwebapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUsersByEmail(String email, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        } else {
            return userRepository.save(new User(email, name));
        }
    }

    @Transactional
    @Override
    public void clearShoppingList(User user) {
        shoppingItemRepository.deleteAllByUser(user);
    }

    @Override
    public void deleteShoppingItem(Long itemId) {
        shoppingItemRepository.deleteById(itemId);
    }

    @Override
    public ShoppingItem addShoppingItem(String itemName, User user) {
        ShoppingItem shoppingItem = new ShoppingItem(itemName, user);
        return shoppingItemRepository.save(shoppingItem);
    }

    @Override
    public List<ShoppingItem> getShoppingItemsByUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).get();
            return shoppingItemRepository.findByUser(user);
        } else return new ArrayList<>();
    }
}