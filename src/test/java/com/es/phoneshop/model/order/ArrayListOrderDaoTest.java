package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.NotFoundException;
import com.es.phoneshop.util.DataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class ArrayListOrderDaoTest {
    private final OrderDao orderDao = ArrayListOrderDao.getInstance();
    private ArrayList<String> secureIds;

    @Before
    public void setup() {
        secureIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            secureIds.add(UUID.randomUUID().toString());
        }
        DataProvider.setUpOrderDao(secureIds);
    }

    @After
    public void deleteOutputFile() {
        orderDao.clear();
    }

    @Test
    public void testGetProductById() {
        Long id = 0L;
        assertTrue(orderDao.getOrder(id).isPresent());
    }

    @Test
    public void testGetNonexistentProductById() {
        Long id = -1L;
        assertFalse(orderDao.getOrder(id).isPresent());
    }

    @Test
    public void testGetProductBySecureId() {
        assertTrue(orderDao.getOrderBySecureId(secureIds.get(0)).isPresent());
    }

    @Test
    public void testGetNonexistentProductBySecureId() {
        assertFalse(orderDao.getOrderBySecureId(UUID.randomUUID().toString()).isPresent());
    }

    @Test
    public void testSaveOrderWithoutId() {
        String secureId = UUID.randomUUID().toString();
        Order order = new Order(null, secureId, new BigDecimal(100), new BigDecimal(5), "Oompa", "Loompa", "228 42 42", LocalDate.of(2021, Month.JUNE, 28), "Minsk, Melnikaite st.", PaymentMethod.CREDIT_CARD);
        orderDao.save(order);

        assertTrue(order.getId() > 0);
        Order result = orderDao.getOrder(order.getId()).get();
        assertSavedOrderIsCorrect(result, secureId);
    }

    @Test(expected = NotFoundException.class)
    public void testSaveOrderWithNonexistentId() {
        String secureId = UUID.randomUUID().toString();
        Order order = new Order(228L, secureId, new BigDecimal(100), new BigDecimal(5), "Oompa", "Loompa", "228 42 42", LocalDate.of(2021, Month.JUNE, 28), "Minsk, Melnikaite st.", PaymentMethod.CREDIT_CARD);
        orderDao.save(order);
    }

    @Test
    public void testSaveOrderWithExistingId() {
        String secureId = UUID.randomUUID().toString();
        Order order = new Order(0L, secureId, new BigDecimal(100), new BigDecimal(5), "Oompa", "Loompa", "228 42 42", LocalDate.of(2021, Month.JUNE, 28), "Minsk, Melnikaite st.", PaymentMethod.CREDIT_CARD);
        orderDao.save(order);
        Order result = orderDao.getOrder(0L).get();
        assertSavedOrderIsCorrect(result, secureId);
    }

    private void assertSavedOrderIsCorrect(Order result, String secureId) {
        assertNotNull(result);
        assertEquals(secureId, result.getSecureId());
        assertEquals(new BigDecimal(100), result.getSubtotal());
        assertEquals(new BigDecimal(5), result.getDeliveryCost());
        assertEquals("Oompa", result.getFirstName());
        assertEquals("Loompa", result.getLastName());
        assertEquals("228 42 42", result.getPhoneNumber());
        assertEquals(LocalDate.of(2021, Month.JUNE, 28), result.getDeliveryDate());
        assertEquals("Minsk, Melnikaite st.", result.getDeliveryAddress());
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
    }
}
