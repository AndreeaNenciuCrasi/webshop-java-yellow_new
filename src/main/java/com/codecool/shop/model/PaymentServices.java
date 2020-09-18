package com.codecool.shop.model;


import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.ArrayList;
import java.util.List;

public class PaymentServices {
    private static final String CLIENT_ID="AaiXPDpObw2Kjdem44-qsQGbbFN-Mn0sDYN3uFkgySOmnybtCYuRPwy0LCmW7HcR5Ysuy564fdF33mTc";
    private static final String CLIENT_SECRET = "EDxQWyEdcQ-eERlb_2K8EA6ovfNa7yu1TxlpWwWMWtlpSmjL68tj70Ag0-wD14tQux5vSCa5Rw035XlU";
    private static final String MODE = "sandbox";

    public String authorizePayment(OrderDetailPaypal orderDetailPaypal)
            throws PayPalRESTException {

        Payer payer = getPayerInformation();
        RedirectUrls redirectUrls = getRedirectURLs();
        List<Transaction> listTransaction = getTransactionInformation(orderDetailPaypal);

        Payment requestPayment = new Payment();
        requestPayment.setTransactions(listTransaction);
        requestPayment.setRedirectUrls(redirectUrls);
        requestPayment.setPayer(payer);
        requestPayment.setIntent("authorize");

        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);

        Payment approvedPayment = requestPayment.create(apiContext);

        return getApprovalLink(approvedPayment);

    }
    public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
        APIContext apiContext = new APIContext(CLIENT_ID,CLIENT_SECRET, MODE);
        return Payment.get(apiContext,paymentId);

    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment payment = new Payment().setId(paymentId);
        APIContext apiContext = new APIContext(CLIENT_ID,CLIENT_SECRET, MODE);
        return payment.execute(apiContext,paymentExecution);
    }

    private Payer getPayerInformation() {
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setFirstName("TestCodecool")
                .setLastName("Codecool")
                .setEmail("codecoolbucurestitest@company.com");

        payer.setPayerInfo(payerInfo);

        return payer;
    }
    private RedirectUrls getRedirectURLs() {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8888/");
        redirectUrls.setReturnUrl("http://localhost:8888/review-payment");

        return redirectUrls;
    }

    private List<Transaction> getTransactionInformation(OrderDetailPaypal orderDetailPaypal) {
        Details details = new Details();
        details.setShipping(orderDetailPaypal.getShipping());
        details.setSubtotal(orderDetailPaypal.getSubtotal());
        details.setTax(orderDetailPaypal.getTax());

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(orderDetailPaypal.getTotal());
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(orderDetailPaypal.getProductName());

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();

        Item item = new Item();
        item.setCurrency("USD");
        item.setName(orderDetailPaypal.getProductName());
        item.setPrice(orderDetailPaypal.getSubtotal());
        item.setTax(orderDetailPaypal.getTax());
        item.setQuantity("1");

        items.add(item);
        itemList.setItems(items);
        transaction.setItemList(itemList);

        List<Transaction> listTransaction = new ArrayList<>();
        listTransaction.add(transaction);

        return listTransaction;
    }

    private String getApprovalLink(Payment approvedPayment) {
        List<Links> links = approvedPayment.getLinks();
        String approvalLink = null;

        for (Links link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                approvalLink = link.getHref();
                break;
            }
        }

        return approvalLink;
    }
}