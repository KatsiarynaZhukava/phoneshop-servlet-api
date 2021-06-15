<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<div>
    Cart: ${cart.totalQuantity} items, total cost
    <fmt:formatNumber value="${not empty cart.totalCost ? cart.totalCost : 0}" type="currency" currencySymbol="${cart.items[0].product.currency.symbol}"/>
</div>