package com.es.phoneshop.web;

import com.es.phoneshop.exception.InvalidValueException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DefaultCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import static com.es.phoneshop.util.Messages.*;

public abstract class AddToCartServlet extends HttpServlet {
    private CartService cartService;
    private Long productId;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            productId = Long.valueOf(request.getParameter("productId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getRequestURI()+ "?error=" + ID_NOT_A_NUMBER + "&id=" + productId);
            return;
        }
        Long quantity = getQuantity(request, response);
        if (quantity != null) {
            Cart cart = cartService.getCart(request.getSession());
            try {
                cartService.add(cart, productId, quantity, request.getSession());
            } catch (OutOfStockException e) {
                String message = e.getQuantityOfItemsInCart() > 0 ?
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITH_ITEMS_IN_CART, e.getStockRequested(), e.getQuantityOfItemsInCart(), e.getStockAvailable()) :
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITHOUT_ITEMS_IN_CART, e.getStockAvailable());
                response.sendRedirect(request.getRequestURI()+ "?error=" + message + "&id=" + productId);
                return;
            }
            response.sendRedirect(request.getRequestURI() + "?message=Added to cart successfully");
        }
    }

    private Long getQuantity(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String quantityString = request.getParameter("quantity");
        Long quantity;
        NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
        try {
            Number quantityNumber =  numberFormat.parse(quantityString);
            if (quantityNumber.doubleValue() > Long.MAX_VALUE) {
                throw new InvalidValueException( QUANTITY_TOO_LARGE );
            } else {
                quantity = quantityNumber.longValue();
            }
            if (quantity <= 0) throw new InvalidValueException( QUANTITY_NON_POSITIVE_VALUE );
        } catch (ParseException e) {
            response.sendRedirect(request.getRequestURI()+ "?error=Not a number&id=" + productId);
            quantity = null;
        } catch (InvalidValueException e) {
            response.sendRedirect(request.getRequestURI()+ "?error=" + e.getMessage() + "&id=" + productId);
            quantity = null;
        }
        return quantity;
    }
}