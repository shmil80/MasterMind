package com.shmuel.mastermind.data.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

class FileNameGenerator
{
    private final Object locker = new Object();
    private boolean initialised;

    FileNameGenerator()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                synchronized (locker)
                {
                    for (File file : new File(ManagerDataProviderFile.instance.getDirRoot()).listFiles())
                    {
                        if (file.getName().startsWith("options"))
                            file.delete();
                    }
                    locker.notify();
                    initialised = true;
                }
            }

        }.start();
    }

    private int num;

    public String generateFileName()
    {
        synchronized (locker)
        {
            if (!initialised)
                try
                {
                    locker.wait();
                }
                catch (InterruptedException ignored)
                {
                }

            return "options" + (++num) + ".bin";
        }
    }

    public String getAllOptionsFileName(int i)
    {
        return "all-options-"+i+".bin";
    }
    public void removeAllOptions()
    {
        for (File file : new File(ManagerDataProviderFile.instance.getDirRoot()).listFiles())
        {
            if (file.getName().startsWith("all-options"))
            {
                file.delete();
            }
        }

    }
}
