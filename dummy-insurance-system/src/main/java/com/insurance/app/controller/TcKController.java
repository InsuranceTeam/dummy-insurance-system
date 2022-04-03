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

import com.insurance.app.domain.CmKTcActuarial;
import com.insurance.app.domain.TcKConfimation;
import com.insurance.app.domain.TcKInput;
import com.insurance.app.domain.TcSContract;
import com.insurance.app.domain.TcSSearch;
import com.insurance.app.service.CmKActuarialService;
import com.insurance.app.service.TcKService;
import com.insurance.app.validation.TcKInputValidation;

@Controller
@RequestMapping("/tc/k")
public class TcKController {

    @Autowired
    private CmKActuarialService cmKActuarialService;

    @Autowired
    private TcKInputValidation tcKInputValidation;

    @Autowired
    private TcKService tcKService;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(tcKInputValidation);
    }

    //F5等でブラウザをリロードしたときに２重更新されることを防ぐためのフラグ
    private boolean reloadFlg = false;

    //入力画面の表示（確認画面で戻るボタンを押下）
    @PostMapping("/input")
    public String returnInput(@ModelAttribute("tcKConfimation") TcKConfimation tcKConfimation, Model model) {

        //解約／取消　入力画面へ戻るための値を設定する
        TcKInput tcKInput = new TcKInput();
        tcKInput.setTcSContract(tcKConfimation.getTcSContract());
        tcKInput.setSelect_cancel(tcKConfimation.getSelect_cancel());
        tcKInput.setCancel_date(tcKConfimation.getCancel_date().replace("/", "-"));
        model.addAttribute("tcKInput", tcKInput);
        model.addAttribute("overlay_display", "1");
        model.addAttribute("screen_info", "input");

        //契約一覧へ戻るための値を設定する
        TcSSearch tcSSearch = new TcSSearch();
        tcSSearch.setInsured_person_id_str(String.valueOf(tcKInput.getTcSContract().getInsured_person_id()));
        model.addAttribute("tcSSearch", tcSSearch);

        return "tc_s_contract";
    }

    //確認画面の表示
    @PostMapping("/confirmation")
    public String checkCancel(@ModelAttribute("tcKInput") @Validated TcKInput tcKInput, BindingResult result,
                                Model model) throws Exception{
        if (result.hasErrors()) {

            //解約／取消　入力画面へ戻るための値を設定する
            model.addAttribute("tcKInput", tcKInput);
            model.addAttribute("overlay_display", "1");
            model.addAttribute("screen_info", "input");

            //契約一覧へ戻るための値を設定する
            TcSSearch tcSSearch = new TcSSearch();
            tcSSearch.setInsured_person_id_str(String.valueOf(tcKInput.getTcSContract().getInsured_person_id()));
            model.addAttribute("tcSSearch", tcSSearch);

            return "tc_s_contract";
        }else {
            try {

                //正常な画面遷移のため、リロードによる誤更新を防ぐためのフラグにfalseを設定する
                reloadFlg = false;

                TcKConfimation tcKConfimation = new TcKConfimation();
                tcKConfimation.setTcSContract(tcKInput.getTcSContract());                           //契約照会情報
                tcKConfimation.setSelect_cancel(tcKInput.getSelect_cancel());                       //解約・取消区分

                //解約処理の場合は、解約日と解約返戻金の編集を行なう
                if(tcKInput.getSelect_cancel().equals("1")) {
                    tcKConfimation.setCancel_date(tcKInput.getCancel_date().replace("-", "/"));     //解約日
                    tcKConfimation.setCancellation_refund(cancellationRefundCalculation(tcKInput)); //解約返戻金
                }
                //契約一覧へ戻るための値を設定する
                TcSSearch tcSSearch = new TcSSearch();
                tcSSearch.setInsured_person_id_str(String.valueOf(tcKInput.getTcSContract().getInsured_person_id()));
                model.addAttribute("tcSSearch", tcSSearch);

                model.addAttribute("tcKConfimation", tcKConfimation);
                model.addAttribute("overlay_display", "1");
                model.addAttribute("screen_info", "confirmation");
                return "tc_s_contract";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }

    //更新処理
    @PostMapping("/contract")
    public String update(@ModelAttribute("tcKConfimation") TcKConfimation tcKConfimation, Model model) {
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
                String update_end_message =tcKService.update(tcKConfimation);

                //解約／取消　入力画面へ戻るための値を設定する
                TcKInput tcKInput = new TcKInput();
                tcKInput.setTcSContract(tcKConfimation.getTcSContract());
                model.addAttribute("tcKInput", tcKInput);
                model.addAttribute("overlay_display", "2");
                model.addAttribute("screen_info", "input");
                model.addAttribute("update_end_message", update_end_message);

                //契約一覧へ戻るための値を設定する
                TcSSearch tcSSearch = new TcSSearch();
                tcSSearch.setInsured_person_id_str(String.valueOf(tcKInput.getTcSContract().getInsured_person_id()));
                model.addAttribute("tcSSearch", tcSSearch);

                return "tc_s_contract";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }



    //数理モジュールの呼び出し
    public int cancellationRefundCalculation(TcKInput tcKInput) throws Exception{

        TcSContract tcSContract = tcKInput.getTcSContract();

        CmKTcActuarial cmKTcActuarial = new CmKTcActuarial();

        cmKTcActuarial.setInsured_person_birth_date(
                stringToDate(tcSContract.getInsured_person_birth_date(),"uuuuMMdd"));          //生年月日
        cmKTcActuarial.setInsured_person_sex(tcSContract.getInsured_person_sex());             //性別
        cmKTcActuarial.setContract_start_date(                                                 //契約開始日
                stringToDate(tcSContract.getContract_start_date(), "uuuuMMdd"));
        cmKTcActuarial.setContract_maturity_date(                                              //契約満期日
                stringToDate(tcSContract.getContract_maturity_date(), "uuuuMMdd"));
        cmKTcActuarial.setSecurity_type(tcSContract.getSecurity_type());                       //保障種類
        cmKTcActuarial.setEntry_age(tcSContract.getEntry_age());                               //加入年齢
        cmKTcActuarial.setPayment_method(tcSContract.getPayment_method());                     //払込方法
        cmKTcActuarial.setHospitalization_money(tcSContract.getInsurance_money());             //入院日額
        cmKTcActuarial.setPremium(tcSContract.getPremium());                                   //掛金
        cmKTcActuarial.setPremium_payment_term(tcSContract.getPremium_payment_term());         //掛金払込期間
        cmKTcActuarial.setCancel_date(stringToDate(tcKInput.getCancel_date(),"uuuu-MM-dd"));   //解約日

        try {
            //数理モジュールを呼び出して、解約返戻金を算出する
            return  cmKActuarialService.cancellationRefundCalculation(cmKTcActuarial);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //Stringの日付をLocalDate型に変換
    public LocalDate stringToDate(String str, String format) {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(format));
    }
}
