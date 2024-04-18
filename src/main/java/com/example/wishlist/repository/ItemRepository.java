package com.example.wishlist.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.example.wishlist.model.Item;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.List;

@Repository
public class ItemRepository {
    @Value("${spring.datasource.url}")
    private String db_url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    private final JdbcTemplate jdbcTemplate;
    public ItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
// lol
    public int addItemToWishlist(int wishlistId, Item item) {
        String sql = "INSERT INTO wishlistItems (wishlistid, itemname, description, price) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"itemId"});
            ps.setInt(1, wishlistId);
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setInt(4, item.getPrice());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new RuntimeException("No key found after insert");
        }

        return keyHolder.getKey().intValue();
    }

    public void removeItemFromWishlist(int wishlistId, String name) {
        String sql = "DELETE FROM WishlistItems WHERE wishlistID = ? AND itemName = ?";
        jdbcTemplate.update(sql, wishlistId, name);
    }

    public void editItemInWishlist(int wishlistId, int itemId, Item newItem) {
        String sql = "UPDATE wishlistitems SET itemname = ?, description = ?, price = ? WHERE wishlistid = ? AND itemId = ?";
        jdbcTemplate.update(sql, newItem.getName(), newItem.getDescription(), newItem.getPrice(), wishlistId, itemId);
    }

    public List<Item> getItemsByWishlistId(int wishlistId) {
        String sql = "SELECT * FROM wishlistItems WHERE wishlistId = ?";
        return jdbcTemplate.query(sql, new Object[]{wishlistId}, (resultSet, rowNum) ->
                new Item(resultSet.getInt("itemId"),
                        resultSet.getString("itemname"),
                        resultSet.getString("description"),
                        resultSet.getInt("price"))
        );
    }
    public Item findItemByName(String itemName) {
        String query = "SELECT * FROM Items WHERE ItemName = ?";
        try (Connection connection = DriverManager.getConnection(db_url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Item item = new Item(resultSet.getInt("ItemId"),
                            resultSet.getString("ItemName"),
                            resultSet.getString("description"),
                            resultSet.getInt("price"));
                    return item;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getWishlistIdByItemName(String itemName) {
        String sql = "SELECT wishlistId FROM wishlistItems WHERE itemName = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{itemName}, Integer.class);
    }



    public Item getItemById(int itemId) {
        String query = "SELECT * FROM WishlistItems WHERE ItemId = ?";

        try (Connection connection = DriverManager.getConnection(db_url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, itemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("ItemName");
                String description = resultSet.getString("Description");
                int price = resultSet.getInt("price");

                return new Item(itemId,name, description, price);
            } else {
                return null; // Item not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Handle the exception
        }
    }



}
