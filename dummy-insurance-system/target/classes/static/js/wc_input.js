var BLANK = "";

$(function() {
  $('.datepicker_birth').datepicker({
    defaultDate: new Date(1990 ,1-1,1), // 1990/1/1を初期設定
    changeYear: true,    // 年を表示
    changeMonth: true,   // 月を選択
    dateFormat: 'yy/mm/dd',      // yyyy/mm/dd
    yearRange: "-120:+0"
  });

  $('.datepicker_start').datepicker({
    defaultDate: new Date(), // 処理日を初期設定
    changeYear: true,    // 年を表示
    changeMonth: true,   // 月を選択
    dateFormat: 'yy/mm/dd'      // yyyy/mm/dd
  });



  $('input').change(function(){
      inputCheck();
  });
  
  $("#komokuCheck").click(function(){
      komokuCheck();
  });
  
  $('input').change(function(){
      $("#errorMsg").html(BLANK);
      $("#errorMsgArea").hide();
      $("#confirmation_btn").hide();
  });
  
});


window.onload = function() {   
    inputCheck();
  }


function inputCheck(){
    // 必須項目入力チェック
    
    var errorFlag = false;
    
    // 背景色クリア
    clearBackGroundColor();
    
    //氏名カナ姓
    if ($("#nameKanaSei").val().trim() == BLANK){
        errorFlag = true;
        $("#nameKanaSei").css("background", "#ffb9b9");
    }
    //氏名カナ名
    if ($("#nameKanaMei").val().trim() == BLANK){
        errorFlag = true;
        $("#nameKanaMei").css("background", "#ffb9b9");
    }
    //生年月日
    if ($("#birthDay").val().trim() == BLANK){
        errorFlag = true;
        $("#birthDay").css("background", "#ffb9b9");
    }
    //加入日
    if ($("#kanyuBi").val().trim() == BLANK){
        errorFlag = true;
        $("#kanyuBi").css("background", "#ffb9b9");
    }
    
    if (errorFlag) {
        // エラーがあったら文言表示＆確認ボタン非表示
        $("#minyuryokuArea").show();
        //$("#confirmBtn").prop("disabled", true);
        $("#komokuCheck").hide();
    } else {
        // なければエラー文言削除＆確認ボタン表示
        $("#minyuryokuArea").hide();
        //$("#confirmBtn").prop("disabled", false);
        $("#komokuCheck").show();
    }
    
   
}

function clearBackGroundColor(){
    $("#nameKanaSei").css("background", "#ffffff");
    $("#nameKanaMei").css("background", "#ffffff");
    $("#birthDay").css("background", "#ffffff");
    $("#kanyuBi").css("background", "#ffffff");
}

function komokuCheck() {
    
    //現在日時を取得
    var now = new Date();
    var nowDate = new Date(now.getFullYear(), now.getMonth()+1, now.getDate(), '00', '00', '00');

    // エラーメッセージ
    var errorMsg = "";
    
    // 生年月日のチェック
    var inputBirthDay = $("#birthDay").val();
    
    // 氏名カナ入力チェック
    var nameKanaSei = $("#nameKanaSei").val();
    var nameKanaMei = $("#nameKanaMei").val();
    var kanaCheckSei = isZenKatakana(nameKanaSei);
    var kanaCheckMei = isZenKatakana(nameKanaMei);
    
    if (!kanaCheckSei || !kanaCheckMei){
        errorMsg = "被共済者氏名カナにカナ以外が入力されています";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }

    // 入力が日付かどうか
    if(!inputBirthDay.match(/^\d{4}\/\d{2}\/\d{2}$/)){
        errorMsg = "生年月日はyyyy/mm/ddの形式で入力してください";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }

    // 存在する日付かのチェック
    var hidukeCheck = ckDate(inputBirthDay);
    if (!hidukeCheck) {
        errorMsg = "生年月日に不正な日付が入力されています";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }

    // 生年月日が未来日付かのチェック
    //比較対象の日付のスラッシュを削除
    var checkDateBirth = inputBirthDay.replace(/\//g, '');
    checkDateBirth = new Date(checkDateBirth.substr(0, 4), checkDateBirth.substr(4, 2), checkDateBirth.substr(6, 2), '00', '00', '00');
    if (nowDate < checkDateBirth) {
        errorMsg = "生年月日に未来日付が設定されています";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }

    // 加入日のチェック
    var inputKanyuBi = $("#kanyuBi").val();
    
    // 入力が日付かどうか
    if(!inputKanyuBi.match(/^\d{4}\/\d{2}\/\d{2}$/)){
        errorMsg = "加入日はyyyy/mm/ddの形式で入力してください";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }
    
    // 存在する日付かのチェック
    var hidukeCheckKanyuBi = ckDate(inputKanyuBi);
    if (!hidukeCheckKanyuBi) {
        errorMsg = "加入日に不正な日付が入力されています";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }
    
    
    // 加入日が過去日かのチェック
    //比較対象の日付のスラッシュを削除
    var checkDateKanyuBi = inputKanyuBi.replace(/\//g, '');
    checkDateKanyuBi = new Date(checkDateKanyuBi.substr(0, 4), checkDateKanyuBi.substr(4, 2), checkDateKanyuBi.substr(6, 2), '00', '00', '00');
    /*
    if (checkDateKanyuBi < nowDate) {
        errorMsg = "加入日に過去日付が設定されています";
        $("#errorMsg").html(errorMsg);
        $("#errorMsgArea").show();
        return;
    }
    */
    
    

    // 加入可能年齢のチェック
    var lowAge = 15; // 加入可能年齢（最小値）
    var highAge = 55; // 加入可能年齢（最大値）
    var manryoNenrei = $('input[name="haraikomiManryoNenrei"]:checked').val();
    var birthDay = inputBirthDay.replace(/\//g, '');
    var age = getAge(birthDay, checkDateKanyuBi);
    
    if (manryoNenrei == 99) {
        // 払込満了年齢が終身の場合は加入可能年齢の最大値を65歳に設定
        highAge = 65;
        if (age < lowAge || highAge < age) {
            errorMsg = "払込満了年齢が終身の場合、加入可能年齢は15～65歳です";
            $("#errorMsg").html(errorMsg);
            $("#errorMsgArea").show();
            return;
        }
    } else {
        if (age < lowAge || highAge < age) {
            errorMsg = "払込満了年齢が60歳の場合、加入可能年齢は15～55歳です";
            $("#errorMsg").html(errorMsg);
            $("#errorMsgArea").show();
            return;
        }
        
    }

    $("#confirmation_btn").show();

}

function isZenKatakana(str){
  
  var check = str.match(/^[ァ-ヶｦ-ﾟー]+$/);
  
  if(check){    //"ー"の後ろの文字は全角スペースです。
    return true;
  }else{
    return false;
  }
}

function ckDate(strDate) {
    
    var y = strDate.split("/")[0];
    var m = strDate.split("/")[1] - 1;
    var d = strDate.split("/")[2];
    var date = new Date(y,m,d);
    if(date.getFullYear() != y || date.getMonth() != m || date.getDate() != d){
        return false;
    }
    return true;
}

function getAge(birthDay, checkDateKanyuBi){
    
    var tdate = (checkDateKanyuBi.getFullYear() * 10000) + ((checkDateKanyuBi.getMonth()) * 100 ) + checkDateKanyuBi.getDate();
    return(Math.floor((tdate - birthDay ) / 10000));    
}


