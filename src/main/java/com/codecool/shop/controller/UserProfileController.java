package com.codecool.shop.controller;

import com.codecool.shop.dao.*;

import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.Product;

import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import com.codecool.shop.model.ProductCategory;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import java.util.Properties;

import static java.lang.System.*;

@WebServlet(urlPatterns = {"/user-profile"})
public class UserProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //get session if it exists
        HttpSession session = req.getSession(false);

        String sessionUsername = null;

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        if (session != null) {
            UserDao user = null;

            try {
                user = UserDaoJDBC.getInstance();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            sessionUsername = (String) session.getAttribute("username");
            context.setVariable("username", sessionUsername);

            try {
                context.setVariable("firstName", user.find(sessionUsername).getFirstName());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                context.setVariable("lastName", user.find(sessionUsername).getLastName());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                context.setVariable("email", user.find(sessionUsername).getEmail());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                context.setVariable("password", user.find(sessionUsername).getPassword());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (!user.find(sessionUsername).getPhone().equals("None")) {
                    try {
                        context.setVariable("phone_number", user.find(sessionUsername).getPhone());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    context.setVariable("phone_number", "Add Phone Number");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (!user.find(sessionUsername).getShippingAddress().equals("None")) {
                    try {
                        context.setVariable("shipping_address", user.find(sessionUsername).getShippingAddress());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    context.setVariable("shipping_address", "Add Shipping Address");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (!user.find(sessionUsername).getBillingAddress().equals("None")) {
                    try {
                        context.setVariable("billing_address", user.find(sessionUsername).getBillingAddress());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    context.setVariable("billing_address", "Add Billing Address");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            engine.process("user_profile.html", context, resp.getWriter());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = null;
        String username = null;
        String email=null;
        String password=null;
        String firstName=null;
        String lastName=null;
        String phone=null;
        String billingAddress=null;
        String shippingAddress=null;

        try {
            userDao = UserDaoJDBC.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HttpSession session = req.getSession(false);

        username = (String) session.getAttribute("username");

        if (!req.getParameter("first_name").equals("")) {
            firstName = req.getParameter("first_name");
        } else {
            try {
                firstName = userDao.getUserByUsername(username).getFirstName();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (!req.getParameter("last_name").equals("")) {
            lastName = req.getParameter("last_name");
        } else {
            try {
                lastName = userDao.getUserByUsername(username).getLastName();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (!req.getParameter("email").equals("")) {
            email = req.getParameter("email");
        } else {
            try {
                email = userDao.getUserByUsername(username).getEmail();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (!req.getParameter("phone_number").equals("")) {
            phone = req.getParameter("phone_number");
        } else {
            try {
                phone = userDao.getUserByUsername(username).getPhone();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        out.println(phone);

        if (!req.getParameter("billing_address").equals("")) {
            billingAddress = req.getParameter("billing_address");
        } else {
            try {
                billingAddress = userDao.getUserByUsername(username).getBillingAddress();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (!req.getParameter("shipping_address").equals("")) {
            shippingAddress = req.getParameter("shipping_address");
        } else {
            try {
                shippingAddress = userDao.getUserByUsername(username).getShippingAddress();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (!req.getParameter("password").equals("")) {
            password = req.getParameter("password");
        } else {
            try {
                password = userDao.getUserByUsername(username).getPassword();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        out.println(password);

        User user = new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);

        try {
            assert userDao != null;
            userDao.edit(user);
            System.out.println("User details edited");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resp.sendRedirect("/");

    }

}
