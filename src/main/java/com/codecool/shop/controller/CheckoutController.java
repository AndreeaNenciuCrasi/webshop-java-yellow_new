package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.OrderDao;
import com.codecool.shop.dao.UserDao;
import com.codecool.shop.dao.implementation.CartDaoJDBC;
import com.codecool.shop.dao.implementation.OrderDaoJDBC;
import com.codecool.shop.dao.implementation.UserDaoJDBC;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CartDaoJDBC cartDao = null;
        float sum = 0;
        int numberOfProducts = 0;
        String sessionUsername = null;
        List<Cart> templist = new ArrayList<>();


        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext() );

        HttpSession session = req.getSession(false);

        if(session!=null) {
            sessionUsername = (String)session.getAttribute("username");
        }

        try {
            cartDao = CartDaoJDBC.getInstance();
            templist = cartDao.getAll((Integer) req.getSession().getAttribute("cartId"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        sum = cartDao.productsTotalPrice(templist);
        numberOfProducts = cartDao.totalNumberOfProductsInCart(templist);

        Integer cartId = (Integer) req.getSession().getAttribute("cartId");

        context.setVariable("cartList", templist);
        context.setVariable("username", sessionUsername);
        context.setVariable("totalPrice", sum);
        context.setVariable("cartId",cartId);
        context.setVariable("totalNumberOfItems", numberOfProducts);

        if (sessionUsername == null){
            resp.sendRedirect("/login");
        }else {
            engine.process("/checkout.html", context, resp.getWriter());
        }
    }
}
