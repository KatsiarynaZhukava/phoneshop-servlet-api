package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);
        if (!cart.getItems().isEmpty()) {
            request.setAttribute("order", orderService.getOrder(cart));
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("message", "Cannot create the order because your cart is empty");
            request.getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap();
        verifyParameters(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clear(cart, session);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            request.setAttribute("order", order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        }
    }

    private void verifyParameters(final HttpServletRequest request, final Map<String, String> errors, final Order order) {
        String firstName = request.getParameter("firstName");
        if (!isValidWord(firstName)) {
            errors.put("firstName", "First name must not be empty and must contain letters only");
        } else {
            order.setFirstName(firstName);
        }

        String lastName = request.getParameter("lastName");
        if (!isValidWord(lastName)) {
            errors.put("lastName", "Last name must not be empty and must contain letters only");
        } else {
            order.setLastName(lastName);
        }

        String phoneNumber = request.getParameter("phoneNumber");
        if (!isValidPhoneNumber(phoneNumber)) {
            errors.put("phoneNumber", "Phone number must not be empty and must contain digits, spaces or dashes");
        } else {
            order.setPhoneNumber(phoneNumber);
        }

        try {
            LocalDate date = LocalDate.parse(request.getParameter("deliveryDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd", request.getLocale()));
            if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                order.setDeliveryDate(date);
            } else {
                errors.put("deliveryDate", "Delivery date must be either today or after today");
            }
        } catch (DateTimeException e) {
            errors.put("deliveryDate", "Provided value is not a date");
        }

        String deliveryAddress = request.getParameter("deliveryAddress");
        if (!isNotEmpty(deliveryAddress)) {
            errors.put("deliveryAddress", "Address must not be empty");
        } else {
            order.setDeliveryAddress(deliveryAddress);
        }

        String paymentMethod = request.getParameter("paymentMethod");
        if (!isValidPaymentMethod(paymentMethod)) {
            errors.put("paymentMethod", "Payment method must not be empty and be one of the suggested options");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        }
    }

    private boolean isValidWord(String value) {
        return value != null && value.matches("[A-Za-z]+");
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidPhoneNumber(String value) {
        return value != null && value.matches("[\\d -]+");
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod != null && (paymentMethod.equals("CASH") || paymentMethod.equals("CREDIT_CARD"));
    }
}
