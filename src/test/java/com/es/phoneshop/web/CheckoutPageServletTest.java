package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.DefaultCartService;
import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private HttpSession httpSession;

    private final CartService cartService = DefaultCartService.getInstance();
    private final CheckoutPageServlet servlet = new CheckoutPageServlet();
    private Cart cart;

    @Before
    public void setup() throws ServletException, OutOfStockException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(httpSession);
        DataProvider.setUpProductDao();
        initializeCorrectAttributes();
        when(request.getLocale()).thenReturn(new Locale("en", "GB"));
    }

    @Test
    public void testDoGetCartInSession() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetNoCartInSession() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(request).setAttribute(eq("message"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostFirstNameEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("firstName")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostFirstNameContainsIncorrectSymbols() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("firstName")).thenReturn("12asdf");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostLastNameEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("lastName")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostLastNameContainsIncorrectSymbols() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("lastName")).thenReturn("12asdf");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostPhoneNumberEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("phoneNumber")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostPhoneNumberContainsIncorrectSymbols() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("phoneNumber")).thenReturn("12asdf");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostDeliveryDateBeforeToday() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("deliveryDate")).thenReturn("1999-12-12");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostDeliveryDateEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("deliveryDate")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostDeliveryAddressEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("deliveryAddress")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostPaymentMethodEmpty() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        when(request.getParameter("paymentMethod")).thenReturn("");
        verifyErrorSituation();
    }

    @Test
    public void testDoPostCorrectOrder() throws ServletException, IOException, OutOfStockException {
        initializeCart();
        servlet.doPost(request, response);
        verify(response, times(1)).sendRedirect(anyString());
    }

    private void verifyErrorSituation() throws ServletException, IOException {
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);
    }

    private void initializeCart() throws OutOfStockException {
        cart = new Cart();
        cartService.add(cart, 0L, 1L, httpSession);
        cartService.add(cart, 1L, 1L, httpSession);
        when(request.getSession().getAttribute(DefaultCartService.class.getName() + "cart")).thenReturn(cart);
        when(request.getSession()).thenReturn(httpSession);
    }

    private void initializeCorrectAttributes() {
        when(request.getParameter("firstName")).thenReturn("Harry");
        when(request.getParameter("lastName")).thenReturn("Potter");
        when(request.getParameter("phoneNumber")).thenReturn("12345678");
        when(request.getParameter("deliveryDate")).thenReturn(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        when(request.getParameter("deliveryAddress")).thenReturn("4 Privet Drive, Little Whinging, Surrey");
        when(request.getParameter("paymentMethod")).thenReturn(PaymentMethod.CASH.toString());
    }
}