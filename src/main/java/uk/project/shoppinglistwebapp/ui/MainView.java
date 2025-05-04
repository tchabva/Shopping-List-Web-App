package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
    private final TextField itemTextField = new TextField();
    private final Button addItemButton = new Button("", new Icon(VaadinIcon.PLUS));
    private final Button clearAllButton = new Button("Clear All");
    private final Grid<ShoppingItem> grid = new Grid<>(ShoppingItem.class, false);
    private final Button logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));

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

        // Create a main content area with white background and shadow
        VerticalLayout contentArea = new VerticalLayout();
        contentArea.getElement().getStyle().set("background-color", "white");
        contentArea.getElement().getStyle().set("border-radius", "8px");
        contentArea.getElement().getStyle().set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)");
        contentArea.setMaxWidth("600px");
        contentArea.setWidth("100%");
        contentArea.setPadding(false);
        contentArea.setSpacing(false);

        // Implement functionality
        implementButtons();
        implementGrid();

        // Add Components to the content area
        contentArea.add(
                header(givenName),
                userProfileArea(picture),
                addItemRow(),
                grid,
                clearAllButtonLayout()
        );

        // Add the content area to the main layout and center it
        add(contentArea);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Set layout properties
        getElement().getStyle().set("background", "linear-gradient(to bottom right, #EFF6FF, #E0E7FF)");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Update the Grid List when the User is created or retrieved from the DB
        updateList();
    }

    private void updateList() {
        grid.setItems(shoppingListService.getShoppingItemsByUser(currentUser));
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
        addItemButton.getElement().getStyle().set("border-radius", "0 4px 4px 0");

        // Logout Button
        logoutButton.addClickListener(click -> {
            UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.getElement().getStyle().set("background-color", "#4338CA");
        logoutButton.getElement().getStyle().set("color", "white");
        logoutButton.getElement().getStyle().set("margin-left", "auto");
        logoutButton.setIconAfterText(true);

        // Clear All Button
        clearAllButton.addClickListener(click -> {
            shoppingListService.clearShoppingList(currentUser);
            updateList();
        });
        clearAllButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        clearAllButton.getElement().getStyle().set("font-weight", "500");
    }

    private void implementGrid() {
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setHeight("400px");

        // Item List Column
        grid.addColumn(ShoppingItem::getName)
                .setFlexGrow(1);

        // Delete Button Column
        grid.addColumn(new ComponentRenderer<>(item -> {
            Button deleteItemButton = new Button("", click -> {
                shoppingListService.deleteShoppingItem(item.getId());
                updateList();
            });
            deleteItemButton.setIcon(new Icon(VaadinIcon.TRASH));
            deleteItemButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return deleteItemButton;
        })).setFlexGrow(0).setWidth("80px");
    }

    private HorizontalLayout addItemRow() {
        HorizontalLayout addItemRow = new HorizontalLayout();
        addItemRow.setWidthFull();
        addItemRow.setPadding(true);
        addItemRow.getElement().getStyle().set("border-bottom", "1px solid #E5E7EB");

        // TextField Configuration
        itemTextField.setPlaceholder("Add new item...");
        itemTextField.setWidthFull();

        // Align the addItemButton with the itemTextField in addItemRow
        addItemRow.add(itemTextField, addItemButton);
        addItemRow.expand(itemTextField);
        addItemRow.setVerticalComponentAlignment(Alignment.CENTER, itemTextField, addItemButton);

        return addItemRow;
    }

    private HorizontalLayout header(String givenName) {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setSpacing(true);
        headerLayout.getElement().getStyle().set("background-color", "#4F46E5"); // Indigo color
        headerLayout.getElement().getStyle().set("padding", "16px");
        headerLayout.getElement().getStyle().set("border-radius", "8px 8px 0 0");

        // Shopping Cart Icon
        Icon shoppingIcon = VaadinIcon.CART.create();
        shoppingIcon.setColor("white");
        shoppingIcon.setSize("24px");

        // Header text
        H2 headerText = new H2((givenName != null) ? givenName.concat("'s Shopping List") : "Shopping List");
        headerText.getElement().getStyle().set("margin", "0");
        headerText.getElement().getStyle().set("color", "white");
        headerText.getElement().getStyle().set("font-size", "20px");

        headerLayout.add(shoppingIcon, headerText, logoutButton);
        headerLayout.setVerticalComponentAlignment(Alignment.CENTER, shoppingIcon, headerText, logoutButton);

        return headerLayout;
    }

    private Div userProfileArea(String pictureUrl) {
        Div profileContainer = new Div();
        profileContainer.setWidthFull();
        profileContainer.getElement().getStyle().set("background-color", "#EEF2FF"); // Light indigo background
        profileContainer.getElement().getStyle().set("padding", "16px");
        profileContainer.getElement().getStyle().set("display", "flex");
        profileContainer.getElement().getStyle().set("justify-content", "center");

        // User image
        Image image = new Image(pictureUrl, "User Image");
        image.setWidth("48px");
        image.setHeight("48px");
        image.getElement().getStyle().set("border-radius", "50%");
        image.getElement().getStyle().set("border", "2px solid #A5B4FC"); // Light indigo border

        profileContainer.add(image);
        return profileContainer;
    }

    private HorizontalLayout clearAllButtonLayout() {
        HorizontalLayout clearButtonLayout = new HorizontalLayout(clearAllButton);
        clearButtonLayout.setWidthFull();
        clearButtonLayout.setPadding(true);
        clearButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        clearButtonLayout.getElement().getStyle().set("background-color", "#F9FAFB");
        clearButtonLayout.getElement().getStyle().set("border-radius", "0 0 8px 8px");
        clearButtonLayout.getElement().getStyle().set("border-top", "1px solid #E5E7EB");

        return clearButtonLayout;
    }
}