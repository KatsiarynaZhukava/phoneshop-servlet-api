<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Advanced Search">
    <c:if test="${not empty param.message}">
        <tags:message message="${param.message}" className="success"/>
    </c:if>
    <c:if test="${not empty error}">
        <tags:message message="An error occurred while adding to cart" className="error"/>
    </c:if>
    <form method="post">
        <div>
            <table>
                <tr>
                    <td>Description</td>
                    <td><input name="description" type="text" value="${param.description}">
                    <td>
                        <select name="descriptionSearchType">
                            <c:forEach var="descriptionSearchType" items="${descriptionSearchTypes}">
                                <option value="${descriptionSearchType}" ${param.descriptionSearchType == descriptionSearchType? "selected" : ""}>${descriptionSearchType}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Min price</td>
                    <td>
                        <input name="minPrice" value="${param.minPrice}">
                        <c:set var="error" value="${errors['minPrice']}"/>
                        <c:if test="${not empty error}">
                            <p class="error">
                                    ${error}
                            </p>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td>Max price</td>
                    <td>
                        <input name="maxPrice" value="${param.maxPrice}">
                        <c:set var="error" value="${errors['maxPrice']}"/>
                        <c:if test="${not empty error}">
                            <p class="error">
                                    ${error}
                            </p>
                        </c:if>
                    </td>
                </tr>
            </table>
            <input id="search-button"type="submit" value="Search">
        </div>
    </form>
    <c:if test="${not empty products}">
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
            </tr>
            </thead>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}
                        </a>
                    </td>
                    <td class="price">
                        <a href='#' onclick='window.open("${pageContext.servletContext.contextPath}/price-history/${product.id}", "_blank", "height=300,width=350");'>
                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</tags:master>
