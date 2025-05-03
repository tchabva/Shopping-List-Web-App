package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import uk.project.shoppinglistwebapp.model.ShoppingItem;
import uk.project.shoppinglistwebapp.model.User;
import uk.project.shoppinglistwebapp.service.ShoppingListService;

@Route("/shopping-list")
@PageTitle("Shopping List")
@PermitAll // Allows any authenticated user to view this page
public class MainView extends VerticalLayout {

    private static final String LOGOUT_SUCCESS_URL = "/";

    // UI Components
    private final TextField itemTextField = new TextField("Item");
    private final Button addItemButton = new Button("Add Item");
    private final Button clearAllButton = new Button("Clear List");
    private final Grid<ShoppingItem> grid = new Grid<>(ShoppingItem.class, false);
    private final Button logoutButton = new Button("Logout");

    private final User currentUser;
    private final ShoppingListService shoppingListService;

    public MainView(@Autowired ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
        // Obtaining OAuth User (Principal)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        String givenName = principal.getAttribute("given_name");
        String familyName = principal.getAttribute("family_name");
        String email = principal.getAttribute("email");
        String picture = principal.getAttribute("picture");

        // Only combines the names if familyName is not null
        String userName = (familyName != null) ? givenName + " " + familyName : givenName;

        // Obtains the existing User or creates a new one
        currentUser = shoppingListService.getUserByEmail(email, userName);

        Image image = new Image(picture, "User Image");

        implementButtons(); // Invokes the Button ClickLister implementations

        implementGrid(); // Invokes the Grid implementation for the Shopping List

        setAlignItems(Alignment.CENTER); // Align the items in the view

        // Add Components to the layout
        add(
                header(givenName),
                image,
                addItemRow(),
                grid,
                clearAllButton
        );

        // Set layout properties
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        updateList(); // Update the Grid List when the User is created or retrieved from the DB
    }

    private void updateList() {
        grid.setItems(shoppingListService.getShoppingItemsByUser(currentUser.getEmail()));
    }

    private void implementButtons() {
        // Add Item Button
        addItemButton.addClickListener(click -> {
            if (!itemTextField.getValue().isBlank()) {
                shoppingListService.addShoppingItem(itemTextField.getValue(), currentUser);
                itemTextField.clear();
                updateList();
            }
        });

        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Logout Button
        logoutButton.addClickListener(click -> {
            UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });

        logoutButton.addThemeVariants(ButtonVariant.LUMO_WARNING);

        // Clear All Button
        clearAllButton.addClickListener(click -> {
            shoppingListService.clearShoppingList(currentUser);
            updateList();
        });
        clearAllButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearAllButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
    }

    private void implementGrid() {
        // Item List Column
        grid.addColumn(ShoppingItem::getName)
                .setHeader(new H3("Shopping List"))
                .setTextAlign(ColumnTextAlign.CENTER);

        // Delete Button Column
        grid.addColumn(new ComponentRenderer<>(item -> {
            Button deleteItemButton = new Button("Delete", click -> {
                shoppingListService.deleteShoppingItem(item.getId());
                updateList();
            });
            deleteItemButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return deleteItemButton;
        })).setFlexGrow(0);
    }

    private HorizontalLayout addItemRow() {
        HorizontalLayout addItemRow = new HorizontalLayout(itemTextField, addItemButton);

        // Align the addItemButton with the itemTextField in addItemRow
        addItemRow.setVerticalComponentAlignment(Alignment.END, addItemButton);
        return addItemRow;
    }

    private HorizontalLayout header(String givenName) {
        H2 headerText = new H2((givenName != null) ? givenName.concat("'s Shopping List") : "Shopping List");
        return new HorizontalLayout(headerText, logoutButton);
    }
}