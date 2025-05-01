package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import uk.project.shoppinglistwebapp.model.User;
import uk.project.shoppinglistwebapp.service.ShoppingListService;

@Route("")
@PermitAll // Allows any authenticated user to view this page
public class MainView extends VerticalLayout {

    private static final String LOGOUT_SUCCESS_URL = "/";

    public MainView(@Autowired ShoppingListService shoppingListService) {
        // Using the raw Spring Security API directly do access Google provided
        // credentials and doing logout. Check the GitHub example for a better basis
        // an actual application, where these details are refactored to a separate UserSession bean
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        String givenName = principal.getAttribute("given_name");
        String familyName = principal.getAttribute("family_name");
        String email = principal.getAttribute("email");
        String picture = principal.getAttribute("picture");

        // Only combines the names if familyName is not null
        String userName = (familyName != null) ? givenName + " " + familyName : givenName;

        // Obtains the existing User or creates a new one
        User currentUser = shoppingListService.getUsersByEmail(email, userName);

        H2 header = new H2("Id: " + currentUser.getId() + "\nName: " + currentUser.getName() + "\nEmail: " + currentUser.getEmail());
        Image image = new Image(picture, "User Image");

        // Logout Button
        Button logoutButton = new Button("Logout", click -> {
            UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        });

        setAlignItems(Alignment.CENTER);
        add(header, image, logoutButton);
    }
}