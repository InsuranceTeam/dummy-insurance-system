package com.insurance.app.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.insurance.app.domain.WcSSearch;

@Component
public class WcSSearchValidation  implements Validator{

    final int insured_person_id_MIN = 0;
    final int insured_person_id_MAX = 2147483647;

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        WcSSearch wcSSearch = (WcSSearch)target;

        //被保険者番号の数値チェック
        if(wcSSearch.getInsured_person_id_str() != "") {
            try {
                wcSSearch.setInsured_person_id_long(Long.parseLong(wcSSearch.getInsured_person_id_str()));
            }catch(NumberFormatException e) {
                errors.rejectValue("insured_person_id_str", "insured_person_id_not_number",
                        "被保険者番号は数値を入力してください");
            }
        }

        //被保険者番号の最大値チェック（int属性は最大値が「2147483647」）
        if(wcSSearch.getInsured_person_id_long() >= insured_person_id_MIN &&
           wcSSearch.getInsured_person_id_long() <= insured_person_id_MAX) {
            wcSSearch.setInsured_person_id(Math.toIntExact(wcSSearch.getInsured_person_id_long()));
        }else {
            errors.rejectValue("insured_person_id_str", "insured_person_id_min_max",
                    insured_person_id_MIN + "～" + insured_person_id_MAX + "の範囲で入力してください");
        }

    }
}
