package com.insurance.app.domain;

public class TlInput {

  private String insured_person_name_kanji; // 被保険者氏名（漢字）
  private String insured_person_name_kana; // 被保険者氏名（カナ）
  private String insured_person_name_kana_error; // 被保険者氏名（カナ）- エラー
  private String insured_person_birth_year; // 生年月日 - 年
  private String insured_person_birth_month; // 生年月日 - 月
  private String insured_person_birth_day; // 生年月日 - 日
  private String insured_person_birth_error; // 生年月日 - エラー
  private String insured_person_sex = "1"; // 性別
  private String start_year; // 加入日 - 年
  private String start_month; // 加入日 - 月
  private String start_day; // 加入日 - 日
  private String start_error; // 加入日 - エラー
  private String payment_method = "1"; // 払込方法
  private String contract_period = "5"; // 契約期間
  private int insurance_money = 100; // 死亡保険金
  private int price_premium; // 掛金
  private int age; // 加入時年齢

  // 加入日 - 年_getter
  public String getStart_year() {
    return start_year;
  }

  // 加入日 - 年_setter
  public void setStart_year(String start_year) {
    this.start_year = start_year;
  }

  // 加入日 - 月_getter
  public String getStart_month() {
    return start_month;
  }

  // 加入日 - 月_setter
  public void setStart_month(String start_month) {
    this.start_month = start_month;
  }

  // 加入日 - 日_getter
  public String getStart_day() {
    return start_day;
  }

  // 加入日 - 日_setter
  public void setStart_day(String start_day) {
    this.start_day = start_day;
  }

  // 契約期間_getter
  public String getContract_period() {
    return contract_period;
  }

  // 契約期間_setter
  public void setContract_period(String contract_period) {
    this.contract_period = contract_period;
  }

  // 被保険者氏名（漢字）_getter
  public String getInsured_person_name_kanji() {
    return insured_person_name_kanji;
  }

  // 被保険者氏名（漢字）_setter
  public void setInsured_person_name_kanji(String insured_person_name_kanji) {
    this.insured_person_name_kanji = insured_person_name_kanji;
  }

  // 被保険者氏名（カナ）_getter
  public String getInsured_person_name_kana() {
    return insured_person_name_kana;
  }

  // 被保険者氏名（カナ）_setter
  public void setInsured_person_name_kana(String insured_person_name_kana) {
    this.insured_person_name_kana = insured_person_name_kana;
  }

  // 生年月日 - 年_getter
  public String getInsured_person_birth_year() {
    return insured_person_birth_year;
  }

  // 生年月日 - 年_setter
  public void setInsured_person_birth_year(String insured_person_birth_year) {
    this.insured_person_birth_year = insured_person_birth_year;
  }

  // 生年月日 - 月_getter
  public String getInsured_person_birth_month() {
    return insured_person_birth_month;
  }

  // 生年月日 - 月_setter
  public void setInsured_person_birth_month(String insured_person_birth_month) {
    this.insured_person_birth_month = insured_person_birth_month;
  }

  // 生年月日 - 日_getter
  public String getInsured_person_birth_day() {
    return insured_person_birth_day;
  }

  // 生年月日 - 日_setter
  public void setInsured_person_birth_day(String insured_person_birth_day) {
    this.insured_person_birth_day = insured_person_birth_day;
  }

  // 性別_getter
  public String getInsured_person_sex() {
    return insured_person_sex;
  }

  // 性別_setter
  public void setInsured_person_sex(String insured_person_sex) {
    this.insured_person_sex = insured_person_sex;
  }

  // 払込方法_getter
  public String getPayment_method() {
    return payment_method;
  }

  // 払込方法_setter
  public void setPayment_method(String payment_method) {
    this.payment_method = payment_method;
  }

  // 死亡保険金_getter
  public int getInsurance_money() {
    return insurance_money;
  }

  // 死亡保険金_setter
  public void setInsurance_money(int insurance_money) {
    this.insurance_money = insurance_money;
  }

  // 被保険者氏名（カナ）- エラー_getter
  public String getInsured_person_name_kana_error() {
    return insured_person_name_kana_error;
  }

  // 被保険者氏名（カナ）- エラー_setter
  public void setInsured_person_name_kana_error(String insured_person_name_kana_error) {
    this.insured_person_name_kana_error = insured_person_name_kana_error;
  }

  // 生年月日 - エラー_getter
  public String getInsured_person_birth_error() {
    return insured_person_birth_error;
  }

  // 生年月日 - エラー_setter
  public void setInsured_person_birth_error(String insured_person_birth_error) {
    this.insured_person_birth_error = insured_person_birth_error;
  }

  // 加入日 - エラー_getter
  public String getStart_error() {
    return start_error;
  }

  // 加入日 - エラー_setter
  public void setStart_error(String start_error) {
    this.start_error = start_error;
  }

  // 掛金_getter
  public int getPrice_premium() {
    return price_premium;
  }

  // 掛金_setter
  public void setPrice_premium(int price_premium) {
    this.price_premium = price_premium;
  }

  // 加入時年齢_getter
  public int getAge() {
    return age;
  }

  // 加入時年齢_setter
  public void setAge(int age) {
    this.age = age;
  }

}
