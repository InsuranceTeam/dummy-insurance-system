package com.insurance.app.domain;

public class WcConfirmation{

    private String  nameKanjiSei; //被保険者氏名（漢字）・姓
    private String  nameKanjiMei;    //被保険者氏名（漢字）・名
    private String  nameKanaSei;  //被保険者氏名（カナ） 姓 
    private String  nameKanaMei;     //被保険者氏名（カナ） 名
    private String  birthDay;         //生年月日
    private String  seibetsu;                //性別
    private String  kanyuBi;               //加入日
    private int     nyuinNichigaku;         //入院日額
    private int     haraikomiManryoNenrei; //払込満了年齢
    private String  haraikomiHoho;         //払込方法
    private int     entryAge;           //加入年齢
    private int     premium;               //掛金

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

    //加入年齢_getter
    public int getEntryAge() {
        return entryAge;
    }

    //加入年齢_setter
    public void setEntryAge(int entryAge) {
        this.entryAge = entryAge;
    }

    //掛金_getter
    public int getPremium() {
        return premium;
    }

    //掛金_setter
    public void setPremium(int premium) {
        this.premium = premium;
    }

    //入力画面項目_setter
    public void setWcInput(WcInput wcInput) {
    	setNameKanjiSei(wcInput.getNameKanjiSei());
    	setNameKanjiMei(wcInput.getNameKanjiMei());
    	setNameKanaSei(wcInput.getNameKanaSei());
    	setNameKanaMei(wcInput.getNameKanaMei());
    	setBirthDay(wcInput.getBirthDay());
    	setSeibetsu(wcInput.getSeibetsu());
        setKanyuBi(wcInput.getKanyuBi());
        setNyuinNichigaku(wcInput.getNyuinNichigaku());
        setHaraikomiManryoNenrei(wcInput.getHaraikomiManryoNenrei());
        setHaraikomiHoho(wcInput.getHaraikomiHoho());
    };
}
