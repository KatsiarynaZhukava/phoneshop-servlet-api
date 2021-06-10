<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="recentlyViewedProducts" type="java.util.ArrayDeque" scope="request"/>

<div>
    <c:if test="${not empty recentlyViewedProducts}">
        <p>Recently viewed:</p>
        <div class="recently-viewed-container">
            <c:forEach var="product" items="${recentlyViewedProducts}">
                <div class="item-container">
                    <img class="product-image" src="${product.imageUrl}" alt="product image"/>
                    <div>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}</a>
                    </div>
                    <div>
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>
