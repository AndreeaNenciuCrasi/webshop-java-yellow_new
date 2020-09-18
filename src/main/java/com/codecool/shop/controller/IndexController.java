package com.codecool.shop.controller;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.ProductCategory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/"})
public class IndexController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CartDaoJDBC cartDao = null;
        int numberOfProducts = 0;
        List<Cart> templist = new ArrayList<>();
        String sessionUsername = null;

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        HttpSession session = req.getSession(false);

        if (session != null) {
            sessionUsername = (String) session.getAttribute("username");
        }

        try {
            cartDao = CartDaoJDBC.getInstance();
            templist = cartDao.getAll((Integer) req.getSession().getAttribute("cartId"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (req.getSession().getAttribute("username") != null) {
            numberOfProducts = cartDao.totalNumberOfProductsInCart(templist);
        } else {
            CartDaoMem cart = (CartDaoMem) req.getSession().getAttribute("cartMem");
            if (cart != null) {
                numberOfProducts = cart.totalNumberOfProductsInCart(cart);
            }
        }

        context.setVariable("totalNumberOfItems", numberOfProducts);
        context.setVariable("username", sessionUsername);

        engine.process("index.html", context, resp.getWriter());
    }

}
