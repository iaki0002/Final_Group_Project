package com.example.finalgroupproject.currency;


/**
 * this class is responsible for the parts of the spinner (flag + name)
 */
public class CountryItem {

    private String mCountryName;
    private int mFlagImage;

    public CountryItem(String countryName, int flagImage) {
        mCountryName = countryName;
        mFlagImage = flagImage;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public int getFlagImage() {
        return mFlagImage;
    }

}
