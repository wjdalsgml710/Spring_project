
	
	$(document).ready(function() {
		$("#button1").click(function() {
			var submenu = $(this).next("#scroll");
			// submenu 가 화면상에 보일때는 위로 보드랍게 접고 아니면 아래로 보드랍게 펼치기
			if (submenu.is(":visible")) {
				$('#img1').css({
					'transform' : 'rotate(0deg)'
				});
				submenu.slideUp("fast");
			} else {
				$('#img1').css({
					'transform' : 'rotate(90deg)'
				});
				submenu.slideDown("fast");
			}
		})
	});
	$(document).ready(function() {
		$("#button2").click(function() {
			var submenu = $(this).next("#scroll");
			// submenu 가 화면상에 보일때는 위로 보드랍게 접고 아니면 아래로 보드랍게 펼치기
			if (submenu.is(":visible")) {
				$('#img2').css({
					'transform' : 'rotate(0deg)'
				});
				submenu.slideUp("fast");
			} else {
				$('#img2').css({
					'transform' : 'rotate(90deg)'
				});
				submenu.slideDown("fast");
			}
		})
	});
	
		// div side 없애기
	function doShow() {
		if ($('#side').is(":visible")) {
			$('#side').hide();
			$('#content').css({
				"width" : "100%"
			});
		} else {
			$('#side').show();
			$('#content').css({
				"width" : "84%"
			});
		}
	}