<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>错误</title>
</head>
<h2>错误界面</h2>
<body>

<c:if test="${not empty err}">${err}</c:if>
</br>

<c:if test="${to eq 'index' }">
<a href="index.jsp">返回登录页面</a>
</c:if>
<c:if test="${to eq 'buyermain' }">
<a href="BuyerMain.jsp">返回主页</a>
</c:if>
<c:if test="${to eq 'show_goods' }">
<a href="show_goods.jsp">返回主页</a>
</c:if>
<c:if test="${to eq 'wrong_answer' }">
<a href="index.jsp">返回登录页面</a>
</c:if>

<c:if test="${not empty sessionScope.admin}"><!-- 登录显示以下内容 -->
	<c:if test="${to eq 'good' }">
	<a href="good.jsp">前往商品界面</a>
	</c:if>
	<c:if test="${to eq 'createorder' }">
	<a href="createorder.jsp">返回订单修改界面</a>
	</c:if>
	
	<c:if test="${sessionScope.admin.power eq '1' }"><!-- 管理员权限显示以下内容 -->
		<c:if test="${to eq 'changepwd' }">
		<a href="changepwd.jsp">返回密码修改界面</a>
		</c:if>
		<c:if test="${to eq 'order' }">
		<a href="order.jsp">前往卖家后台</a>&nbsp;
		<a href="good.jsp">前往商品界面</a>
		</c:if>
		<c:if test="${to eq 'upload_goods' }">
		<a href="upload_goods.jsp">返回填写界面</a>
		</c:if>
		<c:if test="${to eq 'upload_moregoods' }">
		<a href="upload_moregoods.jsp">返回填写界面</a>
		</c:if>
	</c:if>
</c:if>

</body>
</html>