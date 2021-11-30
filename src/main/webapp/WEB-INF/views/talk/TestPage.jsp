<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file = "/WEB-INF/views/header/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
<%@include file = "/WEB-INF/views/header/headerHead.jsp" %>
<link rel="styleSheet" href="${pageContext.request.contextPath}/talk/css/TestPage.css">
<script src="${pageContext.request.contextPath}/talk/js/TestPage.js"></script>
<script src="${pageContext.request.contextPath}/talk/js/moment.js"></script>
<title>Insert title here</title>
<link rel="styleSheet" href="${pageContext.request.contextPath}/talk/css/MemberList.css">
</head>
<body>
<%@include file = "/WEB-INF/views/header/headerBody.jsp" %>
	<div id="side">
	</div>	
	<div id="TestCircle">
		<button type="button" id="Test"></button>
	</div>
	<!-- Modal content -->
	<div id="my_Modal" class="chatModal">
		<div id="content1">
			<div id="room1">
				<div id="logo">
					<div class="img">
						<img alt="로고.jpg" src="${pageContext.request.contextPath}/talk/img/로고.png">
					</div>
					<div class="h5"><b>TEAM</b></div>
					<div class="h6"><b>works</b></div>
				</div>
				<div id="roomContainer" class="roomContainer">
					<table id="memberlist" class="memberlist">
					<tr><th>이름</th>
</tr>
					</table>
					<table id="groupRoomlist">
					<tr><th>방 이름</th></tr>
					</table>
				</div>
				<button type="button" class="onebyone">1:1</button>
				<button type="button" class="group">그룹</button>
			</div>
			<button type="button" class="groupchat">그룹 방만들기</button>
		</div>
		<div id="content2">
			<div id="makegroup"></div>
			<button type="button" onclick="javascript:talkGroup()" class="insertroom">방만들기</button>
		</div>
		<div id="chatting_wrap">
			<div class="container">
				<h1 id="roomName"></h1>
				<input type="hidden" id="sessionId" value="">
				<input type="hidden" id="m_id">
				<input type="hidden" id="m_name">
				<input type="hidden" id="roomNumber">
				<div id="meName"></div>
				<div id="chating" class="chating">
				</div>
				<div id="yourMsg">
					<table class="inputTable">
						<tr>
							<th>메시지</th>
							<th><input id="chatting" placeholder="보내실 메시지를 입력하세요."></th>
							<th><button onclick="send()" id="sendBtn">전송</button></th>
						</tr>
						<tr>
							<th>파일</th>
							<th><input  type="file" id="fileUpload"></th>
							<th><button onclick="fileSend()" id="sendFileBtn"><img alt="다운.png" src="${pageContext.request.contextPath}/talk/img/다운.png"></button></th>
						</tr>
					</table>
				</div>
				<button type="button" id="back" value="돌아가기">돌아가기</button>
				<button type="button" id="join_member" onclick="join_member($('#roomNumber').val())">참여자</button>
				<table id="join_member_list"></table>
			</div>
		</div>
	</div>

<%@include file = "/WEB-INF/views/header/headerFooter.jsp" %>
</body>
</html>