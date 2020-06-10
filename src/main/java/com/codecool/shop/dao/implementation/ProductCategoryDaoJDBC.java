package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDaoJDBC implements ProductCategoryDao {
    DataSource dataSource;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<ProductCategory> products = new ArrayList<>();
    private static ProductCategoryDaoJDBC instance = null;

    public ProductCategoryDaoJDBC() {
    }

    public static ProductCategoryDaoJDBC getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(ProductCategory category) {
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO category (name, department, description) VALUES (?,?,?)");
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDepartment());
            preparedStatement.setString(3, category.getDescription());
            preparedStatement.executeQuery();
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
        }
    }

    @Override
    public ProductCategory find(int id) {
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM category WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String department = resultSet.getString("department");
                String description = resultSet.getString("description");
                ProductCategory productCategory = new ProductCategory(name, department, description);
                productCategory.setId(id);
                return productCategory;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public void remove(int id) {
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM category WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
        }

    }

    @Override
    public List<ProductCategory> getAll() {
        try {
            products.clear();
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM supplier");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String department = resultSet.getString("department");
                String description = resultSet.getString("description");
                ProductCategory productCategory = new ProductCategory(name, department, description);
                productCategory.setId(id);
                products.add(productCategory);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return products;
        } catch (SQLException e) {
        }
        return null;
    }
}
