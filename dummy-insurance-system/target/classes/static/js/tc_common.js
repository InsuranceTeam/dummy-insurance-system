$(function(){

  //解約日（指定可能期間は契約開始日～契約満期日）
  $("#datepicker_cancel").attr("min", $("#contract_start_date").html().split('/').join('-') ); //契約開始日より前日付は指定不可
  $("#datepicker_cancel").attr("max", $("#contract_maturity_date").html().split('/').join('-') ); //契約満期日以降は指定不可

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

  //モーダルウィンドウの表示・表示制御（初期表示）
  switch($("#overlay_display").val()){
    case '1':
      $("body").addClass("no_scroll"); // 背景固定させるクラス付与
      $('#overlay, .modal-window').show();
      break;
    case '2':
      $("#update_end").dialog({
        modal:true,
        title:"完了メッセージ",
        buttons: {"OK": function() {$(this).dialog("close");}}
      });
      break;
  }
  if($("#overlay_display").val() == '1'){
    $("body").addClass("no_scroll"); // 背景固定させるクラス付与
    $('#overlay, .modal-window').show();
  }

  //モーダルウィンドウの表示・表示制御（解約・取消ボタン押下時）
  $('.js-open').click(function () {
    $("body").addClass("no_scroll"); // 背景固定させるクラス付与
    $("#cancel").prop("checked",true); //「解約・取消　区分」を「解約」に設定
    $(".Date-box-show").show();   //解約日の入力項目を表示する
    $(".text-danger").html('<div class="text-danger"></div>');  //エラーメッセージをクリア
    $("#datepicker_cancel").attr("value",$("#contract_start_date").html().split('/').join('-') );// 契約開始日を初期設定
    $('#overlay, .modal-window').fadeIn();
  });
  $('.js-close').click(function () {
    $("body").removeClass("no_scroll"); // 背景固定させるクラス削除
    $('#overlay, .modal-window').fadeOut();
  });
  //モーダルウィンドウの表示・表示制御（戻るボタン押下時）
  if($("#screen_info").val() == 'input' && ($("#rescission").prop("checked"))){
    $(".Date-box-show").hide();       //解約日の入力項目を非表示にする

  }
//  if($("#screen_info").val() == 'input' && ($("#cancel").prop("checked"))){
//    $("#datepicker_cancel").attr("value",$("#datepicker_cancel").html().split('/').join('-') );// /になっているので-に置換
//  }

  //解約日の表示・非表示　切替（「解約・取消　区分」が変更されたとき）
  $(".Label-cancel").click(function(){
	  $(".text-danger").html('<div class="text-danger"></div>'); //エラーメッセージをクリア
	  $(".Date-box-show").show();       //解約日の入力項目を表示する
	  $("#rescission").prop("checked",false);
	  $("#cancel").prop("checked",true);
	  $("#datepicker_cancel").attr("value",$("#contract_start_date").html().split('/').join('-') );// 契約開始日を初期設定

  });
  $(".Label-rescission").click(function(){
	  $(".text-danger").html('<div class="text-danger"></div>'); //エラーメッセージをクリア
	  $(".Date-box-show").hide();       //解約日の入力項目を非表示にする
	  $("#cancel").prop("checked",false);
	  $("#rescission").prop("checked",true);
  });
});

