package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    // URL that Spring Security uses to connect to Google services
    private static final String OAUTH_URL = "/oauth2/authorization/google";

    public LoginView() {
        H1 header = new H1("Shopping List");
        Anchor loginLink = new Anchor(OAUTH_URL, "Login with Google");
        // Instruct Vaadin Router to ignore doing SPA handling
        loginLink.setRouterIgnore(true);
        add(
                header,
                loginLink
        );
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        // Background colour
        getElement().getStyle().set("background", "linear-gradient(to bottom right, #EFF6FF, #E0E7FF)");
    }
}