<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
  <h1>
      Price history
  </h1>
  <h3>
      ${product.description}
  </h3>
  <table>
    <thead>
    <tr>
      <th>Start date</th>
      <th class="price">Price</th>
    </tr>
    </thead>
    <c:forEach var="pair" items="${product.priceHistory.entrySet()}">
      <tr>
        <td>
          <fmt:parseDate value="${ pair.getKey() }" pattern="yyyy-MM-dd'T'HH:mm" var="priceChangeDate" type="both" />
          <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${ priceChangeDate }" />
        </td>
        <td class="price">
          <fmt:formatNumber value="${pair.getValue()}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
    </c:forEach>
  </table>