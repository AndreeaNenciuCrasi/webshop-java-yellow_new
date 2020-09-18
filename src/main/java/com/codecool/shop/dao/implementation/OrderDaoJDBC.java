package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.Order;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDaoJDBC implements OrderDao {
    private final DataSource dataSource = dbConnection.getInstance().getDataSource();

    private static OrderDaoJDBC instance = null;

    public OrderDaoJDBC() throws IOException, SQLException {

    }

    public static OrderDaoJDBC getInstance() throws IOException, SQLException {
        if (instance == null) {
            instance = new OrderDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(Order order) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            String sql = "INSERT INTO orders (cart_id, user_id, status, total_price) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,  order.getCartId());
            preparedStatement.setInt(2, order.getUserId());
            preparedStatement.setString(3, order.getStatus());
            preparedStatement.setFloat(4, order.getTotal());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}