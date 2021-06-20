<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="className" required="true" %>

<p class="${className}">
    ${message}
</p>