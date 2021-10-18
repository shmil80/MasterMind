package com.shmuel.mastermind.ui.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.shmuel.mastermind.shared.beans.Configuration;

public class SettingsData implements Parcelable
{
    private Configuration configuration;
    private boolean sound;

    public SettingsData(Configuration configuration, boolean sound)
    {
        this.configuration = configuration;
        this.sound = sound;
    }

    protected SettingsData(Parcel in)
    {
        configuration = in.readParcelable(Configuration.class.getClassLoader());
        sound = in.readByte() != 0;
    }

    public static final Creator<SettingsData> CREATOR = new Creator<SettingsData>()
    {
        @Override
        public SettingsData createFromParcel(Parcel in)
        {
            return new SettingsData(in);
        }

        @Override
        public SettingsData[] newArray(int size)
        {
            return new SettingsData[size];
        }
    };

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public boolean isSound()
    {
        return sound;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(configuration, flags);
        dest.writeByte((byte) (sound ? 1 : 0));
    }
}
