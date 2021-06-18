package com.es.phoneshop.web;

import com.es.phoneshop.exception.InvalidValueException;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.es.phoneshop.util.Messages.*;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;
    private RecentlyViewedService recentlyViewedService;
    private Map<Long, String> errors;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
        errors = new HashMap<>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request.getSession()));
        request.setAttribute("recentlyViewedProducts", recentlyViewedService.getRecentlyViewed(request.getSession()));
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        errors.clear();
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Cart cart = cartService.getCart(request.getSession());

        if (productIds != null) {
            for (int i = 0; i < productIds.length; i++) {
                Long productId = Long.valueOf(productIds[i]);
                Long quantity = getQuantity(quantities[i], productId, request);
                if (quantity != null) {
                    try {
                        cartService.update(cart, productId, quantity, request.getSession());
                    } catch (OutOfStockException e) {
                        errors.put(productId, MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITHOUT_ITEMS_IN_CART, e.getStockAvailable()));
                    }
                }
            }
        }
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect(request.getRequestURI());
        }
    }

    private Long getQuantity( final String quantityString, final Long productId, final HttpServletRequest request) throws ServletException, IOException {
        Long quantity;
        NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
        try {
            Number quantityNumber = numberFormat.parse(quantityString);
            if (quantityNumber.doubleValue() > Long.MAX_VALUE) {
                throw new InvalidValueException( QUANTITY_TOO_LARGE );
            } else {
                quantity = quantityNumber.longValue();
            }
            if (quantity <= 0) throw new InvalidValueException( QUANTITY_NON_POSITIVE_VALUE );
        } catch (ParseException e) {
            errors.put(productId, "Not a number");
            quantity = null;
        } catch (InvalidValueException e) {
            errors.put(productId, e.getMessage());
            quantity = null;
        }
        return quantity;
    }
}
