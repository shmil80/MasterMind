package com.shmuel.mastermind.shared;

import android.app.Application;

import com.shmuel.mastermind.data.ManagerDataProviderEmpty;
import com.shmuel.mastermind.data.file.ManagerDataProviderFile;
import com.shmuel.mastermind.data.list.ManagerDataProviderList;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;

public class MasterMindApplication extends Application {
       public static ManagerDataProvider dataProvider;

        @Override
        public void onCreate() {
            super.onCreate();
            //final String fileDirs = this.getFilesDir().getAbsolutePath();
//            dataProvider= ManagerDataProviderFile.instance.init(new ManagerDataProviderFile.DirRootGetter() {
//                @Override
//                public String getDirRoot() {
//                    return fileDirs;
//                }
//            });
            dataProvider= ManagerDataProviderEmpty.instance;
        }

}
