package com.shmuel.mastermind.data.file;

import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.mastermind.shared.interfaces.OptionProviderCache;

import java.io.File;

public class OptionProviderFileCache implements OptionProviderCache
{
    @Override
    public boolean isValid(int size)
    {
        String dirRoot = ManagerDataProviderFile.instance.getDirRoot();
        for (int i = 0; i < size; i++)
        {
            String fileName = ManagerDataProviderFile.instance.getFileNameGenerator().getAllOptionsFileName(i);
            if (!new File(dirRoot, fileName).exists())
                return false;
        }
        return true;
    }

    @Override
    public IOptionProvider[] getAllOptions(int providersCount,int optionLength)
    {
        IOptionProvider[] results = new IOptionProvider[providersCount];
        for (int i = 0; i < results.length; i++)
        {
            String nameFile = ManagerDataProviderFile.instance.getFileNameGenerator().getAllOptionsFileName(i);
            results[i] = new OptionProviderFile(nameFile, optionLength);
        }
        return results;
    }

    @Override
    public void removeAllOptions()
    {
        ManagerDataProviderFile.instance.getFileNameGenerator().removeAllOptions();
    }
}
