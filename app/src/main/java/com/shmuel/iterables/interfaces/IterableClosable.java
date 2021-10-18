package com.shmuel.iterables.interfaces;

import java.io.IOException;

public interface IterableClosable<T> extends Iterable<T> {
    IteratorClosable<T> iterator();
}
