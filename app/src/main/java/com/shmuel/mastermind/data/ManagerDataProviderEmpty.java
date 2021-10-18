package com.shmuel.mastermind.data;

import com.shmuel.mastermind.shared.interfaces.IOptionProviderFactory;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;
import com.shmuel.mastermind.shared.interfaces.OptionProviderCache;

public class ManagerDataProviderEmpty implements ManagerDataProvider
{
    public static ManagerDataProviderEmpty instance=new ManagerDataProviderEmpty();
    @Override
    public boolean isCacheAllOptions()
    {
        return false;
    }

    @Override
    public IOptionProviderFactory getOptionProviderFactory()
    {
        return null;
    }

    @Override
    public OptionProviderCache getOptionProviderCache()
    {
        return null;
    }
}
