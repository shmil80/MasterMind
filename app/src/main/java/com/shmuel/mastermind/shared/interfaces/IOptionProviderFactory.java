package com.shmuel.mastermind.shared.interfaces;

public interface IOptionProviderFactory
{
    IOptionProvider create();
    IOptionProvider createForCache(int duplicates);
}
