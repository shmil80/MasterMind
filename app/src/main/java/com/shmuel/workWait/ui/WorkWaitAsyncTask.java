package com.shmuel.workWait.ui;

import com.shmuel.workWait.logic.WorkWaiter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class WorkWaitAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    private final ProgressDialog progressDialog;
    protected boolean canceled;

    public WorkWaitAsyncTask(ProgressDialog progressDialog, final WorkWaiter workWaiter)
    {
        this.progressDialog=progressDialog;
        this.progressDialog.setCancelable(true);
        this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if (workWaiter != null)
                {
                    canceled = true;
                    workWaiter.interrupt();
                }
            }
        });
    }

    @Override
    protected void onPreExecute()
    {
        if (progressDialog != null)
            progressDialog.show();
    }


    @Override
    protected void onPostExecute(Result result)
    {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
