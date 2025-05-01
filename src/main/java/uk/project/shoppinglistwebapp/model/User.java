package uk.project.shoppinglistwebapp.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    Long id;

    private String email;

    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingItem> shoppingItems = new ArrayList<>();

    // Default constructor
    public User() {}

    // Constructor with Params
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShoppingItem> getShoppingItems() {
        return shoppingItems;
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    // Helper Methods
    public void addShoppingItem(ShoppingItem item){
        shoppingItems.add(item);
        item.setUser(this);
    }

    public void removeShoppingItem(ShoppingItem item){
        shoppingItems.remove(item);
        item.setUser(null);
    }

    public void clearShoppingItems(){
        shoppingItems.clear();
    }
}