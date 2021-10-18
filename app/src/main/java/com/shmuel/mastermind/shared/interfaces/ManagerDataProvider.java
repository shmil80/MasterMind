package com.shmuel.mastermind.shared.interfaces;

public interface ManagerDataProvider {
     boolean isCacheAllOptions();
     IOptionProviderFactory getOptionProviderFactory();
     OptionProviderCache getOptionProviderCache();
}
