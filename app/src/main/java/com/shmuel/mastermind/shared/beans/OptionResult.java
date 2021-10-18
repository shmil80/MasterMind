package com.shmuel.mastermind.shared.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class OptionResult implements Parcelable
{
    private Option option;
    private Result result;

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public OptionResult(Option option) {
        this.option = option;
    }
    public OptionResult(byte[] option) {
        this.option =new Option(option);
    }

    protected OptionResult(Parcel in) {
        option = in.readParcelable(Option.class.getClassLoader());
        if(in.readByte()==1)
        {
            result = in.readParcelable(Result.class.getClassLoader());
        }
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(option, flags);
        if(result==null)
        {
            dest.writeByte((byte)0);
        }
        else
        {
            dest.writeByte((byte)1);
            dest.writeParcelable(result, flags);
        }
    }

    public static final Creator<OptionResult> CREATOR = new Creator<OptionResult>() {
        @Override
        public OptionResult createFromParcel(Parcel in) {
            return new OptionResult(in);
        }

        @Override
        public OptionResult[] newArray(int size) {
            return new OptionResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return option + "," + result;
    }
}
