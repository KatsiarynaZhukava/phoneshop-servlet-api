<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<jsp:useBean id="recentlyViewedProducts" type="java.util.ArrayDeque" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <form>
    <input name="query" value="${param.query}">
    <button>Search</button>
  </form>
  <table>
    <thead>
      <tr>
        <td>Image</td>
          <td>
            Description
            <tags:sortLink sort="description" order="asc"/>
            <tags:sortLink sort="description" order="desc"/>
          </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc"/>
          <tags:sortLink sort="price" order="desc"/>
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
</tags:master>