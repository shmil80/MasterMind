package com.shmuel.mastermind.shared.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Colors implements Parcelable
{
    private byte[] colors;

    public byte[] getColors()
    {
        return colors;
    }

    public void setColors(byte[] colors)
    {
        this.colors = colors;
    }

    public Colors(byte[] colors)
    {
        this.colors = colors;
    }


    public String toString()
    {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < this.colors.length; i++) {
            if (i != 0)
                result.append(",");
            result.append(this.colors[i] + 1);
        }
        return result + "]";
    }

    protected Colors(Parcel in) {
        if(in.readByte()==1)
        {
            this.colors=in.createByteArray();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        if(this.colors==null)
        {
            dest.writeByte((byte) 0);
        }
        else
        {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.colors);
        }
    }

    public static final Creator<Colors> CREATOR = new Creator<Colors>() {
        @Override
        public Colors createFromParcel(Parcel in) {
            return new Colors(in);
        }

        @Override
        public Colors[] newArray(int size) {
            return new Colors[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isEmpty() {
        for (byte color : colors) {
           if(color>=0)
               return false;
        }
        return true;
    }

    public boolean isFull() {
        for (byte color : colors) {
            if(color<0)
                return false;
        }
        return true;
    }

    public boolean hasDuplicates()
    {
        Set<Byte> hashset = new HashSet<>();
        for (byte i : colors)
            hashset.add(i);

        return hashset.size() != colors.length;

    }

    @Override
    public Colors clone()
    {
        return new Colors(Arrays.copyOf(colors,colors.length));
    }
}
