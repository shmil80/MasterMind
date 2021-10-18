package com.shmuel.mastermind.shared.interfaces;

public interface OptionProviderCache
{
    boolean isValid(int providersCount);
    IOptionProvider[] getAllOptions(int providersCount,int optionLength);
    void removeAllOptions();

}
