package com.shmuel.mastermind.data.list;

import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.mastermind.shared.interfaces.IOptionProviderFactory;

class OptionProviderListFactory implements IOptionProviderFactory {

    @Override
    public IOptionProvider create()
    {
        return new OptionProviderList();
    }

    @Override
    public IOptionProvider createForCache(int duplicates)
    {
        return create();
    }
}
