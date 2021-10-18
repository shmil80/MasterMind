package com.shmuel.mastermind.data.list;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.shmuel.iterables.logic.IteratorClosableImpl;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.iterables.interfaces.IteratorClosable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class OptionProviderList implements IOptionProvider {

    private static Random rand = new Random(System.currentTimeMillis());
    private List<Option> inner;

    public OptionProviderList() {
        inner=new ArrayList<>();
    }

    @Override
    public void add(Option o) {
        inner.add(o);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Option get(int index) {
                return inner.get(index);
    }

    @Override
    public void startWrite() {

    }

    @Override
    public void endWrite() {

    }

    @Override
    public boolean clear() {
        inner.clear();
        return true;
    }

    @NonNull
    @Override
    public IteratorClosable<Option> iterator() {
        return new IteratorClosableImpl(inner.iterator());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(inner);
    }

    protected OptionProviderList(Parcel in) {
        inner = in.createTypedArrayList(Option.CREATOR);
    }

    public static final Creator<OptionProviderList> CREATOR = new Creator<OptionProviderList>() {
        @Override
        public OptionProviderList createFromParcel(Parcel in) {
            return new OptionProviderList(in);
        }

        @Override
        public OptionProviderList[] newArray(int size) {
            return new OptionProviderList[size];
        }
    };

}
