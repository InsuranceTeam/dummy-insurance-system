package com.insurance.app.domain;

public class TcConfirmation {
	private Long id;
	private String insured_person_name_kanji_sei;
	private String insured_person_name_kanji_mei;
	private String insured_person_name_kana_sei;
	private String insured_person_name_kana_mei;

	private String insured_person_birth_date;

	private String insured_person_sex;

	private String contract_date;

	private String payment_method;

	private int contract_term;

	private int hospital_cash;
	private int entry_age; //加入年齢
	private int premium; //掛金
	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id セットする id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return insured_person_name_kanji_sei
	 */
	public String getInsured_person_name_kanji_sei() {
		return insured_person_name_kanji_sei;
	}
	/**
	 * @param insured_person_name_kanji_sei セットする insured_person_name_kanji_sei
	 */
	public void setInsured_person_name_kanji_sei(String insured_person_name_kanji_sei) {
		this.insured_person_name_kanji_sei = insured_person_name_kanji_sei;
	}
	/**
	 * @return insured_person_name_kanji_mei
	 */
	public String getInsured_person_name_kanji_mei() {
		return insured_person_name_kanji_mei;
	}
	/**
	 * @param insured_person_name_kanji_mei セットする insured_person_name_kanji_mei
	 */
	public void setInsured_person_name_kanji_mei(String insured_person_name_kanji_mei) {
		this.insured_person_name_kanji_mei = insured_person_name_kanji_mei;
	}
	/**
	 * @return insured_person_name_kana_sei
	 */
	public String getInsured_person_name_kana_sei() {
		return insured_person_name_kana_sei;
	}
	/**
	 * @param insured_person_name_kana_sei セットする insured_person_name_kana_sei
	 */
	public void setInsured_person_name_kana_sei(String insured_person_name_kana_sei) {
		this.insured_person_name_kana_sei = insured_person_name_kana_sei;
	}
	/**
	 * @return insured_person_name_kana_mei
	 */
	public String getInsured_person_name_kana_mei() {
		return insured_person_name_kana_mei;
	}
	/**
	 * @param insured_person_name_kana_mei セットする insured_person_name_kana_mei
	 */
	public void setInsured_person_name_kana_mei(String insured_person_name_kana_mei) {
		this.insured_person_name_kana_mei = insured_person_name_kana_mei;
	}
	/**
	 * @return insured_person_birth_date
	 */
	public String getInsured_person_birth_date() {
		return insured_person_birth_date;
	}
	/**
	 * @param insured_person_birth_date セットする insured_person_birth_date
	 */
	public void setInsured_person_birth_date(String insured_person_birth_date) {
		this.insured_person_birth_date = insured_person_birth_date;
	}
	/**
	 * @return insured_person_sex
	 */
	public String getInsured_person_sex() {
		return insured_person_sex;
	}
	/**
	 * @param insured_person_sex セットする insured_person_sex
	 */
	public void setInsured_person_sex(String insured_person_sex) {
		this.insured_person_sex = insured_person_sex;
	}
	/**
	 * @return contract_date
	 */
	public String getContract_date() {
		return contract_date;
	}
	/**
	 * @param contract_date セットする contract_date
	 */
	public void setContract_date(String contract_date) {
		this.contract_date = contract_date;
	}
	/**
	 * @return payment_method
	 */
	public String getPayment_method() {
		return payment_method;
	}
	/**
	 * @param payment_method セットする payment_method
	 */
	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}
	/**
	 * @return contract_term
	 */
	public int getContract_term() {
		return contract_term;
	}
	/**
	 * @param contract_term セットする contract_term
	 */
	public void setContract_term(int contract_term) {
		this.contract_term = contract_term;
	}
	/**
	 * @return hospital_cash
	 */
	public int getHospital_cash() {
		return hospital_cash;
	}
	/**
	 * @param hospital_cash セットする hospital_cash
	 */
	public void setHospital_cash(int hospital_cash) {
		this.hospital_cash = hospital_cash;
	}
	/**
	 * @return entry_age
	 */
	public int getEntry_age() {
		return entry_age;
	}
	/**
	 * @param entry_age セットする entry_age
	 */
	public void setEntry_age(int entry_age) {
		this.entry_age = entry_age;
	}
	/**
	 * @return premium
	 */
	public int getPremium() {
		return premium;
	}
	/**
	 * @param premium セットする premium
	 */
	public void setPremium(int premium) {
		this.premium = premium;
	}


    //入力画面項目_setter
    public void setTcInput(TcInput tcInput) {
    	setInsured_person_name_kanji_sei(tcInput.getInsured_person_name_kanji_sei());
    	setInsured_person_name_kanji_mei(tcInput.getInsured_person_name_kanji_mei());
        setInsured_person_name_kana_sei(tcInput.getInsured_person_name_kana_sei());
        setInsured_person_name_kana_mei(tcInput.getInsured_person_name_kana_mei());
        setInsured_person_birth_date(tcInput.getInsured_person_birth_date());
        setInsured_person_sex(tcInput.getInsured_person_sex());
        setContract_date(tcInput.getContract_date());
        setPayment_method(tcInput.getPayment_method());
        setContract_term(tcInput.getContract_term());
        setHospital_cash(tcInput.getHospital_cash());

    }

}
