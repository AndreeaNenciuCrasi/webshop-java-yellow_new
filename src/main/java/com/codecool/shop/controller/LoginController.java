package com.codecool.shop.controller;

import com.codecool.shop.dao.*;

import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //get session if it exists
        HttpSession session = req.getSession(false);

        String sessionUsername = null;

        if(session!=null) {
            sessionUsername = (String)session.getAttribute("username");
        }

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        context.setVariable("username", sessionUsername);
        engine.process("login.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = null;
        String passwordDB = null;
        CartDao cartDao = null;

        try {
            userDao = UserDaoJDBC.getInstance();
            cartDao = CartDaoJDBC.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        System.out.println(username);

        try {
            passwordDB = userDao.getPasswordByUsername(username);
            System.out.println(passwordDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (password.equals(passwordDB)) {

            System.out.println("Successfully logged in");
            HttpSession session = req.getSession();
            System.out.println("Session created");
            session.setAttribute("username", username);
            String sessionUsername = (String)session.getAttribute("username");
            CartDaoMem cart = (CartDaoMem) req.getSession().getAttribute("cartMem");
            if (cart != null) {
                try {
                    int userId = userDao.getUserByUsername((String) req.getSession().getAttribute("username")).getId();
                    Integer cartId = cartDao.addNewCart(userId);
                    session.setAttribute("cartId", cartId);
                    Map<Product, Integer> tempMap = cart.getAllDaoMem();
                    for (Map.Entry<Product, Integer> entry : tempMap.entrySet()) {
                        for(int i=0; i<entry.getValue(); i++) {
                            cartDao.add(entry.getKey().getId(), cartId);
                        }
                    }
                    req.getSession().removeAttribute("cartMem");

                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            resp.sendRedirect("/");
            System.out.println(sessionUsername);
        } else {
            resp.sendRedirect("/login");
        }
    }


}
