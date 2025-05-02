package uk.project.shoppinglistwebapp.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.project.shoppinglistwebapp.repository.ShoppingItemRepository;
import uk.project.shoppinglistwebapp.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShoppingListServiceTest {

    @Mock
    private ShoppingItemRepository mockShoppingItemRepository;

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;
}