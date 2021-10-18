package com.shmuel.mastermind;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.MasterMindApplication;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;

import java.io.IOException;

class InitCacheAsyncTask extends AsyncTask<Void, Void, Void>
{
    public static interface Callback{
        void afterInit();
    }
    private final ManagerDataProvider dataProvider = MasterMindApplication.dataProvider;
    private Context context;
    private final ProgressDialog progressDialog;
    private ManagerGame managerGame;
    private Callback callback;
    private boolean removeBeforeLoad;

    public InitCacheAsyncTask(Context context, boolean removeBeforeLoad, ManagerGame managerGame, Callback callback)
    {
        this.context = context;
        this.removeBeforeLoad = removeBeforeLoad;
        this.progressDialog = new ProgressDialog(context);
        this.managerGame = managerGame;
        this.callback = callback;
        this.progressDialog.setTitle(context.getString(R.string.initialising));
        this.progressDialog.setMessage(context.getString(R.string.pleaseWait));
        this.progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute()
    {
        if (progressDialog != null)
            progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        if(removeBeforeLoad)
        {

            dataProvider.getOptionProviderCache().removeAllOptions();
        }

        try
        {
            managerGame.optionFinderInList.createOptionsCacheIfNeeded();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result)
    {
        if (progressDialog != null)
            progressDialog.dismiss();
        callback.afterInit();

    }
}
