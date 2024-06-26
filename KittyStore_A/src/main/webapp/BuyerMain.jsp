<%--
  Created by IntelliJ IDEA.
  User: banana
  Date: 2023/10/18
  Time: 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<!--     <link rel="stylesheet" type="text/css" href="BMain.css"/> -->
    <title>买家购物首页</title>
    <meta charset="UTF-8">
</head>

<style>

*{
    background-color: #FFF9F1;
    
}
body {
    display: block;
}
.left{
    /* 买家导航 */
    width: 287px;
    height:100vh;
    background-color: rgba(61, 61, 61, 0.33);
    position: relative;
    float: left;
    align-content: center;
}
.head1{
    background-color: rgba(61, 61, 61, 0);
    position: relative;
    top: 30px;
    left: 70px;
}
.daohang{
    background-color: rgba(0, 0, 0, 0);
    width: 200px;/*格子宽度*/
    position: relative;
    left: 30px;
}
.head2{
    background-color: rgba(61, 61, 61, 0.33);
    text-align: center;
    vertical-align: top;
    font-size:36px;
    color: white;
    height: 100px;/*格子高度*/
}
.head4{
    background-color: rgba(61, 61, 61, 0.33);
    text-align: center;
    height: 100px;/*格子高度*/
}
.head4-1{
    background-color: rgba(61, 61, 61, 0);
    text-decoration: none;
    color: #ffffff;
    font-size:28px;
    font-weight: bold;
}
.head5{
    background-color: rgba(61, 61, 61, 0.33);
    text-align: center;
    height: 100px;/*格子高度*/
    bottom: 0;
    
}
.head5-1{
    background-color: rgba(61, 61, 61, 0);
    text-decoration: none;
    color: #585655;
    font-size:28px;
    font-weight: bold;
}

/* 商品 */
.right{
    /* 商品显示 
    width: 1340px;*/
    height: 100vh;
    /* background-color: aquamarine; */
    position: absolute;/*绝对定位*/
    left: 350px; 
    
    float: right;
}


.goods {
    display: flex; /*使用flex布局*/
    flex-wrap: wrap; /*允许元素换行*/
    justify-content: space-between; /*元素之间留有空隙*/
    border: 1px solid #000; /*添加边框*/
}

.show-1 {
    flex: 0 0 30%; /*每个元素占用30%的宽度，这样每行就可以放3个元素*/
    border: 1px solid #000; /*添加边框*/
    margin-bottom: 10px; /*添加底部边距*/
}

.picture {
    text-align: center; /*图片居中*/
}

.price {
    font-size: 20px;
    height: 20px;
}
.pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 20px;
    }
    .prev, .next {
    	background-color: rgb(237, 196, 110);
    }
form {
    display: flex; /* 让表单内的元素在同一行显示 */
       width:600px;
    height:45px;
	}
	input[type="text"] {
	    flex-grow: 1; /* 让搜索框占据剩余的空间 */
	}
	input[name="keyword"] {
	    width: 100%;
	    padding: 10px; 
	    border: 1px solid #ccc; 
	    border-radius: 4px;
	}	
	#search_list {
		position: fixed;
		top: 110px;
		left: 350px;
		width: 400px;
		background-color: white;
	}

	
	#search_list div {
	    border-bottom: 1px solid black; 
	}
	 .media-container {
        position: relative;
        width: 100px;
        height: 200px;
        overflow: hidden;
    }
    .media-container img, .media-container video {
        display: none;
        width: 100%;
        height: 100%;
        object-fit: contain;
    }
    .media-container img.active, .media-container video.active {
        display: block;
    }
    .media-container button {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        background: rgba(255, 255, 255, 0.7);
    }
    .media-container .prev-button {
        left: 10px;
    }
    .media-container .next-button {
        right: 10px;
    }
</style>
<%
    int currentPage = 1;
    if (request.getParameter("currentPage") != null) {
        currentPage = Integer.parseInt(request.getParameter("currentPage"));
    }
    request.setAttribute("currentPage", currentPage);
