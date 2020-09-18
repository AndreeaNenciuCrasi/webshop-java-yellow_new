package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.UserDao;
import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/cart"})
public class CartController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        CartDaoJDBC cartDao = null;
        float sum = 0;
        int numberOfProducts = 0;
        String sessionUsername = null;

        //get session if it exists
        HttpSession session = req.getSession(false);

        if (session != null) {
            sessionUsername = (String) session.getAttribute("username");
        }

        try {
            cartDao = CartDaoJDBC.getInstance();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (req.getSession().getAttribute("username") != null) {
            List<Cart> templist = new ArrayList<>();
            try {

                templist = cartDao.getAll((Integer) req.getSession().getAttribute("cartId"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            sum = cartDao.productsTotalPrice(templist);
            numberOfProducts = cartDao.totalNumberOfProductsInCart(templist);
            context.setVariable("cartList", templist);

        } else {

            CartDaoMem cart = (CartDaoMem) req.getSession().getAttribute("cartMem");
            if (cart != null) {
                Map<Product, Integer> tempMap = cart.getAllDaoMem();
                sum = cart.productsTotalPrice(cart);
                numberOfProducts = cart.totalNumberOfProductsInCart(cart);
                context.setVariable("cartListInMem", tempMap);
            }

        }

        context.setVariable("username", sessionUsername);
        context.setVariable("totalPrice", sum);
        context.setVariable("totalNumberOfItems", numberOfProducts);
        engine.process("cart.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getSession().getAttribute("username") != null) {

            UserDao user = null;
            CartDao cartDao = null;
            try {
                user = UserDaoJDBC.getInstance();
                cartDao = CartDaoJDBC.getInstance();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (req.getParameter("addToCart") != null) {
                    int id = Integer.parseInt(req.getParameter("addToCart"));
                    int userId = user.getUserByUsername((String) req.getSession().getAttribute("username")).getId();
                    Integer cartId = (Integer) req.getSession().getAttribute("cartId");
                    if(cartId == null){
                        cartId = cartDao.addNewCart(userId);
                        req.getSession().setAttribute("cartId", cartId);
                    }
                    System.out.println("Add new cart" + cartId);
                    cartDao.add(id, cartId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (req.getParameter("itemId") != null && req.getParameter("changeQuantity") != null) {
                    int itemIdToChangeQuantity = Integer.parseInt(req.getParameter("itemId"));
                    int newQuantity = Integer.parseInt(req.getParameter("changeQuantity"));
                    int userId = user.getUserByUsername((String) req.getSession().getAttribute("username")).getId();

                    Integer cartId = (Integer) req.getSession().getAttribute("cartId");
                    if(cartId == null){
                        cartId = cartDao.addNewCart(userId);
                    }
                    if (newQuantity == 0) {
                        cartDao.removeProduct(itemIdToChangeQuantity);
                    }
                    if (newQuantity > 0) {
                        if (newQuantity >= cartDao.get(itemIdToChangeQuantity, (Integer) req.getSession().getAttribute("cartId"))) {
                            int quantityDifference = newQuantity - cartDao.get(itemIdToChangeQuantity, (Integer) req.getSession().getAttribute("cartId"));

                            for (int i = 1; i <= quantityDifference; i++) {

                                cartDao.add(itemIdToChangeQuantity, cartId);
                            }
                        } else {
                            cartDao.removeProduct(itemIdToChangeQuantity);
                            for (int i = 1; i <= newQuantity; i++) {
                                cartDao.add(itemIdToChangeQuantity, cartId);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            CartDao cart = (CartDao) req.getSession().getAttribute("cartMem");
            if (cart == null) {
                cart = new CartDaoMem();
                req.getSession().setAttribute("cartMem", cart);
            }
            try {
                if (req.getParameter("addToCart") != null) {
                    int id = Integer.parseInt(req.getParameter("addToCart"));
                    cart.add(id, 0);
                    Map<Product, Integer> cartMap = null;
                    cartMap = cart.getAllDaoMem();
                    for (Map.Entry<Product, Integer> entry : cartMap.entrySet()) {
                        System.out.println(entry.getKey().getName() + ": " + entry.getValue());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (req.getParameter("itemId") != null && req.getParameter("changeQuantity") != null) {
                    int itemIdToChangeQuantity = Integer.parseInt(req.getParameter("itemId"));
                    int newQuantity = Integer.parseInt(req.getParameter("changeQuantity"));
                    cart.changeQuantity(itemIdToChangeQuantity, newQuantity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        resp.sendRedirect("/shop");
    }

}
