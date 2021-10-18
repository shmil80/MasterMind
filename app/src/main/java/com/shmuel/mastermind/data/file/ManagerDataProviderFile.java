package com.shmuel.mastermind.data.file;

import com.shmuel.mastermind.shared.interfaces.IOptionProviderFactory;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;
import com.shmuel.mastermind.shared.interfaces.OptionProviderCache;

public class ManagerDataProviderFile implements ManagerDataProvider {

    public interface DirRootGetter {
        String getDirRoot();
    }

    public static final ManagerDataProviderFile instance = new ManagerDataProviderFile();

    private ManagerDataProviderFile() {
    }

    private FileNameGenerator fileNameGenerator;

    FileNameGenerator getFileNameGenerator() {
        if(fileNameGenerator==null)
        {
            fileNameGenerator = new FileNameGenerator();
        }
        return fileNameGenerator;
    }

    private final IOptionProviderFactory optionProviderFactory = new OptionProviderFileFactory();
    private final OptionProviderCache optionProviderCache = new OptionProviderFileCache();

    @Override
    public boolean isCacheAllOptions()
    {
        return true;
    }

    @Override
    public IOptionProviderFactory getOptionProviderFactory() {
        return optionProviderFactory;
    }

    @Override
    public OptionProviderCache getOptionProviderCache()
    {
        return optionProviderCache;
    }

    private DirRootGetter dirRootGetter;

    public ManagerDataProviderFile init(DirRootGetter dirRootGetter) {
        this.dirRootGetter = dirRootGetter;
        return this;
    }

    String getDirRoot() {
        return dirRootGetter.getDirRoot();
    }
}
