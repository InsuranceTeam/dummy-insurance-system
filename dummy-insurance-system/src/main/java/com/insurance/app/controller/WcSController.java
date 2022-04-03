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

import com.insurance.app.domain.WcKInput;
import com.insurance.app.domain.WcSList;
import com.insurance.app.domain.WcSSearch;
import com.insurance.app.service.WcSService;
import com.insurance.app.validation.WcSSearchValidation;

@Controller
@RequestMapping("/wc/s")
public class WcSController {

    @Autowired
    private WcSService wcSService;

    @Autowired
    private WcSSearchValidation wcSSearchValidation;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(wcSSearchValidation);
    }

    //被保険者検索画面の表示
    @GetMapping("/search")
    public String wcSSearch(@ModelAttribute("wcSSearch") WcSSearch wcSSearch, Model model) {
        model.addAttribute("wcSSearch", wcSSearch);
        return "wc_s_search";
    }

    //一覧画面の表示
    @PostMapping("/list")
    public String wcSList(@ModelAttribute("wcSSearch") @Validated WcSSearch wcSSearch, BindingResult result,
                        Model model) throws Exception{
        if (result.hasErrors()) {
            //バリデーションエラー
            return "wc_s_search";
        }else {
            try {
                //終身医療契約を取得する
                List<WcSList> wcSLists = wcSService.searchWcSList(wcSSearch.getInsured_person_id());

                if(wcSLists.size() == 0) {
                    //終身医療契約なしのため、その旨をメッセージ出力する
                    result.rejectValue("insured_person_id_str", "WC_nothing",
                            "終身医療の既契約が存在しません");
                    return "wc_s_search";
                }else {
                    //一覧画面へ遷移
                    model.addAttribute("wcSSearch", wcSSearch);
                    model.addAttribute("wcSLists", wcSLists);
                    return "wc_s_list";
                }

            }catch(Exception e) {
                //テーブル取得で予期せぬ処理が発生
                model.addAttribute("massage", e.getMessage());
                return "cm_error";
            }
        }
    }

    //契約照会の表示
    @GetMapping("/reference/{reference_key}")
    public String wcSContract(@PathVariable String reference_key, Model model) throws Exception{

        try {

            //contract_keyを「&」で分割する（0:被保険者番号、1:契約番号）
            String[] reference_keys = reference_key.split("&");
            int insured_person_id = Integer.parseInt(reference_keys[0]);
            int contract_id       = Integer.parseInt(reference_keys[1]);

            WcKInput wcKInput = new WcKInput();
            
            //照会用の契約情報を取得する（今後のため、複数履歴を考慮してList形式で取得する）
            //List<WcSReference> wcSReference = wcSService.getWcSReference(insured_person_id, contract_id);
            //model.addAttribute("wcSReference", wcSReference);
            wcKInput.setWcSReference(wcSService.getWcSReference(insured_person_id, contract_id).get(0));
            
            
            //解約・取消入力画面用のエリアを設定
            model.addAttribute("wcKInput", wcKInput);
            model.addAttribute("overlay_display", "0");
            model.addAttribute("screen_info", "input");

            //契約一覧へ戻るための値を設定する
            WcSSearch wcSSearch = new WcSSearch();
            wcSSearch.setInsured_person_id_str(String.valueOf(insured_person_id));
            model.addAttribute("wcSSearch", wcSSearch);

            return "wc_s_reference";

        }catch(Exception e) {
            //テーブル取得で予期せぬ処理が発生
            model.addAttribute("massage", e.getMessage());
            return "cm_error";
        }
    }

}
