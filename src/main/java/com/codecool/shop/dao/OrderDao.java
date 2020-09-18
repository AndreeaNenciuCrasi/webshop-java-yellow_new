package com.codecool.shop.dao;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.model.User;
import java.sql.SQLException;
import java.util.List;

import java.sql.SQLException;

public interface OrderDao {
    void add(Order order) throws SQLException;

}
