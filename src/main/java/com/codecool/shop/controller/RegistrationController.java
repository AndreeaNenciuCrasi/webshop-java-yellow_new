package com.codecool.shop.controller;

import com.codecool.shop.dao.*;

import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.EmailSender;
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

@WebServlet(urlPatterns = {"/registration"})
public class RegistrationController extends HttpServlet {

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
        engine.process("register.html", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = null;

        try {
            userDao = UserDaoJDBC.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String phone = "None";
        String password = req.getParameter("password");
        String billingAddress = "None";
        String shippingAddress = "None";

        User user =new User(username, password, firstName, lastName, email, phone, billingAddress, shippingAddress);


        try {
            assert userDao != null;
            userDao.add(user);
            System.out.println("New user added");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("New user added");

        String subjectEmail = "Welcome at Codecool Shop";
        String messageEmail =  "We are very happy to have you here! Happy shopping!";

        EmailSender emailSender = new EmailSender(email,subjectEmail,messageEmail);

        resp.sendRedirect("/login");

    }
}