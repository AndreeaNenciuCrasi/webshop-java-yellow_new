package com.codecool.shop.controller;

import com.codecool.shop.model.OrderDetailPaypal;
import com.codecool.shop.model.PaymentServices;
import com.paypal.base.rest.PayPalRESTException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authorize_payment"})
public class PaypalAuthorizePaymentController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Change the values if the shipping and tax is not free
        String product = "OrderCodecoolShopNo" + req.getParameter("product");
        String subtotal = req.getParameter("subtotal");
        String shipping = "0";
        String tax = "0";
        String total = req.getParameter("total");

        OrderDetailPaypal orderDetailPaypal = new OrderDetailPaypal(product, subtotal, shipping, tax, total);


        try {

            PaymentServices paymentServices = new PaymentServices();
            String approvalLink = paymentServices.authorizePayment(orderDetailPaypal);

            resp.sendRedirect(approvalLink);

        }catch (PayPalRESTException ex){
            ex.printStackTrace();
            req.setAttribute("errorMessage",ex.getMessage());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/error");
            dispatcher.forward(req,resp);

        }
    }
}
