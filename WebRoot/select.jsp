<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'select.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript">
	</script>
  </head>
  
  <body>
    <h1>输入要进入的房间名称</h1>
    <font color="red" size="4">${requestScope.msg } </font>
   	<br/>
   	<br/>
    <form action="<c:url value='/EnterRoomServlet'/>" method="post"> 
    	房间号 <input type="text" name="roomname" value="TEST"/>
    	<br/>
    	昵称 <input type="text" name="nickname" maxlength="4"/>
    	<br/>
    	<input type="submit" value="进入房间" />
    </form>
  </body>
</html>
