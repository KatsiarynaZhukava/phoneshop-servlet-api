<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <c:if test="${not empty param.message}">
    <tags:message message="${param.message}" className="success"/>
  </c:if>
  <c:if test="${not empty error}">
    <tags:message message="An error occurred while adding to cart" className="error"/>
  </c:if>
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
          <td class="quantity">
            Quantity
          </td>
          <td class="price">
            Price
            <tags:sortLink sort="price" order="asc"/>
            <tags:sortLink sort="price" order="desc"/>
          </td>
        </tr>
      </thead>
      <c:forEach var="product" items="${products}">
      <form method="post" action="${pageContext.servletContext.contextPath}/products">
        <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
              ${product.description}
            </a>
          </td>
          <td>
            <input class="quantity" name="quantity" value="${not empty error && productId == product.id ? param.quantity : 1}">
            <c:if test="${not empty error && productId == product.id}">
              <p class="error">
                  ${error}
              </p>
            </c:if>
            <input type="hidden" name="productId" value="${product.id}"/>
          </td>
          <td class="price">
            <a href='#' onclick='window.open("${pageContext.servletContext.contextPath}/price-history/${product.id}", "_blank", "height=300,width=350");'>
                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </a>
          </td>
          <td>
            <button>Add to cart</button>
          </td>
        </tr>
      </form>
      </c:forEach>
    </table>
  <footer>
    <jsp:include page="recentlyViewed.jsp"/>
  </footer>
</tags:master>