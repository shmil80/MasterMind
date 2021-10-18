package com.shmuel.mastermind.shared.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable
{
    public byte right;
    public byte exacts;
    public boolean rightAnswer;

    public Result()
    {

    }

    public Result(byte exacts, byte rights,boolean rightAnswer)
    {
        this.exacts = exacts;
        this.right = rights;
        this.rightAnswer = rightAnswer;
    }

    public boolean equals(Result other)
    {
        return right == other.right && exacts == other.exacts;
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o || o != null && getClass() == o.getClass() && equals((Result) o);
    }

    @Override
    public int hashCode()
    {
        return 31 * right + exacts;
    }

    public String toString()
    {
        return "exacts:" + exacts + " Rights:" + right;
    }

    protected Result(Parcel in) {
        right =  in.readByte();
        exacts =  in.readByte();
        rightAnswer=in.readByte()==1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(right);
        dest.writeByte(exacts);
        dest.writeByte((byte) (rightAnswer?1:0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

}
