package com.insurance.app.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/*
@Controller
@RequestMapping("/wc/input")
public class WcInputController {

  @GetMapping
  public String cm_security(Model model) {
    return "wc_input";
  }
}
*/

import com.ibm.icu.text.Transliterator;
import com.insurance.app.domain.CmWcActuarial;
import com.insurance.app.domain.WcCompletion;
import com.insurance.app.domain.WcConfirmation;
import com.insurance.app.domain.WcContracts;
import com.insurance.app.domain.WcInput;
import com.insurance.app.domain.WcInsuredPersons;
import com.insurance.app.service.CmActuarialService;
import com.insurance.app.service.WcService;

@Controller
@RequestMapping("/wc")
public class WcController {

	/*
	@GetMapping("/input")
	public String cm_security(Model model) {
		return "wc_input";
	}
    */	
	
	
	@Autowired
    private CmActuarialService cmActuarialService;
	
	@Autowired
    private WcService wcService;
	
	//F5等でブラウザをリロードしたときに２重更新されることを防ぐためのフラグ
    private boolean reloadFlg = false;
	
	//入力画面の表示
    @GetMapping("/input")
    public String wcInput(@ModelAttribute("wcInput") WcInput wcInput, Model model) {
        model.addAttribute("wcInput", wcInput);
        return "wc_input";
    }
	
	@PostMapping("/confirmation")
	public String check(@ModelAttribute("wcInput") @Validated WcInput wcInput, BindingResult result, Model model)
            throws Exception{
        if (result.hasErrors()) {
            return "wc_input";
        }else {
            try {
                model.addAttribute("wcInput", wcInput);

                //入力情報の編集
                WcConfirmation wcConfimation = new WcConfirmation();
                wcConfimation.setWcInput(wcInput);

                //加入年齢の編集
                LocalDate birth_date = stringToDate(wcInput.getBirthDay());
                LocalDate start_date = stringToDate(wcInput.getKanyuBi());
                wcConfimation.setEntryAge(Period.between(birth_date, start_date).getYears());

                //掛金の編集
                wcConfimation.setPremium(premiumCalculation(wcInput));
                //wcConfimation.setPremium(1000);

                model.addAttribute("wcConfimation", wcConfimation);
                
                //正常な画面遷移のため、リロードによる誤更新を防ぐためのフラグにfalseを設定する
                reloadFlg = false;

                return "wc_confirmation";

           }catch(Exception e) {
               model.addAttribute("massage", e.getMessage());
               return "cm_error";
           }
        }
    }
	
