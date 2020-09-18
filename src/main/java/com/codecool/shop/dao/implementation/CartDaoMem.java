package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CartDaoMem implements CartDao {
    private HashMap<Product, Integer> cart = new HashMap<>();

    /* A private Constructor prevents any other class from instantiating.
     */

    @Override
    public void add(int id, int cartId) throws IOException, SQLException {
        ProductDaoJDBC productsList = ProductDaoJDBC.getInstance();
        Product product = productsList.find(id);
            if(cart.containsKey(product)){
                cart.put(product, cart.get(product) + 1);
            }else{
                cart.put(product, 1);
            }
    }

    @Override
    public void remove(int id) {
        ProductDaoMem productsList = ProductDaoMem.getInstance();
        Product product = productsList.find(id);
        if(cart.containsKey(product)) {
            if(cart.get(product) == 1) {
                cart.remove(product);
            } else {
                cart.put(product, cart.get(product) - 1);
            }
        }
    }

    public void deleteCart(){
        this.cart = new HashMap<>();
    }

    public void changeQuantity(int id, int newQuantity) throws IOException, SQLException {
        ProductDao productList = ProductDaoJDBC.getInstance();
        Product product = null;
        try {
            product = productList.find(id);
        }catch(SQLException e){
            e.printStackTrace();
        }
        if(cart.containsKey(product)) {
            if (newQuantity == 0) {
                cart.remove(product);
            } else {
                cart.put(product, newQuantity);
            }
        }
    }

    @Override
    public List<Cart> getAll(Integer cartId) throws SQLException {
        return null;
    }

    @Override
    public Map<Product, Integer> getAllDaoMem() {
        return this.cart;
    }

    @Override
    public Cart find(int id) throws SQLException {
        return null;
    }

    @Override
    public void removeProduct(int id) throws SQLException {

    }

    @Override
    public int get(int id, Integer cartId) throws SQLException {
        return 0;
    }

    @Override
    public int addNewCart(int user_id) throws SQLException{
    return 0;
    }

    @Override
    public Cart findByUserId(int userId) throws SQLException {
        return null;
    }

    @Override
    public void removeCart(int id) throws SQLException {

    }


    public float productsTotalPrice(CartDao cartDao){
        Map<Product, Integer> cartMap = cartDao.getAllDaoMem();
        float sum = 0;
        for (Map.Entry<Product, Integer> entry : cartMap.entrySet()) {
            sum += entry.getKey().getPriceDouble() * entry.getValue();
        }
        return sum;
    }

    public int totalNumberOfProductsInCart(CartDao cartDao){
        Map<Product, Integer> cartMap = cartDao.getAllDaoMem();
        int numberOfProducts = 0;
        for (Map.Entry<Product, Integer> entry : cartMap.entrySet()) {
            numberOfProducts += entry.getValue();
        }
        return numberOfProducts;
    }
}
