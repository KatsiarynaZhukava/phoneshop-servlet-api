package com.es.phoneshop.web;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.util.Messages.PRODUCT_NOT_FOUND_BY_ID;

public class PriceHistoryPageServlet extends HttpServlet {
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        try {
            request.setAttribute("product", productDao.getProduct(Long.valueOf(productId.substring(1)))
                    .orElseThrow(NotFoundException.supplier(PRODUCT_NOT_FOUND_BY_ID, productId)));
            request.getRequestDispatcher("/WEB-INF/pages/priceHistory.jsp").forward(request, response);
        } catch (NotFoundException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("productId", productId.substring(1));
            request.getRequestDispatcher("/WEB-INF/pages/errorProductNotFound.jsp").forward(request, response);
        }
    }
}
