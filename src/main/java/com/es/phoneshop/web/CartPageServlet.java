package com.es.phoneshop.web;

import com.es.phoneshop.exception.InvalidValueException;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DefaultCartService;
import com.es.phoneshop.service.DefaultRecentlyViewedService;
import com.es.phoneshop.service.RecentlyViewedService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import static com.es.phoneshop.util.Messages.*;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;
    private RecentlyViewedService recentlyViewedService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        request.setAttribute("cart", cartService.getCart(request.getSession()));
        request.setAttribute("recentlyViewedProducts", recentlyViewedService.getRecentlyViewed(request.getSession()));
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Cart cart = cartService.getCart(request.getSession());
        Map<Long, String> errors = new HashMap<>();

        if (productIds != null) {
            Long productId = null;
            for (int i = 0; i < productIds.length; i++) {
                try {
                    productId = Long.valueOf(productIds[i]);
                    Long quantity = getQuantity(quantities[i], productId, errors, request);
                    if (quantity != null) {
                        cartService.update(cart, productId, quantity, request.getSession());
                    }
                } catch (OutOfStockException e) {
                    errors.put(productId, MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITHOUT_ITEMS_IN_CART, e.getStockAvailable()));
                } catch (NotFoundException e) {
                    errors.put(productId, e.getMessage());
                } catch (NumberFormatException e) {
                    errors.put(productId, MessageFormat.format(NOT_A_NUMBER, "Product id "));
                }
            }
        }
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private Long getQuantity( final String quantityString, final Long productId, final Map<Long, String> errors, final HttpServletRequest request) throws ServletException, IOException {
        Long quantity;
        try {
            Scanner scanner = new Scanner(quantityString);
            scanner.useLocale(request.getLocale());
            quantity = scanner.nextLong();
            if (quantity <= 0) throw new InvalidValueException( QUANTITY_NON_POSITIVE_VALUE );
        } catch (InputMismatchException e) {
            errors.put(productId, MessageFormat.format(NOT_A_VALID_NUMBER, "Quantity "));
            quantity = null;
        } catch (InvalidValueException e) {
            errors.put(productId, e.getMessage());
            quantity = null;
        }
        return quantity;
    }
}
