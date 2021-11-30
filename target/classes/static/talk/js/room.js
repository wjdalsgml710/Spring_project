var ws;		
	window.onload = function(){   
		// window.onload라는 메서드를 오버라이딩(재정의) 해주면 되는데, 해당 함수 내의 코드 스크립트는 웹브라우저 내의 모든 요소가 준비가 되어야 실행이 되도록 할수 있다.
		// (웹브라우저 자체를 담당하는 window라는 객체가, 웹 문서를 불러올때, 문서가 사용되는 시점에 실행되는 onload라는 함수를 내가 다시 재정의 한다는 개념이다.)
		console.log("window.onload start...");
		getRoom();
		createRoom();
	}

	function getRoom(){
		console.log("getRoom start...");
		commonAjax(_contextPath+'/talk/getRoom', "", 'post', function(result){
			createChatingRoom(result);
		});
	}
	
	function createRoom(){
		console.log("createRoom start...");
		$("#createRoom").click(function(){
			var msg = {	roomName : $('#roomName').val()	};
			commonAjax(_contextPath+'/talk/createRoom', msg, 'post', function(result){
				createChatingRoom(result);
			});

			$("#roomName").val("");
		});
	}

	function goRoom(number, name){
		console.log("goRoom");
		location.href=_contextPath+"/talk/moveChating?roomName="+name+"&"+"roomNumber="+number;
	}
	

	function createChatingRoom(res){
		console.log("createChatingRoom");
		if(res != null){
			var tag = "<tr><th class='num'>순서</th><th class='room'>방 이름</th><th class='go'></th></tr>";
			res.forEach(function(d, idx){
				var rn = d.roomName.trim();
				var roomNumber = d.roomNumber;
				tag += "<tr>"+
							"<td class='num'>"+(idx+1)+"</td>"+
							"<td class='room'>"+ rn +"</td>"+
							"<td class='go'><button type='button' onclick='goRoom(\""+roomNumber+"\", \""+rn+"\")'>참여</button></td>" +
						"</tr>";	
			});
			$("#roomList").empty().append(tag);
		}
	}

	function commonAjax(url, parameter, type, calbak, contentType){
		$.ajax({
			url: url,
			data: parameter,
			type: type,
			contentType : contentType!=null?contentType:'application/x-www-form-urlencoded; charset=UTF-8',
			success: function (res) {
				calbak(res);
			},
			error : function(err){
				console.log('error');
				calbak(err);
			}
		});
	}