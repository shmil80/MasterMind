package com.shmuel.iterables.interfaces;

import java.io.Closeable;
import java.util.Iterator;

public interface IteratorClosable<T> extends Iterator<T>,Closeable {
}
