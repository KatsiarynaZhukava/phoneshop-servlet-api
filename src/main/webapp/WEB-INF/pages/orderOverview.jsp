<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
    <h1>Order overview</h1>
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="price">
                    Price
                </td>
                <td class="quantity">
                    Quantity
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${order.items}" varStatus="status">
                <tr>
                    <td>
                        <img class="product-tile" src="${item.product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                                ${item.product.description}
                        </a>
                    </td>
                    <td class="price">
                        <a href='#' onclick='window.open("${pageContext.servletContext.contextPath}/price-history/${item.product.id}", "_blank", "height=300,width=350");'>
                            <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="${item.product.currency.symbol}"/>
                        </a>
                    </td>
                    <td class="quantity">
                        <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                        ${item.quantity}
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td></td>
                <td></td>
                <td class="price">Subtotal:</td>
                <td class="price">
                    <fmt:formatNumber value="${order.subtotal}" type="currency"
                                      currencySymbol="${order.items[0].product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td class="price">Delivery cost:</td>
                <td class="price">
                    <fmt:formatNumber value="${order.deliveryCost}" type="currency"
                                      currencySymbol="${order.items[0].product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td class="price">Total cost:</td>
                <td class="price">
                    <fmt:formatNumber value="${order.totalCost}" type="currency"
                                      currencySymbol="${order.items[0].product.currency.symbol}"/>
                </td>
            </tr>
        </table>
        <h2>Order details</h2>
        <table>
            <tags:orderOverviewRow name="firstName" label="First Name" order="${order}"></tags:orderOverviewRow>
            <tags:orderOverviewRow name="lastName" label="Last Name" order="${order}"></tags:orderOverviewRow>
            <tags:orderOverviewRow name="phoneNumber" label="Phone Number" order="${order}"></tags:orderOverviewRow>
            <tags:orderOverviewRow name="deliveryDate" label="Delivery Date" order="${order}"></tags:orderOverviewRow>
            <tags:orderOverviewRow name="deliveryAddress" label="Delivery Address" order="${order}"></tags:orderOverviewRow>
            <tr>
                <td>Payment method</td>
                <td>
                    ${order.paymentMethod}
                </td>
            </tr>
        </table>
</tags:master>
