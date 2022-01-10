package com.insurance.app.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.insurance.app.domain.TcInput;

@Component
public class TcInputValidation implements Validator{

    /**
     * 全角チェックを行うための正規表現
     * (コンパイルに時間がかかるためあらかじめ定数化しておく)
     */
    private static Pattern pattern = Pattern.compile("^[^!-~｡-ﾟ]*$");

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        TcInput tcInput = (TcInput)target;

        //被保険者氏名（漢字）のバリデーション
        validateKanjiName(tcInput.getInsured_person_name_kanji_sei(),
                          tcInput.getInsured_person_name_kanji_mei(),
                          errors);

        //被保険者氏名（カナ）のバリデーション
        validateKanaName(tcInput.getInsured_person_name_kana_sei(),
                         tcInput.getInsured_person_name_kana_mei(),
                         errors);

        //生年月日と加入日の比較チェック、加入時年齢チェック
        String birth_date_str = tcInput.getInsured_person_birth_date();
        String start_date_str = tcInput.getContract_date();
        if(!birth_date_str.isEmpty() && !start_date_str.isEmpty()){
            validateDate(LocalDate.parse(birth_date_str, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                         LocalDate.parse(start_date_str, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                         errors);
        }
    }

    //被保険者氏名（漢字）チェック
    public void validateKanjiName(String kanji_surname, String kanji_name, Errors errors) {

        //被保険者氏名（漢字）　姓・名の入力整合性チェック
        if((kanji_surname.isEmpty() && !kanji_name.isEmpty()) ||
           (!kanji_surname.isEmpty() && kanji_name.isEmpty())){
            errors.rejectValue("insured_person_name_kanji_sei", "name_kanji",
                    "姓・名は両方入力　もしくは　両方未入力としてください");
        }

        //被保険者氏名（漢字）の全角チェック
        if(pattern.matcher(kanji_surname).find() && pattern.matcher(kanji_name).find()) {
        }else {
            errors.rejectValue("insured_person_name_kanji_sei", "name_kanji",
                    "全角で入力してください");
        };
    }

    //被保険者氏名（カナ）チェック
    public void validateKanaName(String kana_surname, String kana_name, Errors errors) {
    	//被保険者氏名（カナ）の全角チェック
        if(pattern.matcher(kana_surname).find() && pattern.matcher(kana_name).find()) {
        }else {
            errors.rejectValue("insured_person_name_kana_sei", "name_kana",
                    "全角で入力してください");
        };
    }

    //生年月日と加入日比較チェック、加入時年齢チェック
    public void validateDate(LocalDate birth_date, LocalDate start_date, Errors errors) {
        //生年月日と加入日の比較チェック
        if(start_date.isBefore(birth_date)) {
            errors.rejectValue("contract_date", "birth_date>start_date",
                    "生年月日と同日、または生年月日より先の日付を入力してください");
        }else {
            //加入時年齢チェック
            int entry_age  = Period.between(birth_date, start_date).getYears();
            if(entry_age > 70) {

                    errors.rejectValue("contract_date", "entry_age_over_70",
                            "上限年齢７０歳を超えています");

            }
        }
    }
}
