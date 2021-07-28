jQuery(document).ready(function($) {
	// Back to top button
	$(window).scroll(function() {
		if ($(this).scrollTop() > 100) {
			$('.back-to-top').fadeIn('slow');
		} else {
			$('.back-to-top').fadeOut('slow');
		}
	});
	$('.back-to-top').click(function() {
		$('html, body').animate({ scrollTop: 0 }, 1500, 'easeInOutExpo');
		return false;
	});

	// Initiate superfish on nav menu
	$('.nav-menu').superfish({
		animation: {
			opacity: 'show'
		},
		speed: 200
	});

	// Mobile Navigation
	if ($('#nav-menu-container').length) {
		var $mobile_nav = $('#nav-menu-container').clone().prop({
			id: 'mobile-nav'
		});
		$mobile_nav.find('> ul').attr({
			'class': '',
			'id': ''
		});
		$('#header').append($mobile_nav);
		$('#header').prepend('<button type="button" id="mobile-nav-toggle"><i class="las la-bars"></i></button>');
		$('#header').append('<div id="mobile-body-overly"></div>');
		$('#mobile-nav').find('.menu-has-children').prepend('<i class="las la-angle-down"></i>');

		$(document).on('click', '.menu-has-children i', function(e) {
			$(this).next().toggleClass('menu-item-active');
			$(this).nextAll('ul').eq(0).slideToggle();
			$(this).toggleClass("la-angle-up la-angle-down");
		});

		$(document).on('click', '#mobile-nav-toggle', function(e) {
			$('body').toggleClass('mobile-nav-active');
			$('#mobile-nav-toggle i').toggleClass('la-times la-bars');
			$('#mobile-body-overly').toggle();
		});

		$(document).click(function(e) {
			var container = $("#mobile-nav, #mobile-nav-toggle");
			if (!container.is(e.target) && container.has(e.target).length === 0) {
				if ($('body').hasClass('mobile-nav-active')) {
					$('body').removeClass('mobile-nav-active');
					$('#mobile-nav-toggle i').toggleClass('la-times la-bars');
					$('#mobile-body-overly').fadeOut();
				}
			}
		});
	} else if ($("#mobile-nav, #mobile-nav-toggle").length) {
		$("#mobile-nav, #mobile-nav-toggle").hide();
	}

	// Smooth scroll for the menu and links with .scrollto classes
	$('.nav-menu a, #mobile-nav a, .scrollto').on('click', function() {
		if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
			var target = $(this.hash);
			if (target.length) {
				var top_space = 0;

				if ($('#header').length) {
					top_space = $('#header').outerHeight();

					if (!$('#header').hasClass('header-fixed')) {
						top_space = top_space - 20;
					}
				}

				$('html, body').animate({
					scrollTop: target.offset().top - top_space
				}, 1500, 'easeInOutExpo');

				if ($(this).parents('.nav-menu').length) {
					$('.nav-menu .menu-active').removeClass('menu-active');
					$(this).closest('li').addClass('menu-active');
				}

				if ($('body').hasClass('mobile-nav-active')) {
					$('body').removeClass('mobile-nav-active');
					$('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
					$('#mobile-body-overly').fadeOut();
				}
				return false;
			}
		}
	});

	// Header scroll class
	$(window).scroll(function() {
		if ($(this).scrollTop() > 20) {
			$('#header').addClass('header-scrolled');
		} else {
			$('#header').removeClass('header-scrolled');
		}
	});

	// minimum setup
	$('.selectpicker').selectpicker({
		size: '6',
		liveSearch: true,
		container: "body"
	});
	$('.selectpickernone').selectpicker({
		size: '6',
		container: "body"
	});

	// Date time
	$('.dateranger-one .date-use').prop('readonly', true);
	$('.dateranger-one').daterangepicker({
		opens: 'right',
		autoApply: true,
		singleDatePicker: true
	}, function(start, end, label) {
		$('.dateranger-one .date-use').val(start.format('YYYY-MM-DD'));
	});

	let timeNow = $("#timeNow").html()
	let CountDownTime = function(timeElement) {
		let countDownDate = new Date(timeElement.getAttribute("data-action-time")+":00z");
		/*if (Number.isNaN(countDownDate.getTime())) { // Safari browser cannot parse above date
			countDownDate = new Date((timeElement.getAttribute("data-action-time") + ":00"));
		};
		countDownDate = new Date(countDownDate.getTime() + (countDownDate.getTimezoneOffset()*60*1000));*/
		let distance = countDownDate - timeNow;
		if (distance <= 1000 * 60 * 60) {
			$('.notice-time-rest').addClass('d-none');
		}
		let x = setInterval(function() {
			distance -= 1000;
			let days = Math.floor(distance / (1000 * 60 * 60 * 24));
			let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
			let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
			let seconds = Math.floor((distance % (1000 * 60)) / 1000);
			if (hours < 10) hours = `0${hours}`
			if (minutes < 10) minutes = `0${minutes}`
			if (seconds < 10) seconds = `0${seconds}`
			timeElement.querySelectorAll('.time-num')[0].innerHTML = days;
			timeElement.querySelectorAll('.time-num')[1].innerHTML = hours;
			timeElement.querySelectorAll('.time-num')[2].innerHTML = minutes;
			timeElement.querySelectorAll('.time-num')[3].innerHTML = seconds;
			if (distance < 0) {
				clearInterval(x);
				timeElement.querySelectorAll('.time-num')[0].innerHTML = "00";
				timeElement.querySelectorAll('.time-num')[1].innerHTML = "00";
				timeElement.querySelectorAll('.time-num')[2].innerHTML = "00";
				timeElement.querySelectorAll('.time-num')[3].innerHTML = "00";
			}
		}, 1000);
	}
	
	// Countdown timer
	let timeElement = $(".time-box");
	for (let i = 0; i < timeElement.length; i++) {
		CountDownTime(timeElement.get(i));
	}
	var intervalTime;
	let CountDownTimeRest = function(timeElement) {
		let countDownDate = new Date(timeElement.getAttribute("data-action-time")+"00z");
		if (Number.isNaN(countDownDate.getTime())) { // Safari browser cannot parse above date
			countDownDate = new Date((timeElement.getAttribute("data-action-time") + ":00"));
		};
		countDownDate = new Date(countDownDate.getTime() + (countDownDate.getTimezoneOffset()*60*1000));
		let distance = countDownDate - timeNow + 1000 * 60 * 60;
		intervalTime = setInterval(function() {
			distance -= 1000;
			let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
			let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
			let seconds = Math.floor((distance % (1000 * 60)) / 1000);
			if (hours < 10) hours = `0${hours}`
			if (minutes < 10) minutes = `0${minutes}`
			if (seconds < 10) seconds = `0${seconds}`
			timeElement.querySelectorAll('.time-num')[0].innerHTML = hours;
			timeElement.querySelectorAll('.time-num')[1].innerHTML = minutes;
			timeElement.querySelectorAll('.time-num')[2].innerHTML = seconds;
			if (distance < 0) {
				clearInterval(intervalTime);
				timeElement.querySelectorAll('.time-num')[0].innerHTML = "00";
				timeElement.querySelectorAll('.time-num')[1].innerHTML = "00";
				timeElement.querySelectorAll('.time-num')[2].innerHTML = "00";
			}
		}, 1000);
	}
	
	if ($(".time-box-rest").size() !== 0) {
		CountDownTimeRest($(".time-box-rest").get(0));
	}

	// Format currency
	for (let i = 0; i < $('.cur-format').length; i++) {
		$('.cur-format').eq(i).html(Math.floor(Number($('.cur-format').eq(i).html())).toLocaleString('en'));
		$('.cur-format').eq(i).val(Math.floor(Number($('.cur-format').eq(i).val())).toLocaleString('en'));
	}

	// Format localedate time
	for (let i = 0; i < $('.time-format').length; i++) {
		let d = $('.time-format').eq(i).html();
		let timeArr = d.split(/[^0-9]/);

		let dateStr = timeArr[3] + ':' + timeArr[4] + ' ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0];
		$('.time-format').eq(i).html(dateStr);
	}
	
	// add active carousel into first item
	if ($('.carousel-inner .carousel-item').length > 0) {
		$('.carousel-inner .carousel-item').eq(0).addClass('active');
	}

	//log out
	$('.nav-logout').click(function() {
		setCookie('AC-ACCESS-KEY', '', 0);
		setCookie('balance','', 0);
	});

	if (checkUserLoggedIn()) {
		// add register auction
		if ($('.UserId').html()) {
			fetch('/auction_registrations/user/' + $('.UserId').html())
				.then(res => res.json())
				.then(results => {
					if (results) {
						for(let i = 0; i < results.length; i++) {
							if (results[i].isDeleted === false) {
								$('#' + results[i].auction.id + ' .registered').removeClass('d-none');
								$('#' + results[i].auction.id + ' .RegisterAuction').css('display','none');
								$('#' + results[i].auction.id + ' .CancelButton').removeClass('d-none');
							}
						}
					}
				})
				.catch(error => {
					console.log("Error: " + error);
			});
		}
			
		// add liked auction 
		fetch('/user/liked_auction')
			.then(res => res.json())
			.then(results => {
				if (results) {
					for(let i = 0; i < results.length; i++) {
						if (results[i].delete == false) {
							$('#' + results[i].auction.id + ' .like-box').addClass('liked');
							$('.item#' + results[i].auction.id + ' .like-box').addClass('liked');
							$('#' + results[i].auction.id + ' .LikeAuction').hide();
							$('#' + results[i].auction.id + ' .LikedAuction').removeClass('d-none');
						}
					}
				}
			})
			.catch(error => {
					console.log("Error: " + error);
			});
		}
	
	// add like auction
	let toggleLike = true;
	$('.like-box').click(function() {
		if (!checkUserLoggedIn()) $('#AlertSignIn').click();
		else if (toggleLike) {
			toggleLike = false;
			if ($(this).hasClass('liked')) {
				$('.item#' + $(this).attr('data-id') + ' .like-box').removeClass('liked');
				$('#' + $(this).attr('data-id') + ' .like-box').removeClass('liked');
				$('#' + $(this).attr('data-id') + ' .LikeAuction').show();
				$('#' + $(this).attr('data-id') + ' .LikedAuction').addClass('d-none');
				fetch('/user/liked_auction/auction/' + $(this).attr('data-id'),{
					method: 'DELETE',
				})
					.then(res => res.text())
					.then(result => {
						if (result == "OK") {
							toggleLike = true;
								if (window.location.pathname === "/dau-gia-yeu-thich") {
									location.reload();
								}
						}
					})
					.catch(error => {
						console.log("Error: " + error);
					});
			}
			else {
				$('#' + $(this).attr('data-id') + ' .like-box').addClass('liked');
				$('.item#' + $(this).attr('data-id') + ' .like-box').addClass('liked');
				$('#' + $(this).attr('data-id') + ' .LikeAuction').hide();
				$('#' + $(this).attr('data-id') + ' .LikedAuction').removeClass('d-none');
				fetch('/user/liked_auction/add/' + $(this).attr('data-id'))
					.then(res => res.text())
					.then(result => {
						if (result == "OK") {
							toggleLike = true;
								if(window.location.pathname === "/dau-gia-yeu-thich"){
									location.reload();
								}
						}
					})
			}
		}
	})
	
	//delete shipping address
	$('.delete-address').click(function() {
	if (!checkUserLoggedIn()) $('#AlertSignIn').click();
		fetch('/user/address/'+$(this).attr('data-id'), {
             method: 'DELETE',
             headers: {
              'Content-Type': 'application/json'
            }})	        
		.then(res => res.text())
		.then(results => {
		if(results == "OK"){
			window.location.assign("/dia-chi-van-chuyen");		
		}
		})
		.catch(error => {
		console.log("Error: " + error);
		});
	});
	
	//connect MQTT
	MQTTconnect();
	function MQTTconnect() {
		// Create a client instance
		client = new Paho.MQTT.Client("mqtt.acscan.net", 8084, "/mqtt", generateUUID());
		// set callback handlers
		client.onConnectionLost = onConnectionLost;
		client.onMessageArrived = onMessageArrived;
	
		// connect the client
		client.connect({
			onSuccess: onConnect, 
			userName: "frontend",
			password: "DDgg8WHv8YLdp9t",
			useSSL: true,
			onFailure: onFailure
		});
	
	
		// called when the client connects
		function onConnect() {
			// Once a connection has been made, make a subscription and send a message.
			client.subscribe("auction/bid");
			client.subscribe("auction/win");
			client.subscribe("auction/status");
			client.subscribe("auction/registration");
			client.subscribe("auction/cancelRegistration");
			client.subscribe("auction/balance");
		}
	
		// called when the client loses its connection
		function onConnectionLost(responseObject) {
			if (responseObject.errorCode !== 0) {
				console.log("onConnectionLost:" + responseObject.errorMessage);
			}
			setTimeout(MQTTconnect, 100);
		}
		function onFailure(message) {
			setTimeout(MQTTconnect, 100);
		}
	
		// called when a message arrives
		function onMessageArrived(message) {	
			
			if (message.destinationName == "auction/balance") { 
				let results = JSON.parse(message.payloadString);
				if (results.userId === $('.UserId').html()) location.reload();				
			}		
		
			if (message.destinationName == "auction/bid") { 
				let results = JSON.parse(message.payloadString);
				
				$(`[data-likeId=${results.auction}]`).html(results.price);
				$(`[data-biddingId=${results.auction}]`).html(results.price);
				
				$(".item#" + results.auction + " .curPrice").html(results.price);
				if (window.location.pathname === "/chi-tiet-dau-gia/" +  results.auction || 
					window.location.pathname === "/") {
					$('.item#' + results.auction + ' .auction-bid-price').prop('Counter', parseInt($('.item#' + results.auction + ' .auction-bid-price').eq(0).html().replace(/,/g, ''))).animate({
						Counter: results.price
					}, {
						duration: 2000,
						easing: 'swing',
						step: function(now) {
							$(this).text(Math.ceil(now).toLocaleString('en'));
						}
					});
				}
				
				if (window.location.pathname === "/chi-tiet-dau-gia/" +  results.auction) {
					$('.item#' + results.auction + ' .BidPrice').prop('Counter', parseInt($('.item#' + results.auction + ' .BidPrice').eq(0).html().replace(/,/g, ''))).animate({
						Counter: parseInt(results.price) + parseInt($('.BidStep').val()) * parseInt($('.StepPrice').html().replace(/,/g, ''))
					}, {
						duration: 2000,
						easing: 'swing',
						step: function(now) {
							$(this).text(Math.ceil(now).toLocaleString('en'));
						}
					});
					
					// count down time when user bid auction
					let countDownDate = new Date($(".item#" + results.auction + " .time-box").get(0).getAttribute("data-action-time")+":00z");
					if (Number.isNaN(countDownDate.getTime())) { // Safari browser cannot parse above date
						countDownDate = new Date((timeElement.getAttribute("data-action-time") + ":00"));
					};
					countDownDate = new Date(countDownDate.getTime() + (countDownDate.getTimezoneOffset()*60*1000));
					let distance = countDownDate - timeNow;
					clearInterval(intervalTime);
					if (distance > 1000 * 60 * 60) {
						if ($('.item#' + results.auction + ' .notice-time-rest').hasClass('d-none')) {
							$('.item#' + results.auction + ' .notice-time-rest').removeClass('d-none');
							$('.item#' + results.auction + ' .time-box-rest').eq(0).attr("data-action-time", results.created);
							CountDownTimeRest($(".time-box-rest").get(0));
						}
						else { 
							$('.time-box-rest').eq(0).attr("data-action-time", results.created);
							CountDownTimeRest($(".time-box-rest").get(0));
						}
					}
					else {
						$('.item#' + results.auction + ' .notice-time-rest').addClass('d-none');
					}
				}
				
			 	// alert to user bid highest			
				$('.item#' + results.auction + ' #ContextualStatus').hide();
				if (results.user === $('.UserId').html()) {
					$('.item#' + results.auction + ' #ContextualStatus').addClass('alert alert-success');
					$('.item#' + results.auction + ' #ContextualStatus').removeClass('alert-danger');
					$('.item#' + results.auction + ' #ContextualStatus').html('Bạn là người thắng đấu giá hiện tại.');
					$('.item#' + results.auction + ' #ContextualStatus').fadeTo(1000, 1, function () {
		                $(this).slideDown(500);
		            });
				}
				else {
					$('.item#' + results.auction + ' #ContextualStatus').addClass('alert alert-danger');
					$('.item#' + results.auction + ' #ContextualStatus').removeClass('alert-success');
					$('.item#' + results.auction + ' #ContextualStatus').html('Bạn chưa phải là người đấu giá cao nhất. Bạn có thể tăng giá đấu bất kì lúc nào.');
					$('.item#' + results.auction + ' #ContextualStatus').fadeTo(1000, 1, function () {
		                $(this).slideDown(500);
		            });
				}
				
				// add new bid to bid history
				let newBid = "<li class='list-group-item border-0 pl-0 pr-0 pt-1 pb-1 bg-transparent'>"
		                      +"    <span class='d-block d-sm-inline'>"
		                      +"        <span class='font-weight-600'>" + results.username + "</span>"
		                      +"       <span class='f-11 text-muted time-ago' data-time='" + results.created + "'>" + timeSince(results.created) + "</span>"
		                      +"    </span>"
		                      +"    <span class='d-block d-sm-inline font-weight-600 text-danger float-sm-right f-14'><span>" + Number(results.price).toLocaleString('en') +"</span> VNDT</span>"
		                      +"</li>";
				$('#' + results.auction + ' .list-group.list-group-flush.list-bid-histoty').prepend(newBid);
				for(let i = 0; i < $('.time-ago').length; i++){
			    	setInterval(() => $('.time-ago').eq(i).html(timeSince($('.time-ago').eq(i).attr('data-time'))), 5000);
			    }
			    pageMe({pagerSelector:'#list-history', perPage: 11, numLinksTwoSide: 1, showFirstAndLast: false, paginationSelector: '#' + results.auction + ' .pagination'});
				
				$('#' + results.auction + ' .userRegisterNum').html(parseInt($('#' + results.auction + ' .userRegisterNum').html()) + 1);
			}
			
			if (message.destinationName == "auction/win") { 
				let results = JSON.parse(message.payloadString);
				
				// congrulation user win auction
				if (results.userId === $('.UserId').html()) {
	            	let d = results.time;
	            	let timeArr = d.split(/[^0-9]/);
					$('.cg-body').html(
						'Bạn là người thắng đấu giá sản phẩm <strong>' + results.auction_name + '</strong> tại giá <strong class="text-primary">' + Number(results.price).toLocaleString("en") + ' VNDT</strong>'
		            	+'<ul class="info">'
		            	+'	<li><span>Mã sản phẩm: </span><strong>' + results.auction_id + '</strong></li>' 
		            	+' 	<li><span>Email: </span><strong>' + results.email+ '</strong></li>'
		            	+'	<li><span>Tên tài khoản: </span><strong>' + results.username + '</strong></li>'
		            	+'	<li><span>Thời gian: </span><strong>' + timeArr[3] + ':' + timeArr[4] + ':' + timeArr[5] +'  ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0] + '</strong></li>'
		            	+'</ul>'
		            	+'<p class="text-center f-15 font-weight-bold text-primary">'
				        +'	Lưu ý: Một thông báo trúng đấu giá đã được gửi về email của bạn.'
				        +'	Vui lòng kiểm tra mail để điền thông tin nhận Sản phẩm trúng đấu giá.'
			        	+'</p>'
					);
					// $('#CongratulationModal').click();
					$('#MdlCongratulation').modal({
						backdrop: 'static',
						keyboard: false  // to prevent closing with Esc button (if you want this too)
					});
					confetti.start();
					let y = setInterval(function() {
						if	($("#MdlCongratulation").is(":hidden")) {
							clearInterval(y);
				    		confetti.stop();
				    		if (window.location.pathname === "/chi-tiet-dau-gia/" + results.auction_id) location.reload();
				    	}
					}, 1000);
				}
			}
			if (message.destinationName == "auction/registration") { 
				let results = JSON.parse(message.payloadString);
				// add new register to register history
				let newRegister = "<li class='list-group-item border-0 pl-0 pr-0 pt-1 pb-1 bg-transparent' data-userid='" + results.userid + "'>"
			                      +"    <span class='d-block d-sm-inline'>"
			                      +"        <span class='font-weight-600'>" + results.username + "</span>"
			                      +"       <span class='f-13 text-muted float-right time-ago' data-time='" + results.created + "'>" + timeSince(results.created) + "</span>"
			                      +"    </span>"
			                      +"</li>";
				$("#" + results.auction + " .list-group.list-group-flush.list-register-histoty").prepend(newRegister);
				for(let i = 0; i < $('.time-ago').length; i++){
			    	setInterval(() => $('.time-ago').eq(i).html(timeSince($('.time-ago').eq(i).attr('data-time'))),5000);
			    }
				pageMe({pagerSelector:'#list-history', perPage: 10, numLinksTwoSide: 1, showFirstAndLast: false, paginationSelector: '#' + results.auction + ' .pagination'});
			    
			    $("#" + results.auction + " .numRegister").html(results.registed);
			}
			if (message.destinationName == "auction/cancelRegistration") {
				let results = JSON.parse(message.payloadString);
			    $("#" + results.auction + " .list-group.list-group-flush").find(`[data-userId='${results.userid}']`).remove();

			    $("#" + results.auction + " .numRegister").html(results.registed);
				pageMe({pagerSelector:'#list-history', perPage: 10, numLinksTwoSide: 1, showFirstAndLast: false, paginationSelector: '#' + results.auction + ' .pagination'});
			}
			if (message.destinationName == "auction/status") { 
				let results = JSON.parse(message.payloadString);
				if (window.location.pathname === "/chi-tiet-dau-gia/" +  results.auction_id && results.userId !== $('.UserId').html()) location.reload();
			}
			
		}
	}
	
	//check cookie AC_ACCESS_KEY
	if (getCookie("AC_ACCESS_KEY") === "") {
		setCookie("AC_ACCESS_KEY", "", 0);
	}	

});

