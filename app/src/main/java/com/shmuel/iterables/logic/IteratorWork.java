package com.shmuel.iterables.logic;

import java.io.IOException;

public abstract class IteratorWork<T>
{
    public abstract boolean doWork(T item) throws IOException;
    public void beforeWork() throws IOException
    {}
    public void afterWorkFinally() throws IOException
    {}
}
