package com.stevenswang.agerasample.entity;

/**
 * Created by wolun on 16/5/3.
 */
public class TelInfoEntity {

    /**
     * mts : 1515713
     * province : 浙江
     * catName : 中国移动
     * telString : 15157132053
     * areaVid : 30510
     * ispVid : 3236139
     * carrier : 浙江移动
     */

    private String mts;
    private String province;
    private String catName;
    private String telString;
    private String areaVid;
    private String ispVid;
    private String carrier;

    public String getMts() {
        if (mts==null){
            mts = "";
        }
        return mts;
    }


    public String getProvince() {
        if (province == null){
            province = "";
        }
        return province;
    }


    public String getCatName() {
        if (catName == null){
            catName = "";
        }
        return catName;
    }


    public String getTelString() {
        if (telString == null){
            telString = "";
        }
        return telString;
    }

    public String getAreaVid() {
        if (areaVid == null){
            areaVid = "";
        }
        return areaVid;
    }


    public String getIspVid() {
        if (ispVid == null){
            ispVid = "";
        }
        return ispVid;
    }


    public String getCarrier() {
        if (carrier == null){
            carrier = "";
        }
        return carrier;
    }

}
