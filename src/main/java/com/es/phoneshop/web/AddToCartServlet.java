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
import java.util.InputMismatchException;
import java.util.Scanner;

import static com.es.phoneshop.util.Messages.*;

public abstract class AddToCartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdString = request.getParameter("productId");
        Long productId;
        try {
            productId = Long.valueOf(productIdString);
        } catch (NumberFormatException e) {
            handleException(request, response, ID_NOT_A_NUMBER, productIdString);
            return;
        }
        Long quantity = getQuantity(request, response, productIdString);
        if (quantity != null) {
            Cart cart = cartService.getCart(request.getSession());
            try {
                cartService.add(cart, productId, quantity, request.getSession());
            } catch (OutOfStockException e) {
                handleException(request, response, e.getQuantityOfItemsInCart() > 0 ?
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITH_ITEMS_IN_CART, e.getStockRequested(), e.getQuantityOfItemsInCart(), e.getStockAvailable()) :
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITHOUT_ITEMS_IN_CART, e.getStockAvailable()),
                        productIdString);
                return;
            }
            response.sendRedirect(request.getRequestURI() + "?message=Added to cart successfully");
        }
    }

    private Long getQuantity(final HttpServletRequest request, final HttpServletResponse response, final String productId) throws IOException, ServletException {
        String quantityString = request.getParameter("quantity");
        Long quantity;
        try {
            Scanner scanner = new Scanner(quantityString);
            scanner.useLocale(request.getLocale());
            quantity = scanner.nextLong();
            if (quantity <= 0) throw new InvalidValueException( QUANTITY_NON_POSITIVE_VALUE );
        } catch (InputMismatchException e) {
            handleException(request, response, NOT_A_VALID_NUMBER, productId);
            quantity = null;
        } catch (InvalidValueException e) {
            handleException(request, response, e.getMessage(), productId);
            quantity = null;
        }
        return quantity;
    }

    private void handleException(final HttpServletRequest request, final HttpServletResponse response, final String message, final String productId) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("productId", productId);
        doGet(request, response);
    }
}