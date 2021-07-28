paginations = function(opts) {

	var defaults = {
		showFirstAndLast: false,
		perPage: 6,
		numLinksTwoSide: 2,
		curPage: 1,
		paginationSelector: '.pagination',
		scrollPage: false
	},
	settings = $.extend(defaults, opts);
	var listElement = $(settings.pagerSelector);
	var perPage = settings.perPage;
	var children = listElement.children();
	var curPage = settings.curPage;
	var delta = settings.numLinksTwoSide;
	var showFirstAndLast = settings.showFirstAndLast;
	var paginationSelector = settings.paginationSelector;
	var curPage = settings.curPage;
	var scrollPage = settings.scrollPage;

	var numItems = children.size();
	var numPages = Math.ceil(numItems / perPage);

	children.hide();
	page = curPage - 1;
	var startAt = page * perPage, endOn = startAt + perPage;
	children.css('display', 'none').slice(startAt, endOn).show();

	$('.numPages').html(numPages);
	$('.numRecords').html(numItems);
	$('.currPage').html(curPage);

	let firstPage = (showFirstAndLast) ? '<li class="page-item first-page"><a class="page-link"><i class="las la-angle-double-left fs-100"></i></a></li>' : '';
	let lastPage = (showFirstAndLast) ? '<li class="page-item last-page"><a class="page-link"><i class="las la-angle-double-right fs-100"></i></a></li>' : '';
	let prevPage = '<li class="page-item prev-page"><a class="page-link m-0 pl-0 pr-0" style="width: 35px; margin-right: 10px!important;"><i class="icn-prev-svg-paginate"></i></a></li>';
	let nextPage = '<li class="page-item next-page"><a class="page-link m-0 pl-0 pr-0" style="width: 35px;"><i class="icn-next-svg-paginate"></i></a></li>';
	let paginationRender = firstPage + prevPage + pagination(delta, numPages, curPage) + nextPage + lastPage;
	$(paginationSelector).html(paginationRender);

	let btnNextPg = $(paginationSelector + " .page-item.next-page");
	let btnPrevPg = $(paginationSelector + " .page-item.prev-page");
	let btnFirstPg = $(paginationSelector + " .page-item.first-page");
	let btnLastPg = $(paginationSelector + " .page-item.last-page");
	if (numItems === 0) {
		btnPrevPg.hide();
		btnFirstPg.hide();
		btnNextPg.hide();
		btnLastPg.hide();
	}
	if (curPage === 1) {
		btnPrevPg.addClass("disabled disable-icon");
		btnFirstPg.addClass("disabled");
	}
	if (curPage === numPages) {
		btnNextPg.addClass("disabled disable-icon");
		btnLastPg.addClass("disabled");
	}

	$(paginationSelector + ' li.page-item.numpaging .page-link').click(function() {
		var clickedPage = $(this).html().valueOf() - 1;
		setCurrPage(clickedPage + 1);
		return false;
	});
	btnPrevPg.not(".disabled").click(function() {
		previous();
		return false;
	});
	btnNextPg.not(".disabled").click(function() {
		next();
		return false;
	});
	btnFirstPg.not(".disabled").click(function() {
		setCurrPage(1);
		return false;
	});
	btnLastPg.not(".disabled").click(function() {
		setCurrPage(numPages);
		return false;
	});

	function previous() {
		let goToPage = curPage - 1;
		setCurrPage(goToPage);
	}

	function next() {
		let goToPage = curPage + 1;
		setCurrPage(goToPage);
	}

	function setCurrPage(curr) {
		curPage = curr;
		paginations($.extend(opts, { curPage: curPage }));
		if (scrollPage) {
			$('li .page-link').click(function () {
				$('html,body').animate({ scrollTop: listElement.offset().top - 150 }, 1400);
			});
		}
	}

	function pagination(delta, numPages, curPage) {
		const range = delta + 4; // use for handle visible number of links left side

		let render = "";
		let renderTwoSide = "";
		let dot = `<li class="page-item"><a class="page-link dot-paging">...</a></li>`;
		let countTruncate = 0; // use for ellipsis - truncate left side or right sidelet curPage = 1;
		// use for truncate two side
		const numberTruncateLeft = curPage - delta;
		const numberTruncateRight = curPage + delta;
		let active = "";
		for (let pos = 1; pos <= numPages; pos++) {
			active = pos === curPage ? "active" : "";

			// truncate
			if (numPages >= 2 * range - 1) {
				if (numberTruncateLeft > 3 && numberTruncateRight < numPages - 3 + 1) {
					// truncate 2 side
					if (pos >= numberTruncateLeft && pos <= numberTruncateRight) {
						renderTwoSide += renderPage(pos, active);
					}
				} else {
					// truncate left side or right side
					if (
						(curPage < range && pos <= range) ||
						(curPage > numPages - range && pos >= numPages - range + 1) ||
						pos === numPages ||
						pos === 1
					) {
						render += renderPage(pos, active);
					} else {
						countTruncate++;
						if (countTruncate === 1) render += dot;
					}
				}
			} else {
				// not truncate
				render += renderPage(pos, active);
			}
		}
		if (renderTwoSide) {
			renderTwoSide = renderPage(1) + dot + renderTwoSide + dot + renderPage(numPages);
			return renderTwoSide;
		} else {
			return render;
		}
	}
	function renderPage(index, active = "") {
		return `<li class="page-item numpaging ${active}">
                <a class="page-link">${index}</a>
            </li>`;
	}
}