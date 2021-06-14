<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
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
        <c:forEach var="item" items="${cart.items}">
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
                <td>
                    <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                    <input class="quantity" name="quantity" value="${quantity}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    <footer>
        <jsp:include page="recentlyViewed.jsp"/>
    </footer>
</tags:master>
