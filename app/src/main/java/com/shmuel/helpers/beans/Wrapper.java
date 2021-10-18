package com.shmuel.helpers.beans;

public class Wrapper<T>
{
    public Wrapper() {
    }

    public Wrapper(T item) {
        this.item = item;
    }

    private T item;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
