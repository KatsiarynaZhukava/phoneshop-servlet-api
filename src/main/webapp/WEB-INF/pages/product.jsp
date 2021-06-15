<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Detail">
  <p>Cart: ${cart}</p>
  <c:if test="${not empty param.message}">
    <p class="success">
        ${param.message}
    </p>
  </c:if>
  <c:if test="${not empty error}">
    <p class="error">
        An error occurred while adding to cart
    </p>
  </c:if>
  <p>
      ${product.description}
  </p>
  <form method="post" action="${pageContext.servletContext.contextPath}/products/${product.id}">
    <table>
      <thead>
      <tr>
        <td>Image</td>
        <td><img src="${product.imageUrl}"></td>
      </tr>
      <tr>
        <td>Code</td>
        <td class="detail">${product.code}</td>
      </tr>
      <tr>
        <td>Price</td>
        <td class="price">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
      <tr>
        <td>Stock</td>
        <td class="detail">${product.stock}</td>
      </tr>
      <tr>
        <td>Quantity</td>
        <td>
          <input class="quantity" name="quantity" value="${not empty error ? param.quantity : 1}">
          <c:if test="${not empty error}">
            <p class="error">
              ${error}
            </p>
          </c:if>
          <input type="hidden" name="productId" value="${product.id}"/>
        </td>
      </tr>
    </table>
    <div class="add-button">
      <button>Add to cart</button>
    </div>
  </form>
  <footer>
    <jsp:include page="recentlyViewed.jsp"/>
  </footer>
</tags:master>