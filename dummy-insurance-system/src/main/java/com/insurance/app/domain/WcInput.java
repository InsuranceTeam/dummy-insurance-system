package com.insurance.app.domain;

import javax.validation.constraints.NotBlank;

public class WcInput {
	
    //被保険者氏名（漢字）・姓
    private String  nameKanjiSei;

    //被保険者氏名（漢字）・名
    private String  nameKanjiMei;

    //被保険者氏名（カナ）
    @NotBlank(message="被保険者氏名の姓（カナ）をセイ欄を入力してください")
    private String  nameKanaSei;

    //被保険者氏名（カナ）
    @NotBlank(message="被保険者氏名の名（カナ）をメイ欄を入力してください")
    private String  nameKanaMei;

    //生年月日
    private String  birthDay;
    
    // 生年月日_年
    private String birthDayYear;
    
    //生年月日_月
    private String birthDayMonth;
    
    //生年月日_日
    private String birthDayDay;

    //性別
    private String  seibetsu;

    //加入日
    @NotBlank(message="終身生命に加入する日を入力してください")
    private String  kanyuBi;

    //入院日額
    private int  nyuinNichigaku;

    //払込満了年齢
    private int  haraikomiManryoNenrei;

    //払込方法
    private String  haraikomiHoho;

    //被保険者氏名（漢字）・姓_getter
    public String getNameKanjiSei() {
        return nameKanjiSei;
    }

    //被保険者氏名（漢字）・姓_setter
    public void setNameKanjiSei(String nameKanjiSei) {
        this.nameKanjiSei = nameKanjiSei;
    }

    //被保険者氏名（漢字）・名_getter
    public String getNameKanjiMei() {
        return nameKanjiMei;
    }

    //被保険者氏名（漢字）・名_setter
    public void setNameKanjiMei(String nameKanjiMei) {
        this.nameKanjiMei = nameKanjiMei;
    }

    //被保険者氏名（カナ）・姓_getter
    public String getNameKanaSei() {
        return nameKanaSei;
    }

    //被保険者氏名（カナ）・姓_setter
    public void setNameKanaSei(String nameKanaSei) {
        this.nameKanaSei = nameKanaSei;
    }

    //被保険者氏名（カナ）・名_getter
    public String getNameKanaMei() {
        return nameKanaMei;
    }

    //被保険者氏名（カナ）・姓_setter
    public void setNameKanaMei(String nameKanaMei) {
        this.nameKanaMei = nameKanaMei;
    }

    //生年月日_getter
    public String getBirthDay() {
        return birthDay;
    }

    //生年月日_setter
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthDayYear() {
		return birthDayYear;
	}

	public void setBirthDayYear(String birthDayYear) {
		this.birthDayYear = birthDayYear;
	}

	public String getBirthDayMonth() {
		return birthDayMonth;
	}

	public void setBirthDayMonth(String birthDayMonth) {
		this.birthDayMonth = birthDayMonth;
	}

	public String getBirthDayDay() {
		return birthDayDay;
	}

	public void setBirthDayDay(String birthDayDay) {
		this.birthDayDay = birthDayDay;
	}

	//性別_getter
    public String getSeibetsu() {
        return seibetsu;
    }

    //性別_setter
    public void setSeibetsu(String seibetsu) {
        this.seibetsu = seibetsu;
    }

    //加入日_getter
    public String getKanyuBi() {
        return kanyuBi;
    }

    //加入日_setter
    public void setKanyuBi(String kanyuBi) {
        this.kanyuBi = kanyuBi;
    }

    //入院日額_getter
    public int getNyuinNichigaku() {
        return nyuinNichigaku;
    }

    //入院日額_setter
    public void setNyuinNichigaku(int nyuinNichigaku) {
        this.nyuinNichigaku = nyuinNichigaku;
    }

    //払込満了年齢_getter
    public int getHaraikomiManryoNenrei() {
        return haraikomiManryoNenrei;
    }

    //払込満了年齢_setter
    public void setHaraikomiManryoNenrei(int haraikomiManryoNenrei) {
        this.haraikomiManryoNenrei = haraikomiManryoNenrei;
    }

    //払込方法_getter
    public String getHaraikomiHoho() {
        return haraikomiHoho;
    }

    //払込方法_setter
    public void setHaraikomiHoho(String haraikomiHoho) {
        this.haraikomiHoho = haraikomiHoho;
    }
}
