package com.qiming.fawcard.synthesize.base;

public class CommonInfoHolder {
    private static CommonInfoHolder instance;
    private String mToken;
    private String mTSPToken;
    private String mVin = "LFP8C7PC2J1D45953";

    private CommonInfoHolder() {
    }

    public static CommonInfoHolder getInstance() {
        if (instance == null) {
            synchronized (CommonInfoHolder.class) {
                if (instance == null) {
                    instance = new CommonInfoHolder();
                }
            }
        }
        return instance;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getTSPToken() {
        return mTSPToken;
    }

    public void setTSPToken(String tspToken) {
        this.mTSPToken = tspToken;
    }

    public String getVin() {
        return mVin;
    }

    public void setVin(String vin) {
        this.mVin = vin;
    }
}
