package com.shmuel.iterables.logic;

import com.shmuel.helpers.beans.Wrapper;
import com.shmuel.iterables.interfaces.IteratorClosable;
import com.shmuel.mastermind.data.file.ManagerDataProviderFile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

public abstract class IteratorClosableReader<T> implements IteratorClosable<T> {

    protected final DataInputStream reader;
    private boolean endOfIteration=false;
    private T next;

    public IteratorClosableReader(File file)
    {
        try
        {
            this.reader = new DataInputStream( new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        init();
    }
    public IteratorClosableReader(DataInputStream reader)
    {
        this.reader = reader;
        init();
    }
    private void init()
    {
        endOfIteration = false;
        next = tryReadNext();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    protected abstract boolean tryReadNext(Wrapper<T> nextRef) throws IOException;

    private T tryReadNext()
    {
        try
        {
            Wrapper<T> nextRef=new Wrapper<>();
            endOfIteration=tryReadNext(nextRef);
            return nextRef.getItem();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return endOfIteration;
    }

    @Override
    public T next() {
        if(!hasNext())
            throw new NoSuchElementException();
        T current=next;
        next = tryReadNext();
        return current;
    }
}
