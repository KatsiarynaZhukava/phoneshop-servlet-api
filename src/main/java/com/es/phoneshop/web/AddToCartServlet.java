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
        productId = Long.valueOf(request.getParameter("productId"));
        Long quantity = getQuantity(request, response);
        if (quantity != null) {
            Cart cart = cartService.getCart(request.getSession());
            try {
                cartService.add(cart, productId, quantity, request.getSession());
            } catch (NumberFormatException e) {
                handleException(request, response, ID_NOT_A_NUMBER);
                return;
            } catch (OutOfStockException e) {
                handleException(request, response, e.getQuantityOfItemsInCart() > 0 ?
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITH_ITEMS_IN_CART, e.getStockRequested(), e.getQuantityOfItemsInCart(), e.getStockAvailable()) :
                        MessageFormat.format(PRODUCT_OUT_OF_STOCK_WITHOUT_ITEMS_IN_CART, e.getStockAvailable()));
                return;
            }
            response.sendRedirect(request.getRequestURI() + "?message=Added to cart successfully");
        }
    }

    private Long getQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String quantityString = request.getParameter("quantity");
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
            handleException(request, response, "Not a number");
            quantity = null;
        } catch (InvalidValueException e) {
            handleException(request, response, e.getMessage());
            quantity = null;
        }
        return quantity;
    }

    private void handleException(final HttpServletRequest request, final HttpServletResponse response, final String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("errorProductId", productId);
        doGet(request, response);
    }
}
