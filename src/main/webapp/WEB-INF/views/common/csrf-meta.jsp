<%@ page import="utils.CSRFUtil" %>
<meta name="csrf-token" content="<%= CSRFUtil.getToken(session) %>">