package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.UserDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBC implements UserDao {
    DataSource dataSource = dbConnection.getInstance().getDataSource();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<User> users = new ArrayList<>();
    private static UserDaoJDBC instance = null;

    public UserDaoJDBC() throws SQLException, IOException {
    }

    public static UserDaoJDBC getInstance() throws SQLException, IOException {
        if (instance == null) {
            instance = new UserDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, first_name, last_name, phone_number, email, billing_address, shipping_address) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getEmail());
            preparedStatement.setString(7, user.getBillingAddress());
            preparedStatement.setString(8, user.getShippingAddress());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public User find(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String username =resultSet.getString("username");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String billingAddress = resultSet.getString("billing_address");
                String shippingAddress = resultSet.getString("shipping_address");
                User user = new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);
                user.setId(id);
                return user;
            }
            resultSet.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public User find(String username) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
//                String username = username;
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String billingAddress = resultSet.getString("billing_address");
                String shippingAddress = resultSet.getString("shipping_address");
                User user = new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);

                return user;
            }
            resultSet.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void remove(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void update(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("UPDATE users SET phone_number = ? , billing_address = ?, shipping_address = ? WHERE username=?");
            preparedStatement.setString(1,user.getPhone());
            preparedStatement.setString(2,user.getBillingAddress());
            preparedStatement.setString(3,user.getShippingAddress());
            preparedStatement.setString(4,user.getUsername());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        users.clear();
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("SELECT * FROM users");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String username =resultSet.getString("username");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String billingAddress = resultSet.getString("billing_address");
                String shippingAddress = resultSet.getString("shipping_address");
                User user =new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);
                user.setId(id);
                users.add(user);
            }
            resultSet.close();
            preparedStatement.close();
            return users;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String getPasswordByUsername(String username) throws SQLException {
        String password = "";
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                password = resultSet.getString("password");
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return password;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        try (Connection connection = dataSource.getConnection();) {

            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phone = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String billingAddress = resultSet.getString("billing_address");
                String shippingAddress = resultSet.getString("shipping_address");
                user = new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);
                user.setId(id);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void edit(User user) throws SQLException {

        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET password = ?, first_name = ?, last_name = ?, email = ?, phone_number = ?, billing_address = ?, shipping_address = ? WHERE username = ?");
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getBillingAddress());
            preparedStatement.setString(7, user.getShippingAddress());
            preparedStatement.setString(8, user.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
