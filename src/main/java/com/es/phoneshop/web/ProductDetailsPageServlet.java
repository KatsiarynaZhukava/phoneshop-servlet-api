package com.es.phoneshop.web;

import com.es.phoneshop.exception.InvalidValueException;
import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DefaultCartService;
import com.es.phoneshop.service.DefaultRecentlyViewedService;
import com.es.phoneshop.service.RecentlyViewedService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import static com.es.phoneshop.util.Messages.*;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedService recentlyViewedService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo().substring(1);
        try {
            HttpSession httpSession = request.getSession();
            request.setAttribute("product", productDao.getProduct(Long.valueOf(productId))
                    .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId)));
            request.setAttribute("cart", cartService.getCart(httpSession));

            recentlyViewedService.add(recentlyViewedService.getRecentlyViewed(httpSession), Long.valueOf(productId), httpSession);
            request.setAttribute("recentlyViewedProducts", recentlyViewedService.getRecentlyViewed(httpSession));
            request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
        } catch (NotFoundException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("productId", productId);
            request.getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long quantity = getQuantity(request, response);
        if (quantity != null) {
            Cart cart = cartService.getCart(request.getSession());
            Long productId;
            try {
                productId = Long.valueOf(request.getPathInfo().substring(1));
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
            response.sendRedirect(request.getContextPath() + "/products/" + productId + "?message=Added to cart successfully");
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
        doGet(request, response);
    }
}
