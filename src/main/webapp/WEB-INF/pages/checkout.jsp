<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
    <c:if test="${not empty param.message}">
        <tags:message message="${param.message}" className="success"/>
    </c:if>
    <c:if test="${not empty errors}">
        <tags:message message="Errors occurred while placing order" className="error"/>
    </c:if>
    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
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
            <tags:orderFormRow name="firstName" label="First Name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="lastName" label="Last Name" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tags:orderFormRow name="phoneNumber" label="Phone Number" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Delivery Date<span style="color:red">*</span></td>
                <td>
                    <c:set var="error" value="${errors['deliveryDate']}"/>
                    <input type="date" name="deliveryDate" value="${not empty error ? param['deliveryDate'] : order['deliveryDate']}">
                    <c:if test="${not empty error}">
                        <p class="error">
                                ${error}
                        </p>
                    </c:if>
                </td>
            </tr>
            <tags:orderFormRow name="deliveryAddress" label="Delivery Address" order="${order}" errors="${errors}"></tags:orderFormRow>
            <tr>
                <td>Payment method<span style="color:red">*</span></td>
                <td>
                    <select name="paymentMethod">
                        <option></option>
                        <c:forEach var="paymentMethod" items="${paymentMethods}">
                            <option value="${paymentMethod}" ${param.paymentMethod == paymentMethod? "selected" : ""}>${paymentMethod}</option>
                        </c:forEach>
                    </select>
                    <c:set var="error" value="${errors['paymentMethod']}"/>
                    <c:if test="${not empty error}">
                        <p class="error">
                                ${error}
                        </p>
                    </c:if>
                </td>
            </tr>
        </table>
        <p>
            <button>Place order</button>
        </p>
    </form>
    <footer>
        <jsp:include page="recentlyViewed.jsp"/>
    </footer>
</tags:master>
