<%@ page import="utils.CSRFUtil" %>
<input type="hidden" name="csrfToken" value="<%= CSRFUtil.getToken(session) %>">