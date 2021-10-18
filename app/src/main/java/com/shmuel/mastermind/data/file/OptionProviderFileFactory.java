package com.shmuel.mastermind.data.file;

import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.mastermind.shared.interfaces.IOptionProviderFactory;

import java.io.File;

class OptionProviderFileFactory implements IOptionProviderFactory
{
    @Override
    public IOptionProvider create()
    {
        String nameFile =ManagerDataProviderFile.instance.getFileNameGenerator().generateFileName();
        return new OptionProviderFile(nameFile,false);
    }
    @Override
    public IOptionProvider createForCache(int duplicates)
    {
        String nameFile = ManagerDataProviderFile.instance.getFileNameGenerator().getAllOptionsFileName(duplicates);
        return new OptionProviderFile(nameFile,true);
    }

 }
