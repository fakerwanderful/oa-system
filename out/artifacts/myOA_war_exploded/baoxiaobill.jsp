<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<base href="${basePath}">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>流程管理</title>

<!-- Bootstrap -->
<link href="bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="css/content.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script src="js/jquery.min.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function() {
		$(".btn-danger").click(function() {
			var a = $(this).attr("href");
			var b = $(this).attr("id");
			if (confirm("确认删除【" + b + "】吗？")) {
				//确认，发送ajax请求删除即可
				$.ajax({
					url : a,
					type : "GET",
					dataType : "text",
					success : function(result) {

						/* 刷新页面,浏览器上的刷新页面按钮  */
						location.reload();

					}
				});
			}

		});

	});
</script>
</head>
<body>

	<!--路径导航-->
	<ol class="breadcrumb breadcrumb_nav">
		<li>首页</li>
		<li>报销管理</li>
		<li class="active">我的报销单</li>
	</ol>
	<!--路径导航-->

	<div class="page-content">
		<form class="form-inline">
			<input type="hidden" name="pageNum" value="${pageInfo.pageNum}">
			<div class="panel panel-default">
				<div class="panel-heading">报销单列表</div>

				<div class="table-responsive">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th width="5%">ID</th>
								<th width="10%">报销金额</th>
								<th width="15%">标题</th>
								<th width="20%">备注</th>
								<th width="15%">时间</th>
								<th width="10%">状态</th>
								<th width="25%">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="bill" items="${baoxiaobillList}">
								<tr>
									<td>${bill.id}</td>
									<td>${bill.money}</td>
									<td id="tit">${bill.title}</td>
									<td>${bill.remark}</td>
									<td><fmt:formatDate value="${bill.creatdate}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td><c:if test="${bill.state==1}">
				        	   		审核中
				        	   </c:if> <c:if test="${bill.state==2}">
				        	  		审核完成
				        	   </c:if></td>
									<td><c:if test="${bill.state==1}">
											<a
												href="viewHisComment?id=${bill.id}"
												class="btn btn-success btn-xs"><span
												class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>
											<a
												href="viewCurrentImageByBill?billId=${bill.id}"
												target="_blank" class="btn btn-success btn-xs"><span
												class="glyphicon glyphicon-eye-open"></span> 查看当前流程图</a>
										</c:if> <c:if test="${bill.state==2}">
											<div id="${bill.title}" href="deleteBaoXiaoById?id=${bill.id}"
												class="btn btn-danger btn-xs">
												<span class="glyphicon glyphicon-remove"></span> 删除
											</div>
											<a
												href="viewHisComment?id=${bill.id}"
												class="btn btn-success btn-xs"><span
												class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>
										</c:if></td>
								</tr>

							</c:forEach>
						</tbody>
					</table>

				</div>

			</div>
		</form>

		<!-- 显示分页信息 -->
		<div class="row text-center">
			<nav aria-label="Page navigation" >
				<ul class="pagination pagination-lg">
<%--					<c:if test="${pageInfo.pageNum > 1}">--%>
						<li class="${pageInfo.hasPreviousPage ? '':'disabled'}">
							<a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=1" aria-label="Previous">
								<span aria-hidden="true">首页</span>
							</a>
						</li>
						<li class="${pageInfo.hasPreviousPage ? '':'disabled'}">

							<a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=${pageInfo.pageNum-1}" aria-label="Previous">
								<span aria-hidden="true">&laquo;</span>
							</a>
						</li>
<%--					</c:if>--%>

					<c:forEach items="${pageInfo.navigatepageNums}" var="arr">
						<c:if test="${pageInfo.pageNum == arr}">
							<li class="active"><a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=${arr}">${arr}</a></li>
						</c:if>

						<c:if test="${pageInfo.pageNum != arr}">
							<li ><a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=${arr}">${arr}</a></li>
						</c:if>

					</c:forEach>

<%--					<c:if test="${pageInfo.pageNum < pageInfo.pages}">--%>
						<li class="${pageInfo.hasNextPage ? '':'disabled'}">
							<a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=${pageInfo.pageNum+1}" aria-label="Next">
								<span aria-hidden="true">&raquo;</span>
							</a>
						</li>
						<li class="${pageInfo.hasNextPage ? '':'disabled'}">
							<a href="${pageContext.request.contextPath}/myBaoxiaoBill?pageNum=${pageInfo.pages}" aria-label="Next">
								<span aria-hidden="true">末页</span>
							</a>
						</li>
<%--					</c:if>--%>



				</ul>
			</nav>

			<!--分页文字信息  -->

			<div id="page_info_area">当前第${pageInfo. pageNum}页,总${pageInfo. pages}页,总${pageInfo.total}条记录</div>

		</div>

	</div>


</body>
</html>