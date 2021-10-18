package com.shmuel.mastermind.data.list;

import com.shmuel.mastermind.data.file.OptionProviderFileCache;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;
import com.shmuel.mastermind.shared.interfaces.IOptionProviderFactory;
import com.shmuel.mastermind.shared.interfaces.OptionProviderCache;

public class ManagerDataProviderList implements ManagerDataProvider {
    public static ManagerDataProviderList instance=new ManagerDataProviderList();

    private ManagerDataProviderList() {
    }
    private final IOptionProviderFactory optionProviderFactory=new OptionProviderListFactory();

    @Override
    public boolean isCacheAllOptions()
    {
        return false;
    }

    @Override
    public IOptionProviderFactory getOptionProviderFactory() {
        return optionProviderFactory;
    }

    @Override
    public OptionProviderCache getOptionProviderCache()
    {
        return null;
    }
}
