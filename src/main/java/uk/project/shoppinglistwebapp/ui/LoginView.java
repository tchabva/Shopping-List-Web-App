package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
        add(loginContainer());
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        // Background colour
        getElement().getStyle().set("background", "linear-gradient(to bottom right, #EFF6FF, #E0E7FF)");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private Anchor loginLink(){
        Anchor loginLink = new Anchor(OAUTH_URL, "Login with Google");
        loginLink.getElement().getStyle().set("background-color", "#4F46E5");
        loginLink.getElement().getStyle().set("color", "white");
        loginLink.getElement().getStyle().set("padding", "8px 16px");
        loginLink.getElement().getStyle().set("border-radius", "4px");
        loginLink.getElement().getStyle().set("text-decoration", "none");

        // Instruct Vaadin Router to ignore doing SPA handling
        loginLink.setRouterIgnore(true);
        return loginLink;
    }

    private VerticalLayout loginContainer(){
        // Header
        H1 header = new H1("Shopping List");
        header.getElement().getStyle().set("color", "#4F46E5");
        header.getElement().getStyle().set("margin-top", "8px");

        // Welcome text
        Paragraph welcomeText = new Paragraph("Welcome! Sign in to manage your shopping list");
        welcomeText.getElement().getStyle().set("text-align", "center");
        welcomeText.getElement().getStyle().set("color", "#6B7280");

        // Shopping Cart Icon
        Icon shoppingIcon = VaadinIcon.CART.create();
        shoppingIcon.setSize("50px");
        shoppingIcon.setColor("#4F46E5");

        // Styling the content container
        // Create a container for the login content
        VerticalLayout loginContainer = new VerticalLayout();
        loginContainer.setAlignItems(Alignment.CENTER);
        loginContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        loginContainer.setPadding(true);
        loginContainer.setSpacing(true);
        loginContainer.setMaxWidth("400px");
        loginContainer.setHeight("auto");

        // Style the container
        loginContainer.getElement().getStyle().set("background-color", "white");
        loginContainer.getElement().getStyle().set("border-radius", "8px");
        loginContainer.getElement().getStyle().set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)");

        // Add all components to the container
        loginContainer.add(shoppingIcon, header, welcomeText, loginLink());

        return loginContainer;
    }
}