%>
<script >
	var currentPage = ${requestScope.currentPage};
	var totalItems = ${sessionScope.gL.size()}; // 商品总数
	var itemsPerPage = 6; // 每页显示的商品数量
	var totalPages = Math.ceil(totalItems / itemsPerPage); 
	
	function goToPrevPage() {
	    if (currentPage > 1) {
	        currentPage--;
	        location.href = "BuyerMain.jsp?currentPage=" + currentPage;
	    }
	}
	
	function goToNextPage() {
	    if (currentPage < totalPages) {
	        currentPage++;
	        console.log(currentPage);
	        location.href = "BuyerMain.jsp?currentPage=" + currentPage;
	    }
	}
	
	function search() {
        var keyword = document.getElementsByName('keyword')[0].value;
        var kind = document.getElementsByName('search_kind')[0].value;
        var xhr = new XMLHttpRequest();// 使用Ajax发送请求

        xhr.open('POST', 'searchgoodservlet', true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.overrideMimeType("application/json; charset=UTF-8");

        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && xhr.status == 200) {
                var results= JSON.parse(xhr.responseText);// 当请求成功时，使用返回的数据更新搜索结果列表
                var resultsDiv = document.getElementById('search_list');
                resultsDiv.innerHTML = '';
                for (var i = 0; i < results.length; i++) {
                    var div = document.createElement('div');
                    div.textContent = results[i];
                    resultsDiv.appendChild(div);
                }
            }
        };
        xhr.send('keyword=' + encodeURIComponent(keyword) + '&search_kind=' + encodeURIComponent(kind));
    }
	window.onload = function() {
        var containers = document.querySelectorAll('.media-container');
        containers.forEach(function(container) {
            var mediaFiles = container.querySelectorAll('img, video');
            var index = 0;

            function updateMedia() {
                mediaFiles.forEach(function(file, i) {
                    file.classList.remove('active');
                    if (i === index) {
                        file.classList.add('active');
                    }
                });
            }

            container.querySelector('.prev-button').addEventListener('click', function() {
                index = (index - 1 + mediaFiles.length) % mediaFiles.length;
                updateMedia();
            });

            container.querySelector('.next-button').addEventListener('click', function() {
                index = (index + 1) % mediaFiles.length;
                updateMedia();
            });

            mediaFiles.forEach(function(file) {
                file.addEventListener('click', function() {
                    var modal = document.querySelector('#modal');
                    var modalImage = modal.querySelector('#modalImage');
                    var modalVideo = modal.querySelector('#modalVideo');
                    if (file.tagName === 'VIDEO') {
                        modalImage.style.display = 'none';
                        modalVideo.src = file.src;
                        modalVideo.style.display = 'block';
                    } else {
                        modalVideo.style.display = 'none';
                        modalImage.src = file.src;
                        modalImage.style.display = 'block';
                    }
                    modal.style.display = 'block';
                });
            });

            updateMedia();
        });

        var modal = document.querySelector('#modal');
        modal.querySelector('.close-button').addEventListener('click', function() {
            modal.style.display = 'none';
        });
    };
</script>
<body style="margin: 0px;">
	<div class="left" ><!-- 买家导航 -->
	    <!-- <hr class="head3" color=#FFF2E1 width="230px" size="2px" > -->
	    <table class="daohang">
	    	<img class="head1" src="img/buyer/head.png" alt=""  >	
	    	<c:if test="${not empty sessionScope.admin }">
		        <tr>
		            <td class="head2">${sessionScope.admin.username}</td>
		        </tr>
		        <tr>
		            <td class="head4"><a  href="userorderservlet?userId=${sessionScope.admin.username}" class="head4-1">历史购买记录</a></td>
		        </tr>
		        <tr >
		            <td class="head5"><a href="quitloginservlet" class="head5-1">退出登录</a></td>
		        </tr>
		    </c:if>
		    <c:if test="${empty sessionScope.admin }">
		    	<tr>
		            <td class="head2"> 游客</td>
		        </tr>
		        <tr>
		            <td class="head4"><a   class="head4-1">其他功能后登录使用</a></td>
		        </tr>
		        <tr >
		            <td class="head5"><a href="index.jsp" class="head5-1">返回登录</a></td>
		        </tr>
		    </c:if>
	    </table>
	
	</div>
	
	<div class="right">
		<center>
			<h2>欢迎来到猫咪美食坊！</h2>
            <form action="successsearchservlet" method="post">
				<input type="text" name="keyword" placeholder="搜索商品" oninput="search()" >&nbsp;&nbsp;&nbsp;
				<select name="search_kind" id="search_kind">
                    <option value="猫咪主粮">猫咪主粮</option>
                    <option value="猫咪零食">猫咪零食</option>
                    <option value="猫咪日用">猫咪日用</option>
                </select><br><br>&nbsp;&nbsp;&nbsp;
				<input type="submit" value="搜索" style="width:130px">
				<input type="hidden" id="ishistory" name="ishistory" value="0">
			</form>
			<div id="search_list"></div>  
		</center>
	    <div class="goods">
	    	<c:forEach items="${sessionScope.gL}" var="g" begin="${(currentPage-1)*6}" end="${currentPage*6-1}">
	    		<div  class="show-1">
		    		<div>
		    			<c:set var="mediaFiles" value="${fn:split(g.picture, ',')}" /> <!-- 分割媒体文件路径字符串 -->
				        <div class="media-container">
				            <button class="prev-button">＜</button>
				            <button class="next-button">＞</button>
				            <c:forEach var="file" items="${mediaFiles}"> <!-- 遍历媒体文件路径数组 -->
				                <c:choose>
				                    <c:when test="${fn:endsWith(file, '.mp4')}"> <!-- 如果文件是MP4视频 -->
				                        <video src="${file}" controls width="200"></video>
				                    </c:when>
				                    <c:otherwise> <!-- 否则，我们假设文件是图片 -->
				                        <img src="${file}" alt="" width="200">
				                    </c:otherwise>
				                </c:choose>
				            </c:forEach>
				        </div>
				        <a href="showgoodservlet?goodid=${g.goodid}">
		    			<p>商品名：${g.goodname}<br>超值价：${g.price}元</p>
		    			</a>
		    		</div>
	    		</div>
	    	</c:forEach>
	    </div>
	    <div class="pagination">
		    <button class="prev" onclick="goToPrevPage()">上一页</button>
		    <span id="page-info">第${currentPage}页 </span>
		    <%-- 当前页码小于总页数时，才显示“下一页”按钮 --%>
		   	<button class="next" onclick="goToNextPage()">下一页</button>
		</div>
	</div>
</body>
</html>
