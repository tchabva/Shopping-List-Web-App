package uk.project.shoppinglistwebapp.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
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
        H1 header = new H1("Shopping List");
        header.getElement().getStyle().set("color", "#4F46E5");

        Icon shoppingIcon = VaadinIcon.CART.create();
        shoppingIcon.setSize("50px");
        shoppingIcon.setColor("#4F46E5");

        Anchor loginLink = new Anchor(OAUTH_URL, "Login with Google");
        loginLink.getElement().getStyle().set("background-color", "#4F46E5");
        loginLink.getElement().getStyle().set("color", "white");
        loginLink.getElement().getStyle().set("padding", "8px 16px");
        loginLink.getElement().getStyle().set("border-radius", "4px");
        loginLink.getElement().getStyle().set("text-decoration", "none");

        // Instruct Vaadin Router to ignore doing SPA handling
        loginLink.setRouterIgnore(true);

        // Styling the content container
        VerticalLayout content = new VerticalLayout(shoppingIcon, header, loginLink);
        content.setSpacing(true);
        content.setPadding(true);
        content.setAlignItems(Alignment.CENTER);
        content.getElement().getStyle().set("background-color", "white");
        content.getElement().getStyle().set("border-radius", "8px");
        content.getElement().getStyle().set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)");
        content.setMaxWidth("400px");
        content.setHeight("auto");

        add(content);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        // Background colour
        getElement().getStyle().set("background", "linear-gradient(to bottom right, #EFF6FF, #E0E7FF)");
    }
}