	//数理モジュールの呼び出し
    public int premiumCalculation(WcInput wcInput) throws Exception{
        CmWcActuarial cmWcActuarial = new CmWcActuarial();

        //保障区分
        cmWcActuarial.setSecurity_type("WC");

        //加入時年齢
        LocalDate birth_date = stringToDate(wcInput.getBirthDay());
        LocalDate start_date = stringToDate(wcInput.getKanyuBi());
        cmWcActuarial.setEntry_age(Period.between(birth_date, start_date).getYears());

        //性別
        cmWcActuarial.setInsured_person_sex(wcInput.getSeibetsu());

        //払込方法
        cmWcActuarial.setPayment_method(wcInput.getHaraikomiHoho());

        //払込満了年齢
        cmWcActuarial.setPayment_expiration_age(wcInput.getHaraikomiManryoNenrei());

        //加入日
        cmWcActuarial.setReference_date(LocalDate.parse(wcInput.getKanyuBi(),DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        //死亡保険金
        cmWcActuarial.setHospitalization_money(wcInput.getNyuinNichigaku());

        try {
            return  cmActuarialService.findPremium(cmWcActuarial);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

  //完了画面の表示
    @PostMapping("/completion")
    public String entry(@ModelAttribute("wcConfirmation") WcConfirmation wcConfirmation, Model model)
                        throws Exception{
        if(reloadFlg) {
            //F5等でブラウザがリロードされたため、更新処理を行なわない
            String errorInfo =   "ブラウザのリロードによる誤更新を防ぐためにシステムエラー画面へ遷移しました";
            model.addAttribute("massage", errorInfo);
            return "cm_error";
        }else {
            try {
                //ブラウザのリロードによる誤更新を防ぐため、フラグにtrueを設定する
                reloadFlg = true;

                //被保険者情報の編集を行なう
                WcInsuredPersons wcInsuredPersons = new WcInsuredPersons();
                editInsuredPersons(wcConfirmation, wcInsuredPersons);

                //契約情報の編集を行なう
                WcContracts wcContracts = new WcContracts();
                editContracts(wcConfirmation, wcInsuredPersons, wcContracts);

                //被保険者情報の登録／更新 及び 契約情報の登録を行なう
                String iuComment = wcService.entryContracts(wcInsuredPersons, wcContracts);


                //完了画面に表示する値を編集する（被保険者氏名（カナ）は全角、日付は"yyyy/mm/dd"形式）
                wcInsuredPersons.setInsured_person_name_kana(
                          wcConfirmation.getNameKanaSei() + "　"
                        + wcConfirmation.getNameKanaMei());
                wcInsuredPersons.setInsured_person_birth_date(wcConfirmation.getBirthDay());
                wcContracts.setContract_start_date(wcConfirmation.getKanyuBi());
                wcContracts.setContract_end_date("0000/00/00");
                wcContracts.setContract_maturity_date("9999/12/31");

                WcCompletion wcCompletion = new WcCompletion();
                wcCompletion.setWcInsuredPersons(wcInsuredPersons);
                wcCompletion.setWcContracts(wcContracts);
                model.addAttribute("wcCompletion", wcCompletion);

                //被保険者情報テーブルが登録／更新のどちらであるかを画面表示する
                model.addAttribute("iuComment", iuComment);

                return "wc_completion";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }
    
  //被保険者情報の登録／更新
    public void editInsuredPersons(WcConfirmation   wcConfirmation,
                                    WcInsuredPersons wcInsuredPersons) throws Exception {
        try {

            //被保険者氏名（漢字）
            if(wcConfirmation.getNameKanjiSei() != "") {
                wcInsuredPersons.setInsured_person_name_kanji(wcConfirmation.getNameKanjiSei() + "　"
                                                              + wcConfirmation.getNameKanjiMei());
            }else {
                wcInsuredPersons.setInsured_person_name_kanji("");
            }

            //被保険者氏名（カナ）⇒ 被保険者氏名(ｶﾅ)
            String name_kana_em = wcConfirmation.getNameKanaSei() + "　"
                    + wcConfirmation.getNameKanaMei();
            Transliterator transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
            String name_kana    = transliterator.transliterate(name_kana_em);
            wcInsuredPersons.setInsured_person_name_kana(name_kana);

            //生年月日
            String birth_date = wcConfirmation.getBirthDay().replace("/", "");
            wcInsuredPersons.setInsured_person_birth_date(birth_date);

            //性別
            String sex = wcConfirmation.getSeibetsu();
            wcInsuredPersons.setInsured_person_sex(sex);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //契約情報の登録
    public void editContracts(WcConfirmation   wcConfirmation,
                               WcInsuredPersons wcInsuredPersons,
                               WcContracts      wcContracts) throws Exception {
        try {

            //契約履歴番号
            wcContracts.setContract_history_id(10000);

            //契約開始日
            String start_date = wcConfirmation.getKanyuBi().replace("/", "");
            wcContracts.setContract_start_date(start_date);

            //契約終了日
            wcContracts.setContract_end_date("00000000");
            //契約終了事由
            wcContracts.setContract_end_reason("");
            //契約満期日
            wcContracts.setContract_maturity_date("99991231");
            //保障種類
            wcContracts.setSecurity_type("WC");

            //加入年齢
            int entry_age = wcConfirmation.getEntryAge();
            wcContracts.setEntry_age(entry_age);

            //払込方法
            wcContracts.setPayment_method(wcConfirmation.getHaraikomiHoho());
            //（保険金）入院日額
            wcContracts.setInsurance_money(wcConfirmation.getNyuinNichigaku());
            //掛金
            wcContracts.setPremium(wcConfirmation.getPremium());

            //掛金払込期間
            int premium_payment_term = wcConfirmation.getHaraikomiManryoNenrei() == 99 ? 99 : 60 - entry_age;
            wcContracts.setPremium_payment_term(premium_payment_term);

            //契約期間
            wcContracts.setContract_term(99);
            //払込満了年齢
            wcContracts.setPayment_expiration_age(wcConfirmation.getHaraikomiManryoNenrei());

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    
	//Stringの日付をLocalDate型に変換
    public LocalDate stringToDate(String str) {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}