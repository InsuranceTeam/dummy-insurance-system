package com.insurance.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import com.insurance.app.domain.TlInput;


@Service
public class TlInputService {


  @Transactional(rollbackFor = Exception.class)
  public boolean execute(TlInput tlInput) {

    boolean result = false;

    result = validate(tlInput);

    return result;

  }


  /* 入力値チェック */
  private boolean validate(TlInput tlInput) {

    boolean result = true;
    String strBirth = "";
    String strStart = "";
    int age = -1;

    /* 氏名カナチェック */
    if (StringUtils.isEmpty(tlInput.getInsured_person_name_kana())) {
      tlInput.setInsured_person_name_kana_error("氏名カナを入力してください。");
      result = false;
    }

    /* 生年月日チェック */
    if (StringUtils.isEmpty(tlInput.getInsured_person_birth_year())
        || StringUtils.isEmpty(tlInput.getInsured_person_birth_month())
        || StringUtils.isEmpty(tlInput.getInsured_person_birth_day())) {
      tlInput.setInsured_person_birth_error("生年月日を入力してください。");
      result = false;
    } else {
      tlInput.setInsured_person_birth_month(
          String.format("%2s", tlInput.getInsured_person_birth_month()).replace(" ", "0"));
      tlInput.setInsured_person_birth_day(
          String.format("%2s", tlInput.getInsured_person_birth_day()).replace(" ", "0"));

      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      sdf.setLenient(false);
      strBirth = tlInput.getInsured_person_birth_year() + tlInput.getInsured_person_birth_month()
          + tlInput.getInsured_person_birth_day();

      try {
        sdf.parse(strBirth);
        LocalDate localDateStart =
            LocalDate.parse(strBirth, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate localDateNow = LocalDate.now();
        if (localDateStart.compareTo(localDateNow) == 1) {
          tlInput.setInsured_person_birth_error("生年月日が不正です。（未来日付）");
          result = false;
        }
      } catch (ParseException e) {
        tlInput.setInsured_person_birth_error("生年月日が不正です。");
        result = false;
      }
    }

    /* 加入日チェック */
    if (StringUtils.isEmpty(tlInput.getStart_year())
        || StringUtils.isEmpty(tlInput.getStart_month())
        || StringUtils.isEmpty(tlInput.getStart_day())) {
      tlInput.setStart_error("加入日を入力してください。");
      result = false;
    } else {
      tlInput.setStart_month(String.format("%2s", tlInput.getStart_month()).replace(" ", "0"));
      tlInput.setStart_day(String.format("%2s", tlInput.getStart_day()).replace(" ", "0"));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      sdf.setLenient(false);
      strStart = tlInput.getStart_year() + tlInput.getStart_month() + tlInput.getStart_day();
      try {
        sdf.parse(strStart);

      } catch (ParseException e) {
        tlInput.setStart_error("加入日が不正です。");
        result = false;
      }
    }

    /* 加入年齢チェック */
    if (result) {
      age = (Integer.parseInt(strStart) - Integer.parseInt(strBirth)) / 10000;
      if (age < 0 || age > 70) {
        tlInput.setInsured_person_birth_error("加入可能年齢を超えています。");
        result = false;
      }
      tlInput.setAge(age);
    }

    return result;

  }
}
