$('.select').on('click','.placeholder',function(){
  var parent = $(this).closest('.select');
  if ( ! parent.hasClass('is-open')){
    parent.addClass('is-open');
    $('.select.is-open').not(parent).removeClass('is-open');
  }else{
    parent.removeClass('is-open');
  }
}).on('click','ul>li',function(){
  var parent = $(this).closest('.select');
  parent.removeClass('is-open').find('.placeholder').text( $(this).text() );
  parent.find('input[type=hidden]').attr('value', $(this).attr('data-value') );

  if(parent.find('input[type=hidden]').attr('value') == "K2"){
		$("#refundDateInput").hide();
  }
  if(parent.find('input[type=hidden]').attr('value') == "K0"){
		$("#refundDateInput").show();
  }

});

$('.year,.month,.day').on("keypress", function(event){return leaveOnlyNumber(event);});

function leaveOnlyNumber(e){
  // 数字以外の不要な文字を削除
  var st = String.fromCharCode(e.which);
  if ("0123456789".indexOf(st,0) < 0) { return false; }
  return true;  
}

  $(function(){
    $('body input[type="text"]').each(function(i, elem) {
      elem.autocomplete = "off";
    });
  });
  
  
  //data-hrefの属性を持つtrを選択しclassにclickableを付加
  $('tr').addClass('clickable').click(function(e) {
   
		var a = $(e.target).closest('tr').children("td").last().children('form').submit();
  });

//global nav
 var btn = $(".mod_dropnavi ul li.parent");
 var submenu = $(".mod_dropnavi_child");
 var submenulink = $(".mod_dropnavi_child ul li a");
    //click
    $(btn).bind("click", "focus", function(event){
     var shownav = $(this).find(".mod_dropnavi_child");
    if($(shownav).css("display")=="none"){
        $(shownav).slideDown("fast");
    }else{
        $(shownav).slideUp("fast");
   }
 });

  //hover
    $(btn).hover(function () {
     },
      function () {
        $(submenu).slideUp("fast");
  });


var elements = $('.modal-overlay, #modal1');

$('#refundMenu').click(function(){
    elements.addClass('active');
	$("#errorMsg").text(" ");
	$("#year").val("");
	$("#month").val("");
	$("#day").val("");
});

$('.close-modal').click(function(){
    elements.removeClass('active');
	$("#modal2").removeClass('active');
});

$('#refundButton').click(function(){
	

	$("#errorMsg").text("");
	console.log($("#contract_id").text())
	
	if ($("#contract_end_reason_disp").text() == ""){
			var reason = $("#reason").val();
	
		if (reason == "K2"){
			$("#modal1").removeClass('active');
			$("#modal2").addClass('active');
			$("#modalTitle").text("取消 確認画面");
			$("#refund_date_column").hide();
			$("#refund_money_column").hide();
			$("#contract_end_reason").val("K2");
		} else {

			$("#year").val(('0000'+$("#year").val()).slice( -4 ));
			$("#month").val(('00'+$("#month").val()).slice( -2 ));
			$("#day").val(('00'+$("#day").val()).slice( -2 ));
			
			    $.ajax({
	      url: "/tl/s/refund",  // リクエストを送信するURLを指定（action属性のurlを抽出）
	      type: "POST",  // HTTPメソッドを指定（デフォルトはGET）
	      data: {
	        insured_person_id: $("#insured_person_id").val(),
			insured_person_birth_date:$("#insured_person_birth_date").val(),
			insured_person_sex:$("#insured_person_sex").val(),
			entry_age:$("#entry_age").val(),
			contract_id:$("#contract_id").text(),
			contract_history_id:$("#contract_history_id").text(),
			contract_start_date:$("#contract_start_date").val(),
			contract_maturity_date:$("#contract_maturity_date").val(),
			contract_end_date:$("#contract_end_date").val(),
			premium_payment_term:$("#premium_payment_term").val(),
			payment_method:$("#payment_method").val(),
			insurance_money:$("#insurance_money").val(),
			premium:$("#premium").val(),
			cancel_date:$("#year").val() + $("#month").val() + $("#day").val() 
		
		      }
		    })
		    .done(function(data) {
			
				if (data == -1){
					
					$("#errorMsg").text("日付が不正です。");
				} else if(data == -2) {
					$("#errorMsg").text("入力日付で無効な契約です。");
				} else {
					$("#modal1").removeClass('active');
					$("#modal2").addClass('active');
					$("#modalTitle").text("解約 確認画面");
					$("#refund_date_column").show();
					$("#refund_money_column").show();
					$("#refund_date_disp").text($("#year").val() + "年" + $("#month").val() + "月" +  $("#day").val() + "日");
					$("#contract_end_date").val($("#year").val() + $("#month").val() +  $("#day").val() );
					$("#contract_end_reason").val("K0");
					$("#refund_money").val(data);
					$("#refund_money_disp").text(data + "円");
				}
		    })
		} 
	
	} else {
		$("#errorMsg").text("既に解約/取消された契約です。");
	}

});

  $('.dropdown > .caption').on('click', function() {
    $(this).parent().toggleClass('open');
  });
 
  $(document).on('keyup', function(evt) {
    if ( (evt.keyCode || evt.which) === 27 ) {
      $('.dropdown').removeClass('open');
    }
  });
  
  $(document).on('click', function(evt) {
    if ( $(evt.target).closest(".dropdown > .caption").length === 0 ) {
      $('.dropdown').removeClass('open');
    }
  });
