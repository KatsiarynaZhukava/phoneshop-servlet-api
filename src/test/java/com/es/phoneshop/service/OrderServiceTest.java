package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.util.DataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {
    private final OrderService orderService = DefaultOrderService.getInstance();

    @Before
    public void setup() {
        ArrayList<String> secureIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            secureIds.add(UUID.randomUUID().toString());
        }
        DataProvider.setUpOrderDao(secureIds);
        DataProvider.setUpProductDao();
    }

    @Test
    public void testGetOrder() {
        Cart cart = new Cart();
        CopyOnWriteArrayList<CartItem> cartItems = new CopyOnWriteArrayList<>();
        Product product = ArrayListProductDao.getInstance().getProduct(0L).get();
        cartItems.add(new CartItem(product, 1));
        cart.setItems(cartItems);
        cart.setTotalCost(product.getPrice());

        Order order = orderService.getOrder(cart);
        BigDecimal subtotal = product.getPrice();
        assertEquals(subtotal, order.getSubtotal());
        assertEquals(new BigDecimal(5), order.getDeliveryCost());
        assertEquals(subtotal.add(new BigDecimal(5)), order.getTotalCost());
    }

    @Test
    public void testPlaceOrder() {
        Order order = new Order(null, null, new BigDecimal(100), new BigDecimal(5), "Oompa", "Loompa", "228 42 42", LocalDate.of(2021, Month.JUNE, 28), "Minsk, Melnikaite st.", PaymentMethod.CREDIT_CARD);
        orderService.placeOrder(order);
        assertNotEquals(null, order.getSecureId());
        assertNotEquals(null, order.getId());
    }

    @Test
    public void testGetPaymentMethods() {
        assertEquals(Arrays.asList(PaymentMethod.values()), orderService.getPaymentMethods());
    }
}
