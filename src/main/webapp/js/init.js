$(document).ready(function (){
    $('.show-password').on('click', function (){
        let input = $(this).closest('.form-group').find('input')
        if (input.attr('type') == 'text') {
            input.attr('type', 'password');
        } else {
            input.attr('type', 'text');
        }
    })

    //modal login ==============
    $('.open-modal-tab-register').on('click', function () {
        $('#modalLogin').find('.nav-register').trigger('click');
        $('#modalLogin').modal('show');
    })
    $('.open-modal-tab-login').on('click', function () {
        $('#modalLogin').find('.nav-login').trigger('click');
        $('#modalLogin').modal('show');
		$('#modalRegisterSuccess').modal('hide');
    })

    //sidebar
    //check show more if > 1 item
    if ($('#sidebar').find('.nav-item').length > 0) {
        $.each($('#sidebar').find('.nav-item'), function (key, value) {
            if ($(value).find('.child-nav-item').length > 0) {
                $(value).find('.show-more-icn').show();
            }
        })
    }

    $('#sidebar').find('.active').find('.child-nav').collapse('show')
    $('#sidebar').find('.active').attr('data-show', 1)
    $('#sidebar').find('.active').find('.show-more-icn').addClass('rotate')

    $('.parent-nav-item').on('click', function (){
        $('.nav-item').removeClass('active')
        $(this).closest('.nav-item').addClass('active')
        $('#sidebar').find('.child-nav').collapse('hide')

        //reset rotage all (-90deg)
        $('#sidebar').find('.show-more-icn').removeClass('rotate').addClass('rotate-90')
        $('#sidebar').find('.nav-item').not('.active').attr('data-show', 0)

        //rotage
        let check = $(this).closest('.nav-item').attr('data-show')

        if (check == 1) {
            $(this).closest('.nav-item').attr('data-show', 0)
            $(this).closest('.nav-item').find('.show-more-icn').removeClass('rotate').addClass('rotate-90')
        } else {
            $(this).closest('.nav-item').attr('data-show', 1)
            $(this).closest('.nav-item').find('.show-more-icn').addClass('rotate').removeClass('rotate-90')
        }
    })

    // withdrawal screen
    $(".list-bank-remember .item").on("click", function () {
        $(".account-number").val($(this).attr("data-bank-number"));
        $(".bank-id").val($(this).attr("data-bank-id"));
        $(".bank-selected-show").html("");

        let item = $(this).children();
        item.each((item, value) => {
            if (item === 2) {
                $(".bank-selected-show").html(value.textContent.replace(/\s\s+/g, " "));
                $(".bank-selected-show").addClass("color-selected");
            }
        })
    })

    $(".dg-wallet .bank-selected").on("click", function () {
        let isShow = $(".list-bank").attr("data-show") //default display none
        if (isShow === '0') {
            $(".list-bank").css( {
                'height': '200px',
                'transition': "all 250ms linear"
            })
            $(".list-bank").attr('data-show', 1)
        } else {
            $(".list-bank").css( {
                'height': '0',
            })
            $(".list-bank").attr('data-show', 0)
        }

    })

    $(".list-wallet-remember .item").on("click", function () {
        $("#address-wallet").val($(this).attr("data-wallet-id"));
    })

    let checkAcWallet = false;
    $(".remember-wallet-address label").on("change", function () {
        checkAcWallet = !checkAcWallet;
        if (checkAcWallet) {
            $(".reminiscent-name").css("display", "block");
        } else {
            $(".reminiscent-name").css("display", "none");
        }
    })

    let checkBank = false;
    $(".remember-bank-address label").on("change", function () {
        checkBank = !checkBank;
        if (checkBank) {
            $(".reminiscent-name-bank").css("display", "block");
        } else {
            $(".reminiscent-name-bank").css("display", "none");
        }
    })

    $("#withdrawal-agency").on("click", function () {
        $(".btn-withdrawal").css("display", "none");
    })

    $("#withdrawal-ac-wallet").on("click", function () {
        $(".btn-withdrawal").css("display", "block");
    })

    $("#withdrawal-bank").on("click", function () {
        $(".btn-withdrawal").css("display", "block");
    })
    //end widthdrawal

    // wallet screen
    $(".dg-wallet").find("#giftcode").on("click", function () {
        $(".btn-payment").css("display", "block");
    })

    $(".dg-wallet").find("#bank").on("click", function () {
        $(".btn-payment").css("display", "none");
    })

    $(".dg-wallet").find("#ac-wallet").on("click", function () {
        $(".btn-payment").css("display", "none");
    })

    // $(".image-copy-input").on("click", function (e) {
    //     $("#" + $(this).data("id")).select();
    //     document.execCommand("copy");
    // });
	
    $(".list-bank-item").on("click", function () {
		$(".list-bank-item").removeClass('list-bank-item-selected');
        $(".bank-selected-show").html($(this).text());
        $(".bank-id").val($(this).attr("data-bank-id"));
        $(".bank-selected-show").addClass("color-selected");
		$(this).addClass("list-bank-item-selected");
		$(".list-bank").css( {
			'height': '0',
		})
		$(".list-bank").attr('data-show', 0)
    })

    // end wallet screen

    // show modal thêm địa chỉ
    $('.dg-list-address').find('.open-modal-add-address').on('click', function (){
        $('#modalAddAddress').modal('show')
    })

    // show modal sửa địa chỉ
    // $('.dg-list-address').find('.open-modal-edit-address').on('click', function (){
    //     $('#modalEditAddress').modal('show')
    // })

    let showMoney = false;
    $(".switch-balance").on("click", function () {
        if (!showMoney) {
            $(".dg-price").css("display", "block");
            $(".dg-price-hidden").css("display", "none")
        } else {
            $(".dg-price").css("display", "none");
            $(".dg-price-hidden").css("display", "block")
        }

        showMoney =! showMoney;
    });

	if (checkUserLoggedIn()) {
		// add liked auction 
		fetch('/user/liked_auction')
			.then(res => res.json())
			.then(results => {
				if (results) {
					for(let i = 0; i < results.length; i++) {
						if (results[i].delete == false) {
							$(`[data-auction-id='${results[i].auction.id}']`).find(".favorite").addClass("active-favorite");
							$(`[data-auction-id='${results[i].auction.id}']`).find(".favorite").children().attr("src", "/images/ac-img/liked.svg");
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
	$('.favorite').click(function() {
		if (!checkUserLoggedIn()) $('#modalLogin').modal('show');
		else if (toggleLike) {
			toggleLike = false;
			let auctionId = $(this).attr("data-id");
			if ($(this).hasClass('active-favorite')) {
				$(`[data-auction-id='${auctionId}']`).find('.favorite').removeClass('active-favorite');
				$(`[data-auction-id='${auctionId}']`).find('.favorite').children().attr("src", "/images/ac-img/like.svg")
				fetch('/user/liked_auction/auction/' + auctionId,{
					method: 'DELETE',
				})
					.then(res => res.text())
					.then(result => {
						if (result === "OK") {
							toggleLike = true;
						}
					})
					.catch(error => {
						console.log("Error: " + error);
					});
			}
			else {
				$(`[data-auction-id='${auctionId}']`).find('.favorite').addClass('active-favorite');
				$(`[data-auction-id='${auctionId}']`).find('.favorite').children().attr("src", "/images/ac-img/liked.svg");
				fetch('/user/liked_auction/add/' + auctionId)
					.then(res => res.text())
					.then(result => {
						if (result === "OK") {
							toggleLike = true;
						}
					})
			}
		}
	})
	
	

    //show and hide sidebar
    $('.dg-show-sidebar').on('click', function (){ //show
        $('.dg-sidebar').removeClass('sidebar-collapse')

        $('.dg-sidebar').removeClass('hide-sidebar').addClass('active-sidebar')
        $(this).hide()
        $('.dg-hide-sidebar').show()
    })

    $('.dg-hide-sidebar').on('click', function (){ //hide
        $('.dg-sidebar').removeClass('active-sidebar').addClass('hide-sidebar')
        $(this).hide()
        $('.dg-show-sidebar').show()
    })

    //show box search
    $('.box-menu-mbl .mbl-box-search').on('click', function (){
        $('.dg-header-top-mobile').addClass('active-search')
        //$('.dg-header-top-mobile').find('.dg-search').show()

    })

    //handle noty header
    $('.btn-show-noty').click(function () {
        $('.noty-header').addClass('active');
    });

    $(document).on('click touch', function (event) { //click out box-search
        if (!$(event.target).parents().addBack().is('.mbl-box-search')
            && !$(event.target).parents().addBack().is('.dg-search input')) {
            $('.dg-header-top-mobile').removeClass('active-search')
        }

        if (!$(event.target).parents().addBack().is('.dg-sidebar ') //click out sidebar
            && !$(event.target).parents().addBack().is('.dg-show-sidebar')) {

            $('.dg-sidebar').removeClass('active-sidebar').addClass('hide-sidebar')
            $('.dg-hide-sidebar').hide()
            $('.dg-show-sidebar').show()
        }
    });

    let showBoxExit = false;
    $(".box-exit-btn").on("click", function () {
        showBoxExit = !showBoxExit;
        console.log("shit")
        if (showBoxExit) {
            $(".box-exit").css("display", "block");
        } else {
            $(".box-exit").css("display", "none");
        }
    });

    $(".preview-image .owl-item").on("click", function () {
        $(this).parent().children().find(".custom-active").removeClass("custom-active");
        $(this).children().addClass("custom-active");
    })

    // $(".transfer-history-detail").on("click", function () {
    //     if ($( window ).width() <= 768) {
    //         $("#transfer-id").text("ID: " + $(this).children()[0].textContent);
    //         $("#amount").text($(this).children()[1].textContent);
    //         $("#title").text($(this).children()[2].textContent);
    //         $("#message").text($(this).children()[4].textContent);
    //         $("#status").text($(this).children()[5].textContent);
    //         $("#transfer-at").text($(this).children()[3].textContent);

    //         $('#box-transfer-history').modal('show');
    //     }
    // });

    //tin tuc
    $(".big-img").hover(function(){
        $(this).removeClass('hide1')
    }, function(){
        $(this).addClass('hide1')
    });

    $(".dg-cart .choose").on("click", function () {
        $(".done").css("display", "block");
        $(this).css("display", "none");
        $(".custom-checkbox").css("display", "inline-block");
    });

    $(".dg-cart .done").on("click", function () {
        $(".choose").css("display", "block");
        $(this).css("display", "none");
        $(".custom-checkbox").css("display", "none");
    });


    //layout sidebar
    $('.dg-collapse').on('click', function (){
        //dãn rộng content
        $('#sidebar').closest('.dg-sidebar').addClass('sidebar-collapse')
        //dãn rộng content
        $('.dg-content').addClass('dg-content-collapse')
        //footer
        $('.dg-footer').addClass('collapse-footer')
    })

    $('.dg-show-sidebar-bottom').on('click', function (){
        //dãn rộng content
        $('#sidebar').closest('.dg-sidebar').removeClass('sidebar-collapse')
        //dãn rộng content
        $('.dg-content').removeClass('dg-content-collapse')
        //footer
        $('.dg-footer').removeClass('collapse-footer')
    })

   /* $( window ).resize(function() {
        if ($('body').width() <= 768 ) {
            if ($('.dg-sidebar').hasClass('sidebar-collapse')) {
                $('.dg-content').addClass('dg-collapse-more-content-1');
            }
        }

    });*/


    $(".dg-home #auction-slider").owlCarousel({
        items: 1,
        nav: true,
		loop: true,
		//autoplay: true,
		//autoplayTimeout: 10000,
		autoplayHoverPause: true,
    });

    $(".dg-home .product-image").on("mouseover", function () {
        $(".preview-image").css("display", "block");
        $(".dg-home .preview-image").on("mouseover", function () {
            $(".preview-image").css("display", "block");
        });
    });

    $(".dg-home .product-image").on("mouseout", function () {
        $(".preview-image").css("display", "none");

        $(".dg-home .preview-image").on("mouseout", function () {
            $(".preview-image").css("display", "none");
        });
    });

    $(".dg-home .preview-image").owlCarousel({
        items: 3,
        nav: true,
		center: true,
        navText: [
            "<img src='/images/ac-img/pre-preview.svg' />",
            "<img src='/images/ac-img/next-preview.svg' />"
        ]
    });

    $(".dg-home .preview-image .owl-item").on("click", function () {
        let imgSrc = $(this).children().children().attr("src");
        $(this).parent().parent().parent().siblings(".product-image").attr("src", imgSrc);
        $(this).siblings(".owl-item").find(".custom-active").removeClass("custom-active");
        $(this).children().addClass("custom-active");
    });

	// Format currency
	for (let i = 0; i < $('.cur-format').length; i++) {
		$('.cur-format').eq(i).html(Math.floor(Number($('.cur-format').eq(i).html())).toLocaleString('en'));
		$('.cur-format').eq(i).val(Math.floor(Number($('.cur-format').eq(i).val())).toLocaleString('en'));
	}
	
	let timeNow = $("#timeNow").html()
	let CountDownTime = function(timeElement) {
		let countDownDate = new Date(timeElement.getAttribute("data-action-time"));
		
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
			if (days > 0) {
				timeElement.querySelectorAll('.time-num')[0].innerHTML = days + "n";
			}
			else {
				timeElement.querySelectorAll('.time-num')[0].innerHTML = "";
			}
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
	let timingElement = $(".timing");
	for (let i = 0; i < timingElement.length; i++) {
		CountDownTime(timingElement.get(i));
	}
	let timeBoxElement = $(".box-date");
	for (let i = 0; i < timeBoxElement.length; i++) {
		CountDownTime(timeBoxElement.get(i));
	}
	
	//count time rest auction
	var intervalTime;
	let CountDownTimeRest = function(timeElement) {
		let countDownDate = new Date(timeElement.getAttribute("data-action-time")+"00z");
		if (Number.isNaN(countDownDate.getTime())) { // Safari browser cannot parse above date
			countDownDate = new Date((timeElement.getAttribute("data-action-time") + ":00"));
		};
		countDownDate = new Date(countDownDate.getTime() + (countDownDate.getTimezoneOffset()*60*1000));
		
		fetch("/system-time")
			.then(res => res.text())
			.then(timeNow => {
				let distance = countDownDate - timeNow + 60 * 60 * 1000;
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
			});
	}
	
	if ($(".time-box-rest").size() !== 0) {
		let timingElement = $(".time-box-rest");
		for (let i = 0; i < timingElement.length; i++) {
			CountDownTimeRest(timingElement.get(i));
		}
	}
	
	// Format localedate time
	for (let i = 0; i < $('.time-format').length; i++) {
		let d = $('.time-format').eq(i).html();
		let timeArr = d.split(/[^0-9]/);

		let dateStr = timeArr[3] + ':' + timeArr[4] + ' ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0];
		$('.time-format').eq(i).html(dateStr);
	}
	
	// Format localedate time
	for (let i = 0; i < $('.time-format-history').length; i++) {
		let d = $('.time-format-history').eq(i).html();
		let timeArr = d.split(/[^0-9]/);

		let dateStr = timeArr[3] + ':' + timeArr[4] + ':' + timeArr[5] + ' ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0];
		$('.time-format-history').eq(i).html(dateStr);
	}
	//log out
	$('.logOut').click(function() {
		setCookie("AC-ACCESS-KEY", '', 0);
		window.location.reload();
	});
	
	//get num of register user
	if ($(".numRegister").size() > 0) {
		$(".numRegister").each((i, e) => {
			let id = e.getAttribute("data-register-auction-id");
			fetch("/auction_registrations/auction/" + id)
				.then(res => res.status === 200 ? res.json() : [])
				.then(results => e.innerHTML = results.length < 10 ? '0' + results.length : results.length);
		});
	}
	
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
		}
	
		// called when the client loses its connection
		function onConnectionLost(responseObject) {
			if (responseObject.errorCode !== 0) {
				console.log("onConnectionLost:" + responseObject.errorMessage);
			}
			setTimeout(MQTTconnect, 1000);
		}
		function onFailure(message) {
			setTimeout(MQTTconnect, 1000);
		}
		
		// called when a message arrives
		function onMessageArrived(message) {
			if (message.destinationName == "auction/bid") { 
				let results = JSON.parse(message.payloadString);
				
				if ($(`[data-auction-id='${results.auction}']`).size() > 0) {
					$(`[data-home-likeId=${results.auction}]`).find(".curPriceLike").html(results.price);
					
					$(`[data-auction-id='${results.auction}']`).find(".curPrice").html(results.price);
					$(`[data-auction-id='${results.auction}']`).find(".currentPrice").prop('Counter', parseInt($(`[data-auction-id='${results.auction}']`).find(".currentPrice").eq(0).html().replace(/,/g, ''))).animate({
						Counter: results.price
					}, {
						duration: 2000,
						easing: 'swing',
						step: function(now) {
							$(this).text(Math.ceil(now).toLocaleString('en'));
						}
					});
				
					$(`[data-auction-id='${results.auction}']`).find(".BidPrice").prop('Counter', parseInt($(`[data-auction-id='${results.auction}']`).find(".BidPrice").eq(0).html().replace(/,/g, ''))).animate({
						Counter: parseInt(results.price) + parseInt($('.BidStep').val()) * parseInt($('.StepPrice').html().replace(/,/g, ''))
					}, {
						duration: 2000,
						easing: 'swing',
						step: function(now) {
							$(this).text(Math.ceil(now).toLocaleString('en'));
						}
					});
					
					// count down time when user bid auction
					let countDownDate = new Date($(`[data-auction-id='${results.auction}']`).find(".time-auction").get(0).getAttribute("data-action-time"));
					fetch("/system-time")
						.then(res => res.text())
						.then(timeNow => {
							let distance = countDownDate - timeNow;
							
							clearInterval(intervalTime);
							if (distance > 1000 * 60 * 60) {
								if ($(`[data-auction-id='${results.auction}']`).find(".notice-time-rest").hasClass('d-none')) {
									$(`[data-auction-id='${results.auction}']`).find(".notice-time-rest").removeClass('d-none');
									$(`[data-auction-id='${results.auction}']`).find(".time-box-rest").eq(0).attr("data-action-time", results.created);
									CountDownTimeRest($(`[data-auction-id='${results.auction}']`).find(".time-box-rest").get(0));
								}
								else { 
									$(`[data-auction-id='${results.auction}']`).find(".time-box-rest").eq(0).attr("data-action-time", results.created);
									CountDownTimeRest($(`[data-auction-id='${results.auction}']`).find(".time-box-rest").get(0));
								}
							}
							else {
								$(`[data-auction-id='${results.auction}']`).find(".notice-time-rest").addClass('d-none');
							}
						});
				}
				
			 	// alert to user bid highest			
				$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").hide();
				if (results.user === $('.UserId').html()) {
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").addClass('alert alert-success');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").removeClass('alert-danger');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").html('Bạn là người thắng đấu giá hiện tại.');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").fadeTo(1000, 1, function () {
		                $(this).slideDown(500);
		            });
				}
				else {
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").addClass('alert alert-danger');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").removeClass('alert-success');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").html('Bạn chưa phải là người đấu giá cao nhất. Bạn có thể tăng giá đấu bất kì lúc nào.');
					$(`[data-auction-id='${results.auction}']`).find("#ContextualStatus").fadeTo(1000, 1, function () {
		                $(this).slideDown(500);
		            });
				}
				
				// add new bid to bid history
				let timeArr =  results.created.split(/[^0-9]/);
				if (results.user === $('.UserId').html()) {
					let newBidTop = "<div class='dg-detail-auction-me-payment'>"
                                    +"    <div class='dg-detail-auction-me-payment-price'>"
                                    +"        <span>" + Number(results.price).toLocaleString('en') + "</span> VNDT"
                                    +"    </div>"
                                    +"    <div class='dg-detail-person-avatar'>"
                                    +"          <img src='" + (results.avatar ? results.avatar : '/images/ac-img/no-avatar.jpg') + "' height='32' width='32' alt=''>"
                                    +"    </div>"
                                	+"</div>";
					$(`[data-auction-id='${results.auction}']`).find(".list-bid-history-top").append(newBidTop);
				}
				else {
					let newBidTop = "<div class='dg-detail-auction-other-payment'>"
	                                +"    <div class='dg-detail-person-avatar'>"
	                                +"          <img src='" + (results.avatar ? results.avatar : '/images/ac-img/no-avatar.jpg') + "' height='32' width='32' alt=''>"
	                                +"    </div>"
	                                +"    <div class='dg-detail-auction-other-payment-price'>"
	                                +"        <span>" + Number(results.price).toLocaleString('en') + "</span> VNDT"
	                                +"    </div>"
	                                +"</div>";
					$(`[data-auction-id='${results.auction}']`).find(".list-bid-history-top").append(newBidTop);
				}
				
				let newBid = "<tr>"
	                         +"   <td class='dg-detail-col-person'>"
	                         +"       <div class='dg-detail-person-avatar'>"
	                         +"          <img src='" + (results.avatar ? results.avatar : '/images/ac-img/no-avatar.jpg') + "' height='32' width='32' alt=''>"
	                         +"       </div>"
	                         +"       <div>" + results.username + "</div>"
	                         +"   </td>"
	                         +"   <td>" + Number(results.price).toLocaleString('en') + "</td>"
	                         +"   <td>" + timeArr[3] + ':' + timeArr[4] + ':' + timeArr[5] + '  ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0] + "</td>"
	                         +"</tr>";
				$(`[data-auction-id='${results.auction}']`).find(".list-bid-histoty").prepend(newBid);
				
				$(`[data-auction-id='${results.auction}']`).find(".userBidNum").html(parseInt($(`[data-auction-id='${results.auction}']`).find(".userBidNum").html()) + 1);
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
				let timeArr =  results.created.split(/[^0-9]/);
				let newRegister = "<tr data-userid='" + results.userid + "'>"
	                              +"  <td class='dg-detail-col-person'>"
	                              +"      <div class='dg-detail-person-avatar'>"
	                              +"          <img src='" + (results.avatar ? results.avatar : '/images/ac-img/no-avatar.jpg') + "' height='32' width='32' alt=''>"
	                              +"      </div>"
	                              +"      <div>" + results.username + "</div>"
	                              +"  </td>"
	                              +"  <td class='time-format'>" + timeArr[3] + ':' + timeArr[4] + ':' + timeArr[5] +'  ' + timeArr[2] + '-' + timeArr[1] + '-' + timeArr[0] + "</td>"
	                              +"</tr>";
				$(`[data-auction-id='${results.auction}']`).find(".list-register-histoty").prepend(newRegister);
			    $(`[data-auction-id='${results.auction}']`).find(".numRegister").html(results.registed < 10 ? '0' + results.registed : results.registed);
			    $(`[data-auction-id='${results.auction}']`).find(".numRegisters").html(results.registed < 10 ? '0' + results.registed : results.registed);
			}
			if (message.destinationName == "auction/cancelRegistration") {
				let results = JSON.parse(message.payloadString);
			    $(`[data-auction-id='${results.auction}']`).find(".list-register-histoty").find(`[data-userId='${results.userid}']`).remove();

			    $(`[data-auction-id='${results.auction}']`).find(".numRegister").html(results.registed < 10 ? '0' + results.registed : results.registed);
			    $(`[data-auction-id='${results.auction}']`).find(".numRegisters").html(results.registed < 10 ? '0' + results.registed : results.registed);
			}
			if (message.destinationName == "auction/status") { 
				let results = JSON.parse(message.payloadString);
				if (window.location.pathname === "/chi-tiet-dau-gia/" +  results.auction_id && results.userId !== $('.UserId').html()) location.reload();
				if (window.location.pathname === "/dau-gia-da-tham-gia" && $(`[data-auction-id='${results.auction_id}']`).size() > 0) {
					$(`[data-auction-id='${results.auction}']`).find(".timing").html("Kết thúc");
					let detailActice = $(`[data-auction-id='${results.auction}']`).find(".dg-detail-active");
					detailActice.hide();
					let detailEnd = $(`[data-auction-id='${results.auction}']`).find(".dg-detail-ended");
					detailActice.removeClass("d-none");
					detailEnd.find(".dg-detail-product-detail-title").html(results.auction_name);
					if (user-id) {
						detailEnd.find(".avatar-person-winner img").attr("src", (results.avatar ? results.avatar : '/images/ac-img/no-avatar.jpg'));
						detailEnd.find(".username").html(results.username);
						detailEnd.find(".user-id").html("@" + results.userId);
						detailEnd.find(".price").html(results.price + "VNDT");
					}
					else {
						detailEnd.find(".dg-detail-winner").html("Đấu giá đã kết thúc");
						detailEnd.find(".dg-detail-winner").css("font-size", "25px");	
						detailEnd.find(".dg-detail-winner").css("font-weight", "500");	
						detailEnd.find(".dg-detail-winner").addClass("text-center");
					}
				}
			}
			
		}
	}

})

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
function checkUserLoggedIn() {
	let token = getCookie("AC-ACCESS-KEY");
	if (token !== "") {
		return true;
	}
	else {
		return false;
	}
}
// copy to Clipboard
function copyToClipboard(element) {
	$('.copy-nofi').removeClass('alert alert-success');
	var $temp = $("<input>");
	$("body").append($temp);
	$temp.val($(element).val()).select();
	document.execCommand("copy");
	$temp.remove();
	$('.copy-nofi').addClass('alert alert-success');
	$('.copy-nofi').html('Đã sao chép');
	$(".copy-nofi").fadeIn('slow');
    setTimeout(() => $(".copy-nofi").fadeOut('slow'),1000);
}