package com.insurance.app.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.insurance.app.domain.TcSSearch;

@Component
public class TcSSearchValidation  implements Validator{

    final int insured_person_id_MIN = 0;
    final int insured_person_id_MAX = 2147483647;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        TcSSearch tcSSearch = (TcSSearch)target;

        //被保険者番号の数値チェック
        if(tcSSearch.getInsured_person_id_str() != "") {
            try {
                tcSSearch.setInsured_person_id_long(Long.parseLong(tcSSearch.getInsured_person_id_str()));
            }catch(NumberFormatException e) {
                errors.rejectValue("insured_person_id_str", "insured_person_id_not_number",
                        "被保険者番号は数値を入力してください");
            }
        }

        //被保険者番号の最大値チェック（int属性は最大値が「2147483647」）
        if(tcSSearch.getInsured_person_id_long() >= insured_person_id_MIN &&
           tcSSearch.getInsured_person_id_long() <= insured_person_id_MAX) {
            tcSSearch.setInsured_person_id(Math.toIntExact(tcSSearch.getInsured_person_id_long()));
        }else {
            errors.rejectValue("insured_person_id_str", "insured_person_id_min_max",
                    insured_person_id_MIN + "～" + insured_person_id_MAX + "の範囲で入力してください");
        }

    }
}
