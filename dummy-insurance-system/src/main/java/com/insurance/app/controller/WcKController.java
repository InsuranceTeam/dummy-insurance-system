package com.insurance.app.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.insurance.app.domain.CmKWcActuarial;
import com.insurance.app.domain.WcKConfimation;
import com.insurance.app.domain.WcKInput;
import com.insurance.app.domain.WcSReference;
import com.insurance.app.domain.WcSSearch;
import com.insurance.app.service.CmKActuarialService;
import com.insurance.app.service.WcKService;
import com.insurance.app.service.WcSService;
import com.insurance.app.validation.WcKInputValidation;

@Controller
@RequestMapping("/wc/k")
public class WcKController {

    @Autowired
    private CmKActuarialService cmKActuarialService;

    @Autowired
    private WcKInputValidation wcKInputValidation;

    @Autowired
    private WcKService wcKService;
    
    @Autowired
    private WcSService wcSService;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(wcKInputValidation);
    }

    //F5等でブラウザをリロードしたときに２重更新されることを防ぐためのフラグ
    private boolean reloadFlg = false;

    //入力画面の表示（確認画面で戻るボタンを押下）
    @PostMapping("/input")
    public String returnInput(@ModelAttribute("wcKConfimation") WcKConfimation wcKConfimation, Model model) {

        //解約／取消　入力画面へ戻るための値を設定する
        WcKInput wcKInput = new WcKInput();
        wcKInput.setWcSReference(wcKConfimation.getWcSReference());
        wcKInput.setSelect_cancel(wcKConfimation.getSelect_cancel());
        wcKInput.setCancel_date(wcKConfimation.getCancel_date());
        model.addAttribute("wcKInput", wcKInput);
        model.addAttribute("overlay_display", "1");
        model.addAttribute("screen_info", "input");

        //契約一覧へ戻るための値を設定する
        WcSSearch wcSSearch = new WcSSearch();
        wcSSearch.setInsured_person_id_str(String.valueOf(wcKInput.getWcSReference().getInsured_person_id()));
        model.addAttribute("wcSSearch", wcSSearch);

        return "wc_s_reference";
    }

    //確認画面の表示
    @PostMapping("/confirmation")
    public String checkCancel(@ModelAttribute("wcKInput") @Validated WcKInput wcKInput, BindingResult result,
                                Model model) throws Exception{
        if (result.hasErrors()) {

            //解約／取消　入力画面へ戻るための値を設定する
            model.addAttribute("wcKInput", wcKInput);
            model.addAttribute("overlay_display", "1");
            model.addAttribute("screen_info", "input");

            //契約一覧へ戻るための値を設定する
            WcSSearch wcSSearch = new WcSSearch();
            wcSSearch.setInsured_person_id_str(String.valueOf(wcKInput.getWcSReference().getInsured_person_id()));
            model.addAttribute("wcSSearch", wcSSearch);

            return "wc_s_reference";
        }else {
            try {

                //正常な画面遷移のため、リロードによる誤更新を防ぐためのフラグにfalseを設定する
                reloadFlg = false;

                WcKConfimation wcKConfimation = new WcKConfimation();
                wcKConfimation.setWcSReference(wcKInput.getWcSReference());                           //契約照会情報
                wcKConfimation.setSelect_cancel(wcKInput.getSelect_cancel());                       //解約・取消区分

                //解約処理の場合は、解約日と解約返戻金の編集を行なう
                if(wcKInput.getSelect_cancel().equals("1")) {
                    wcKConfimation.setCancel_date(wcKInput.getCancel_date());                       //解約日
                    wcKConfimation.setCancellation_refund(cancellationRefundCalculation(wcKInput)); //解約返戻金
                }

                model.addAttribute("wcKConfimation", wcKConfimation);
                model.addAttribute("overlay_display", "1");
                model.addAttribute("screen_info", "confirmation");
                return "wc_s_reference";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }

    //更新処理
    @PostMapping("/reference")
    public String update(@ModelAttribute("wcKConfimation") WcKConfimation wcKConfimation, Model model) {
        if(reloadFlg) {
            //F5等でブラウザがリロードされたため、更新処理を行なわない
            String errorInfo =   "ブラウザのリロードによる誤更新を防ぐためにシステムエラー画面へ遷移しました";
            model.addAttribute("massage", errorInfo);
            return "cm_error";
        }else {
            try {
                //ブラウザのリロードによる誤更新を防ぐため、フラグにtrueを設定する
                reloadFlg = true;

                //解約・取消の更新処理を行なう
                String update_end_message =wcKService.update(wcKConfimation);

                
                // 支払情報・契約情報最新更新日取得
                int insured_person_id = wcKConfimation.getWcSReference().getInsured_person_id();
                int contract_id       = wcKConfimation.getWcSReference().getContract_id();
                WcKInput wcKInputDay = new WcKInput();
                wcKInputDay.setWcSReference(wcSService.getWcSReference(insured_person_id, contract_id).get(0));
                
                //解約／取消　入力画面へ戻るための値を設定する
                WcKInput wcKInput = new WcKInput();
                wcKInput.setWcSReference(wcKConfimation.getWcSReference());
                // 支払情報・契約情報最終更新日設定
                wcKInput.getWcSReference().setP_update_at(wcKInputDay.getWcSReference().getP_update_at());        
                wcKInput.getWcSReference().setC_update_at(wcKInputDay.getWcSReference().getC_update_at());
                model.addAttribute("wcKInput", wcKInput);
                model.addAttribute("overlay_display", "2");
                model.addAttribute("screen_info", "input");
                model.addAttribute("update_end_message", update_end_message);

                //契約一覧へ戻るための値を設定する
                WcSSearch wcSSearch = new WcSSearch();
                wcSSearch.setInsured_person_id_str(String.valueOf(wcKInput.getWcSReference().getInsured_person_id()));
                model.addAttribute("wcSSearch", wcSSearch);

                return "wc_s_reference";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }



    //数理モジュールの呼び出し
    public int cancellationRefundCalculation(WcKInput wcKInput) throws Exception{

        WcSReference wcSReference = wcKInput.getWcSReference();

        CmKWcActuarial cmKWcActuarial = new CmKWcActuarial();

        cmKWcActuarial.setInsured_person_birth_date(
                stringToDate(wcSReference.getInsured_person_birth_date(),"uuuuMMdd"));          //生年月日
        cmKWcActuarial.setInsured_person_sex(wcSReference.getInsured_person_sex());             //性別
        cmKWcActuarial.setContract_start_date(                                                 //契約開始日
                stringToDate(wcSReference.getContract_start_date(), "uuuuMMdd"));
        cmKWcActuarial.setContract_maturity_date(                                              //契約満期日
                stringToDate(wcSReference.getContract_maturity_date(), "uuuuMMdd"));
        cmKWcActuarial.setSecurity_type(wcSReference.getSecurity_type());                       //保障種類
        cmKWcActuarial.setEntry_age(wcSReference.getEntry_age());                               //加入年齢
        cmKWcActuarial.setPayment_method(wcSReference.getPayment_method());                     //払込方法
        cmKWcActuarial.setInsurance_money(wcSReference.getInsurance_money());                   //死亡保険金
        cmKWcActuarial.setPremium(wcSReference.getPremium());                                   //掛金
        cmKWcActuarial.setPremium_payment_term(wcSReference.getPremium_payment_term());         //掛金払込期間
        cmKWcActuarial.setPayment_expiration_age(wcSReference.getPayment_expiration_age());     //払込満了年齢
        cmKWcActuarial.setCancel_date(stringToDate(wcKInput.getCancel_date(),"uuuu/MM/dd"));   //解約日

        try {
            //数理モジュールを呼び出して、解約返戻金を算出する
            return  cmKActuarialService.cancellationRefundCalculation(cmKWcActuarial);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //Stringの日付をLocalDate型に変換
    public LocalDate stringToDate(String str, String format) {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(format));
    }
}