function setCookie(cname, cvalue, exdays) {
	let d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	let expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
	let name = cname + "=";
	let ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function checkCookie() {
	let token = getCookie('AC-ACCESS-KEY');
	if (token != "") {
		return true;
	}
	else {
		return false;
	}
}
function checkUserLoggedIn() {
	return $('#user-is-logged-in').get(0) ? true : false; 
}

function generateUUID() { // Public Domain/MIT
	var d = new Date().getTime();//Timestamp
	var d2 = (performance && performance.now && (performance.now() * 1000)) || 0;//Time in microseconds since page-load or 0 if unsupported
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16;//random number between 0 and 16
		if (d > 0) {//Use timestamp until depleted
			r = (d + r) % 16 | 0;
			d = Math.floor(d / 16);
		} else {//Use microseconds since page-load if supported
			r = (d2 + r) % 16 | 0;
			d2 = Math.floor(d2 / 16);
		}
		return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
	});
}

function copyToClipboard(element) {
	var $temp = $("<input>");
	$("body").append($temp);
	$temp.val($(element).text()).select();
	document.execCommand("copy");
	$temp.remove();
	$(".tooltiptext").show();
	setTimeout(() => $(".tooltiptext").hide('slow'),1000);
}

// function isSafari() {
// 	let userAgentStr = navigator.userAgent;
// 	console.log('userAgent: {}', userAgentStr);

// 	return userAgentStr.indexOf("Safari") > -1 && userAgentStr.indexOf("Chrome") <= -1;
// }

let timeSince = function(date) {
    if (typeof date !== 'object') {
      date = new Date(date);
    }

    var seconds = Math.floor((new Date().getTime() - date) / 1000);
    var intervalType;

    var interval = Math.floor(seconds / 31536000);
    if (interval >= 1) {
        intervalType = "năm";
    } else {
        interval = Math.floor(seconds / 2592000);
        if (interval >= 1) {
          intervalType = "tháng";
        } else {
          interval = Math.floor(seconds / 86400);
          if (interval >= 1) {
            intervalType = "ngày";
          } else {
            interval = Math.floor(seconds / 3600);
            if (interval >= 1) {
              intervalType = "giờ";
            } else {
              interval = Math.floor(seconds / 60);
              if (interval >= 1) {
                intervalType = "phút";
              } else {
                interval = seconds;
                intervalType = "giây";
              }
            }
          }
        }
      }

  return interval + ' ' + intervalType + ' trước';
};


