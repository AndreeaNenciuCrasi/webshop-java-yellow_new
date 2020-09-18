package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.datasource.dbConnection;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartDaoJDBC implements CartDao {
    private final DataSource dataSource = dbConnection.getInstance().getDataSource();
    //    ProductDao productDao = new ProductDaoJDBC.getInstance();
    private static CartDaoJDBC instance = null;

    public CartDaoJDBC() throws IOException, SQLException {

    }

    public static CartDaoJDBC getInstance() throws IOException, SQLException {
        if (instance == null) {
            instance = new CartDaoJDBC();
        }
        return instance;
    }


    @Override
    public int addNewCart(int user_id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO cart (user_id) VALUES (?)");
            preparedStatement.setInt(1, user_id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            int cartId = getIdCart(user_id);
            return cartId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int getIdCart(int userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM cart WHERE user_id=? ORDER BY date DESC LIMIT 1");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int cartId = resultSet.getInt("id");
            resultSet.close();
            preparedStatement.close();
            return cartId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Cart find(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cart WHERE id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                Cart cart = new Cart(userId);
                cart.setId(id);
                return cart;
            }
            resultSet.close();
            preparedStatement.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Cart findByUserId(int userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cart WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int cartId = resultSet.getInt("id");
                Cart cart = new Cart(userId);
                cart.setId(userId);
                return cart;
            }
            resultSet.close();
            preparedStatement.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void removeCart(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM cart WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void add(int id, int cartId) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            String sql = "INSERT INTO product_cart (cart_id, product_id) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, cartId);
            preparedStatement.setInt(2, id); //here it needs the id of the product
            preparedStatement.executeUpdate();

            preparedStatement.close();

            System.out.println("Product add id " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void remove(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product_cart WHERE cart_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void removeProduct(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product_cart WHERE product_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int get(int id, Integer cartId) throws SQLException {
        try (Connection connection = dataSource.getConnection();) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(product_id) AS quantity FROM product_cart WHERE product_id=? AND cart_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, cartId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt("quantity");
            }
            resultSet.close();
            preparedStatement.close();

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public void changeQuantity(int quantity, int id) throws SQLException {
    }

    @Override
    public List<Cart> getAll(Integer cartId) throws SQLException {
        List<Cart> templist = new ArrayList<>();
        if(cartId != null) {
            try (Connection connection = dataSource.getConnection();) {

                String sql = "SELECT product_id,p.name, p.image,  p.price, count(product_id), count(product_id) * p.price as total_price from product_cart\n" +
                        "JOIN product p on product_cart.product_id = p.id\n WHERE product_cart.cart_id=?" +
                        "group by p.price, product_id, p.name, p.image";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, cartId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    String productName = resultSet.getString("name");
                    String productimage = resultSet.getString("image");
                    float price = resultSet.getFloat("price");
                    int quantity = resultSet.getInt("count");
                    float total = resultSet.getFloat("total_price");

                    Cart cart = new Cart(productId, productName, productimage, price, quantity, total);
                    cart.setId(cartId);
                    templist.add(cart);

                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return templist;
    }

    @Override
    public Map<Product, Integer> getAllDaoMem() {
        return null;
    }

    public float productsTotalPrice(List<Cart> cartList){
        float sum = 0;
        for (int i = 0; i < cartList.size(); i++) {
            sum += cartList.get(i).getTotal();
        }
        return sum;
    }

    public int totalNumberOfProductsInCart(List<Cart> cartList){
        int numberOfProducts = 0;
        for (int i = 0; i < cartList.size(); i++) {
            numberOfProducts += cartList.get(i).getQuantity();
        }
        return numberOfProducts;
    }

}
