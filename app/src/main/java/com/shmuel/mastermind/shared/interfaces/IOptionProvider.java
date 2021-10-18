package com.shmuel.mastermind.shared.interfaces;

import android.os.Parcelable;


import com.shmuel.iterables.interfaces.IterableClosable;
import com.shmuel.mastermind.shared.beans.Option;

import java.io.IOException;

public interface IOptionProvider extends IterableClosable<Option>,Parcelable
{
    void add(Option o)throws  IOException;;

    int size();

    Option get(int index) throws IOException;

    void startWrite() throws  IOException;

    void endWrite()throws  IOException;;

    boolean clear()throws  IOException;;
}
