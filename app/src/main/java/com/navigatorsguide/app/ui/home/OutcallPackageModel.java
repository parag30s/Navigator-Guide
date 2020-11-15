package com.navigatorsguide.app.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class OutcallPackageModel implements Parcelable {

    final private long packId;
    final private String packName;
    final private String countryISO;
    final private int validity;
    final private float buyingPrice;
    private long activationDate;
    private long expirationDate;
    private boolean isAutoRenewOn;
    private String discount;
    private int talkTimeRemaining;
    private boolean displayAutoRenewable;
    private boolean isUnlimited;

    public OutcallPackageModel(long packId, String packName, String countryISO, int validity, float buyingPrice) {
        this.packId = packId;
        this.packName = packName;
        this.countryISO = countryISO;
        this.buyingPrice = buyingPrice;
        this.validity = validity;
    }

    public long getPackId() {
        return packId;
    }

    public String getPackName() {
        return packName;
    }

    public String getCountryISO() {
        return countryISO;
    }


    public float getBuyingPrice() {
        return buyingPrice;
    }

    public int getValidity() {
        return validity;
    }

    private OutcallPackageModel(Parcel in) {
        packId = in.readLong();
        packName = in.readString();
        countryISO = in.readString();
        buyingPrice = in.readFloat();
        validity = in.readInt();
        activationDate = in.readLong();
        expirationDate = in.readLong();
        isAutoRenewOn = in.readInt() == 1 ? true : false;
        discount = in.readString();
        talkTimeRemaining = in.readInt();
        displayAutoRenewable = in.readInt() == 1 ? true : false;
        isUnlimited = in.readInt() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(packId);
        dest.writeString(packName);
        dest.writeString(countryISO);
        dest.writeFloat(buyingPrice);
        dest.writeInt(validity);
        dest.writeLong(activationDate);
        dest.writeLong(expirationDate);
        dest.writeInt(isAutoRenewOn ? 1 : 0);
        dest.writeString(discount);
        dest.writeInt(talkTimeRemaining);
        dest.writeInt(displayAutoRenewable? 1 : 0);
        dest.writeInt(isUnlimited? 1 : 0);
    }

    public static final Creator<OutcallPackageModel> CREATOR = new Creator<OutcallPackageModel>() {
        public OutcallPackageModel createFromParcel(Parcel in) {
            return new OutcallPackageModel(in);
        }

        public OutcallPackageModel[] newArray(int size) {
            return new OutcallPackageModel[size];
        }
    };

    public boolean isAutoRenewOn() {
        return isAutoRenewOn;
    }

    public void setIsAutoRenewOn(boolean isAutoRenewOn) {
        this.isAutoRenewOn = isAutoRenewOn;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(long activationDate) {
        this.activationDate = activationDate;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getTalkTimeRemaining() {
        return talkTimeRemaining;
    }

    public void setTalkTimeRemaining(int talkTimeRemaining) {
        this.talkTimeRemaining = talkTimeRemaining;
    }

    public boolean isDisplayAutoRenewable() {
        return displayAutoRenewable;
    }

    public void setDisplayAutoRenewable(boolean displayAutoRenewable) {
        this.displayAutoRenewable = displayAutoRenewable;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }

    public void setUnlimited(boolean unlimited) {
        isUnlimited = unlimited;
    }
}
