package com.insurance.app.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ibm.icu.text.Transliterator;
import com.insurance.app.domain.CmTcActuarial;
import com.insurance.app.domain.TcCompletion;
import com.insurance.app.domain.TcConfirmation;
import com.insurance.app.domain.TcContracts;
import com.insurance.app.domain.TcInput;
import com.insurance.app.domain.TcInsuredPersons;
import com.insurance.app.service.CmActuarialService;
import com.insurance.app.service.TcService;
import com.insurance.app.validation.TcInputValidation;

@Controller
@RequestMapping("/tc")
public class TcInputController {
	@Autowired
	private TcInputValidation tcInputValidation;
	@Autowired
	private CmActuarialService cmActuarialService;
    @Autowired
    private TcService tcService;


	@GetMapping("/input")
	public String cm_security(@ModelAttribute("TcInput") TcInput tcInput, Model model) {
		return "tc_input";
	}
    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(tcInputValidation);
    }

	@PostMapping("/confirmation")
	public String tc_input(@ModelAttribute("TcInput") @Validated TcInput tcInput, BindingResult result, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("TcInput",tcInput);
			return "tc_input";
		} else {
			try {
				model.addAttribute("TcInput",tcInput);

				 //入力情報の編集
				TcConfirmation tcConfirmation = new TcConfirmation();
				tcConfirmation.setTcInput(tcInput);
				//加入年齢の編集
				LocalDate birth_date = stringToDate(tcInput.getInsured_person_birth_date());
				LocalDate start_date = stringToDate(tcInput.getContract_date());
				tcConfirmation.setEntry_age(Period.between(birth_date, start_date).getYears());

				//掛金の編集
				tcConfirmation.setPremium(premiumCalculation(tcInput));
				//確認画面へ入力情報、加入年齢、掛金を引き継ぐ
				model.addAttribute("TcConfirmation",tcConfirmation);

				return "tc_confirm";

			} catch (Exception e) {
				model.addAttribute("message",e.getMessage());
				return "cm_error";
			}


		}
	}
	   //完了画面の表示
    @PostMapping("/completion")
    public String entry(@ModelAttribute("tcConfirmation") TcConfirmation tcConfirmation, Model model)
                        throws Exception{

            try {

                //被保険者情報の編集を行なう
                TcInsuredPersons tcInsuredPersons = new TcInsuredPersons();
                editInsuredPersons(tcConfirmation, tcInsuredPersons);

                //契約情報の編集を行なう
                TcContracts tcContracts = new TcContracts();
                editContracts(tcConfirmation, tcInsuredPersons, tcContracts);

                //被保険者情報の登録／更新 及び 契約情報の登録を行なう
                String iuComment = tcService.entryContracts(tcInsuredPersons, tcContracts);


                //完了画面に表示する値を編集する（被保険者氏名（カナ）は全角、日付は"yyyy-mm-dd"形式）
                tcInsuredPersons.setInsured_person_name_kana(
                          tcConfirmation.getInsured_person_name_kana_sei() + "　"
                        + tcConfirmation.getInsured_person_name_kana_mei());
                tcInsuredPersons.setInsured_person_birth_date(tcConfirmation.getInsured_person_birth_date());
                tcContracts.setContract_start_date(tcConfirmation.getContract_date());
                //契約満期日
        		LocalDate contract_date = stringToDate(tcConfirmation.getContract_date());
        		LocalDate contract_maturity_date = contract_date.plusYears(tcConfirmation.getContract_term());
        		contract_maturity_date = contract_maturity_date.minusDays(1);
        		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        		String contract_maturity_dateStr = contract_maturity_date.format(dateTimeFormatter);

                tcContracts.setContract_maturity_date(contract_maturity_dateStr);

                //契約終了日
                tcContracts.setContract_end_date("00000000");
                TcCompletion tcCompletion = new TcCompletion();
                tcCompletion.setTcInsuredPersons(tcInsuredPersons);
                tcCompletion.setTcContracts(tcContracts);
                model.addAttribute("tcCompletion", tcCompletion);

                //被保険者情報テーブルが登録／更新のどちらであるかを画面表示する
                model.addAttribute("iuComment", iuComment);

                return "tc_completion";

            }catch(Exception e) {
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }

    }

	public int premiumCalculation(TcInput tcInput) throws Exception {
		CmTcActuarial cmTcActuarial = new CmTcActuarial();
		//保障区分
		cmTcActuarial.setSecurity_type("TC");
		//加入年齢

		LocalDate birth_date = stringToDate(tcInput.getInsured_person_birth_date());
		LocalDate start_date = stringToDate(tcInput.getContract_date());
		cmTcActuarial.setEntry_age(Period.between(birth_date, start_date).getYears());

		//性別
		cmTcActuarial.setInsured_person_sex(tcInput.getInsured_person_sex());
		//払込方法
		cmTcActuarial.setPayment_method(tcInput.getPayment_method());

		//掛金払込期間
		cmTcActuarial.setPremium_payment_term(tcInput.getContract_term());

		//基準日
		cmTcActuarial.setReference_date(stringToDate(tcInput.getContract_date()));
		//入院日額
		cmTcActuarial.setHospitalization_money(tcInput.getHospital_cash());

		try {
			return cmActuarialService.findPremium(cmTcActuarial);
		}catch(Exception e) {
			throw new Exception(e);
		}
	}
    //被保険者情報の登録／更新
    public void editInsuredPersons(TcConfirmation   tcConfirmation,
                                    TcInsuredPersons tcInsuredPersons) throws Exception {
        try {

            //被保険者氏名（漢字）
            if(tcConfirmation.getInsured_person_name_kanji_sei() != "") {
                tcInsuredPersons.setInsured_person_name_kanji(tcConfirmation.getInsured_person_name_kanji_sei() + "　"
                                                              + tcConfirmation.getInsured_person_name_kanji_mei());
            }else {
                tcInsuredPersons.setInsured_person_name_kanji("");
            }

            //被保険者氏名（カナ）⇒ 被保険者氏名(ｶﾅ)
            String name_kana_em = tcConfirmation.getInsured_person_name_kana_sei() + "　"
                    + tcConfirmation.getInsured_person_name_kana_mei();
            Transliterator transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
            String name_kana    = transliterator.transliterate(name_kana_em);
            tcInsuredPersons.setInsured_person_name_kana(name_kana);

            //生年月日
            String birth_date = tcConfirmation.getInsured_person_birth_date().replace("-", "");
            tcInsuredPersons.setInsured_person_birth_date(birth_date);

            //性別
            String sex = tcConfirmation.getInsured_person_sex();
            tcInsuredPersons.setInsured_person_sex(sex);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    //契約情報の登録
    public void editContracts(TcConfirmation   tcConfirmation,
                               TcInsuredPersons tcInsuredPersons,
                               TcContracts      tcContracts) throws Exception {
        try {

            //契約履歴番号
            tcContracts.setContract_history_id(10000);

            //契約開始日
            String start_date = tcConfirmation.getContract_date().replace("-", "");
            tcContracts.setContract_start_date(start_date);

            //契約終了日

    		tcContracts.setContract_end_date("00000000");
            //契約終了事由
            tcContracts.setContract_end_reason("");
            //契約満期日
    		LocalDate contract_date = stringToDate(tcConfirmation.getContract_date());
    		LocalDate contract_maturity_date = contract_date.plusYears(tcConfirmation.getContract_term());
    		contract_maturity_date = contract_maturity_date.minusDays(1);
    		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    		String contract_maturity_dateStr = contract_maturity_date.format(dateTimeFormatter);

            tcContracts.setContract_maturity_date(contract_maturity_dateStr);
            //保障種類
            tcContracts.setSecurity_type("TC");

            //加入年齢
            int entry_age = tcConfirmation.getEntry_age();
            tcContracts.setEntry_age(entry_age);

            //払込方法
            tcContracts.setPayment_method(tcConfirmation.getPayment_method());
            //入院日額
            tcContracts.setInsurance_money(tcConfirmation.getHospital_cash());
            //掛金
            tcContracts.setPremium(tcConfirmation.getPremium());

            //掛金払込期間
            tcContracts.setPremium_payment_term(tcConfirmation.getContract_term());

            //契約期間
            tcContracts.setContract_term(tcConfirmation.getContract_term());
            //払込満了年齢
            tcContracts.setPayment_expiration_age(0);

        }catch(Exception e) {
            throw new Exception(e);
        }
    }


	public LocalDate stringToDate(String str) {
		return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

}