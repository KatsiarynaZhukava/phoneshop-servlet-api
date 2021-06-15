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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import static com.es.phoneshop.util.Messages.*;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;

public class ProductDetailsPageServlet extends AddToCartServlet {
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
        super.doPost(request, response);
    }
}
