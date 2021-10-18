package com.shmuel.iterables.logic;

import com.shmuel.iterables.interfaces.IterableClosable;
import com.shmuel.iterables.interfaces.IteratorClosable;
import com.shmuel.mastermind.shared.beans.Option;

import java.io.IOException;
import java.util.Iterator;

public class IteratorClosableImpl implements IteratorClosable<Option> {
    private final Iterator<Option> iterator;

    public IteratorClosableImpl(Iterator<Option> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void close() {}

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Option next() {
        return iterator.next();
    }

    public static <T> void Iterate(IterableClosable<T> iterable, IteratorWork<T> worker) throws IOException
    {
        IteratorClosable<T> iterator = null;
        try
        {
            worker.beforeWork();
            iterator = iterable.iterator();
            while (iterator.hasNext())
            {
                T item = iterator.next();
                if(worker.doWork(item))
                    break;
            }
        }
        finally
        {
            if (iterator != null)
                iterator.close();
            worker.afterWorkFinally();
        }
    }
}
