package com.shmuel.mastermind.shared.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Configuration implements Parcelable
{
    private byte optionLength;
    private byte optionPosibilities;
    private boolean allowDuplicate;
    private boolean allowEmpty;

    public Configuration(boolean allowDuplicate, byte optionLength, byte optionPosibilities,  boolean allowEmpty)
    {
        this.optionLength = optionLength;
        this.optionPosibilities = optionPosibilities;
        this.allowDuplicate = allowDuplicate;
        this.allowEmpty = allowEmpty;
    }

    public byte getOptionLength()
    {
        return optionLength;
    }

    public void setOptionLength(byte optionLength)
    {
        this.optionLength = optionLength;
    }

    public byte getOptionPosibilities()
    {
        return this.optionPosibilities;
    }

    public void setOptionPosibilities(byte optionPosibilities)
    {
        this.optionPosibilities = optionPosibilities;
    }

    public boolean isAllowDuplicate()
    {
        return this.allowDuplicate;
    }

    public void setAllowDuplicate(boolean allowDuplicate)
    {
        this.allowDuplicate = allowDuplicate;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return optionLength == that.optionLength &&
                optionPosibilities == that.optionPosibilities &&
                allowDuplicate == that.allowDuplicate &&
                allowEmpty == that.allowEmpty;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new int[]{optionLength, optionPosibilities, allowDuplicate ? 1 : 0, allowEmpty ? 1 : 0});
    }

    protected Configuration(Parcel in)
    {
        optionLength = in.readByte();
        optionPosibilities = in.readByte();
        allowDuplicate = in.readByte() == 1;
        allowEmpty = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeByte(optionLength);
        dest.writeByte(optionPosibilities);
        dest.writeByte((byte) (allowDuplicate ? 1 : 0));
        dest.writeByte((byte) (allowEmpty ? 1 : 0));
    }

    public static final Creator<Configuration> CREATOR = new Creator<Configuration>()
    {
        @Override
        public Configuration createFromParcel(Parcel in)
        {
            return new Configuration(in);
        }

        @Override
        public Configuration[] newArray(int size)
        {
            return new Configuration[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    public boolean isAllowEmpty()
    {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty)
    {
        this.allowEmpty = allowEmpty;
    }
}
