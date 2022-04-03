package com.insurance.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.insurance.app.domain.TcKInput;
import com.insurance.app.domain.TcSList;
import com.insurance.app.domain.TcSSearch;
import com.insurance.app.service.TcSService;
import com.insurance.app.validation.TcSSearchValidation;

@Controller
@RequestMapping("/tc/s")
public class TcSController {

    @Autowired
    private TcSService tcSService;

    @Autowired
    private TcSSearchValidation tcSSearchValidation;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(tcSSearchValidation);
    }

    //被保険者検索画面の表示
    @GetMapping("/search")
    public String tcSSearch(@ModelAttribute("tcSSearch") TcSSearch tcSSearch, Model model) {
        model.addAttribute("tcSSearch", tcSSearch);
        return "tc_s_search";
    }

    //一覧画面の表示
    @PostMapping("/list")
    public String tcSList(@ModelAttribute("tcSSearch") @Validated TcSSearch tcSSearch, BindingResult result,
                        Model model) throws Exception{
        if (result.hasErrors()) {
            //バリデーションエラー
            return "tc_s_search";
        }else {
            try {
                //定期医療契約を取得する
                List<TcSList> tcSLists = tcSService.searchTcSList(tcSSearch.getInsured_person_id());

                if(tcSLists.size() == 0) {
                    //定期医療契約なしのため、その旨をメッセージ出力する
                    result.rejectValue("insured_person_id_str", "WL_nothing",
                            "定期医療契約が存在しません");
                    return "tc_s_search";
                }else {
                    //一覧画面へ遷移
                    model.addAttribute("tcSSearch", tcSSearch);
                    model.addAttribute("tcSLists", tcSLists);
                    return "tc_s_list";
                }

            }catch(Exception e) {
                //テーブル取得で予期せぬ処理が発生
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }

    //契約照会の表示
    @GetMapping("/contract/{contract_key}")
    public String tcSContract(@PathVariable String contract_key, Model model) throws Exception{

        try {

            //contract_keyを「&」で分割する（0:被保険者番号、1:契約番号）
            String[] contract_keys = contract_key.split("&");
            int insured_person_id = Integer.parseInt(contract_keys[0]);
            int contract_id       = Integer.parseInt(contract_keys[1]);

            //照会用の契約情報を取得する（今後のため、複数履歴を考慮してList形式で取得する）
            //List<TcSContract> tcSContracts = tcSService.getTcSContract(insured_person_id, contract_id);
            TcKInput tcKInput = new TcKInput();
            tcKInput.setTcSContract(tcSService.getTcSContract(insured_person_id, contract_id).get(0));
            model.addAttribute("tcKInput", tcKInput);
            model.addAttribute("overlay_display", "0");
            model.addAttribute("screen_info", "input");

            //契約一覧へ戻るための値を設定する
            TcSSearch tcSSearch = new TcSSearch();
            tcSSearch.setInsured_person_id_str(String.valueOf(insured_person_id));
            model.addAttribute("tcSSearch", tcSSearch);

            return "tc_s_contract";

        }catch(Exception e) {
            //テーブル取得で予期せぬ処理が発生
            model.addAttribute("massage", e.getMessage());
            return "cm_error";
        }
    }

}
