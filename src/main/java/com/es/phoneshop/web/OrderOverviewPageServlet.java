package com.es.phoneshop.web;

import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

import static com.es.phoneshop.util.Messages.ORDER_NOT_FOUND_BY_ID;

public class OrderOverviewPageServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureId = request.getPathInfo().substring(1);
        Optional<Order> order = ArrayListOrderDao.getInstance().getOrderBySecureId(secureId);
        if (order.isPresent()) {
            request.setAttribute("order", order.get());
            request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp").forward(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("message", MessageFormat.format(ORDER_NOT_FOUND_BY_ID, secureId));
            request.getRequestDispatcher("/WEB-INF/pages/errorNotFound.jsp").forward(request, response);
        }
    }
}
