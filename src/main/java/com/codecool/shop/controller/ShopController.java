package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.Cart;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/shop"})
public class ShopController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDataStore = null;
        ProductCategoryDao productCategoryDataStore = null;
        SupplierDao supplier = null;
        CartDaoJDBC cartDao = null;
        int numberOfProducts = 0;
        List<Cart> templist = new ArrayList<>();
        int categoryId = 2;
        int supplierId = 3;
        String sessionUsername = null;

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        //get session if it exists
        HttpSession session = req.getSession(false);

        if (session != null) {
            sessionUsername = (String) session.getAttribute("username");
        }

        try {
            productDataStore = ProductDaoJDBC.getInstance();
            productCategoryDataStore = ProductCategoryDaoJDBC.getInstance();
            supplier = SupplierDaoJDBC.getInstance();
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


        if (req.getParameter("category") != null) {
            categoryId = Integer.parseInt(req.getParameter("category"));
            supplierId = 0;
        }

        if (req.getParameter("supplier") != null) {
            supplierId = Integer.parseInt(req.getParameter("supplier"));

        }

        context.setVariable("totalNumberOfItems", numberOfProducts);
        context.setVariable("username", sessionUsername);
        displayProducts(context, engine, resp, productCategoryDataStore, productDataStore, categoryId, supplierId, supplier);

    }


    private void displayProducts(WebContext context, TemplateEngine engine, HttpServletResponse resp, ProductCategoryDao productCategoryDataStore, ProductDao productDataStore, int categoryId, int supplierId, SupplierDao supplier) throws IOException {
        try {
            context.setVariable("category", productCategoryDataStore.find(categoryId));
        } catch (Exception exc) {
            if (exc instanceof IOException || exc instanceof SQLException) {
                exc.printStackTrace();
            }
        }

        if (supplierId != 0) {
            try {
                context.setVariable("supplier", supplier.find(supplierId));
            } catch (Exception exc) {
                if (exc instanceof IOException || exc instanceof SQLException) {
                    exc.printStackTrace();
                }
            }
        }
        try {
            context.setVariable("products", productDataStore.getBy(productCategoryDataStore.find(categoryId)));
        } catch (Exception exc) {
            if (exc instanceof IOException || exc instanceof SQLException) {
                exc.printStackTrace();
            }
        }
        if (supplierId != 0) {
            try {
                context.setVariable("products", productDataStore.getBy(supplier.find(supplierId)));
            } catch (Exception exc) {
                if (exc instanceof IOException || exc instanceof SQLException) {
                    exc.printStackTrace();
                }
            }
        }
        engine.process("shop.html", context, resp.getWriter());
    }





}