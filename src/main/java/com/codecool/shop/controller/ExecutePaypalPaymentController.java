package com.codecool.shop.controller;

import com.codecool.shop.dao.CartDao;
import com.codecool.shop.dao.implementation.CartDaoJDBC;
import com.codecool.shop.model.EmailSender;
import com.codecool.shop.model.PaymentServices;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/execute_payment"})
public class ExecutePaypalPaymentController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String paymentId = req.getParameter("paymentId");
        String payerId = req.getParameter("PayerID");
        Integer cartId = (Integer) req.getSession().getAttribute("cartId");

        try {
            PaymentServices paymentServices = new PaymentServices();
            paymentServices.executePayment(paymentId,payerId);

            //TODO: order history
            CartDao cartDao = new CartDaoJDBC().getInstance();
            cartDao.removeCart(cartId);


            String emailTo = "codecoolbucurestitest@gmail.com"; //client email
            String subject = "Your order No."+cartId;
            String emailMessage = "Your Codecoolshop order has been registered No" +cartId;
            EmailSender emailSender = new EmailSender(emailTo,subject,emailMessage);

            resp.sendRedirect("/confirmed");

        }catch (PayPalRESTException | SQLException ex){
            ex.printStackTrace();
            req.setAttribute("errorMessage", ex.getMessage());
            req.getRequestDispatcher("/error").forward(req, resp);
        }
    }
}