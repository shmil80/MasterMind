package com.shmuel.mastermind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.shmuel.mastermind.shared.MasterMindApplication;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;
import com.shmuel.mastermind.ui.SoundTickPlayer;
import com.shmuel.mastermind.ui.SoundWinPlayer;
import com.shmuel.mastermind.ui.beans.SettingsData;
import com.shmuel.mastermind.ui.views.CircleColorButton;
import com.shmuel.mastermind.ui.views.LinearLineOption;
import com.shmuel.mastermind.ui.views.LinearPallete;
import com.shmuel.workWait.logic.WorkWaiter;
import com.shmuel.mastermind.shared.beans.Configuration;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;
import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.logic.PlayerChooser;
import com.shmuel.workWait.ui.WorkWaitAsyncTask;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private final ManagerDataProvider dataProvider = MasterMindApplication.dataProvider;
    private ManagerGame managerGame;
    private PlayerChooser chooser;
    private ArrayList<OptionResult> optionResults;
    private OptionResult currentOption;
    private ListView list_view_guesses;
    private OptionAdapter optionsAdapter;
    private IPlayerGuesser guesser;
    private LinearPallete pallete;
    private final WorkWaiter workWaiter = new WorkWaiter();
    private GuesserThread guesserThread;
    private boolean inGame;
    private boolean canGuess=true;
    private Button submitButton;
    private MenuItem guessButton;
    private SoundWinPlayer soundWinPlayer;
    private SettingsData settings;
    private GuessColorAnimation guessColorAnimation;
    private ChooseColorAnimation chooseAnimation;

    //private Button guessButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings= SettingsActivity.initPreferences(this);;
        Configuration config = settings.getConfiguration();

        this.managerGame = new ManagerGame(config);
        if (savedInstanceState == null)
        {
            optionResults = new ArrayList<>();
            initConfig(false);
        } else
        {
            this.optionResults = savedInstanceState.getParcelableArrayList("optionResults");
        }
        this.list_view_guesses = findViewById(R.id.list_view_guesses);
        this.submitButton = findViewById(R.id.submitButton);
        //this.guessButton = findViewById(R.id.guessButton);
        CircleColorButton buttonAnim= findViewById(R.id.buttonAnim);
        this.optionsAdapter = new OptionAdapter(this, this.optionResults);
        this.list_view_guesses.setAdapter(optionsAdapter);
        this.list_view_guesses.setStackFromBottom(true);
        this.chooseAnimation = new ChooseColorAnimation(buttonAnim);
        this.chooseAnimation.setSound(settings.isSound());
        this.pallete = findViewById(R.id.pallete);
        this.pallete.setOptionAdapter(optionsAdapter);
        this.pallete.setLength(config.getOptionPosibilities());
        this.pallete.init(chooseAnimation);
        this.soundWinPlayer=new SoundWinPlayer(this);
        pallete.invalidate();

    }

    private void initConfig(boolean removeBeforeLoad)
    {
        if(this.guesserThread!=null)
            this.guesserThread.close();
        if (dataProvider.isCacheAllOptions())
        {
            new InitCacheAsyncTask(this, removeBeforeLoad, managerGame, new InitCacheAsyncTask.Callback()
            {
                @Override
                public void afterInit() {start();}
            }).execute();
        } else
        {
            start();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        guessButton = menu.findItem(R.id.menu_guess);
        guessButton.setEnabled(canGuess);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean start()
    {
        try
        {
            this.chooser = this.managerGame.createChooser();
            this.chooser.startGame();
            this.guesser = this.managerGame.createGuesser();
            this.guesserThread=new GuesserThread(this.workWaiter, this.guesser, true);
            optionResults.clear();
            currentOption = new OptionResult(managerGame.optionFinder.emptyOption());
            optionResults.add(currentOption);
            inGame=true;
        }
        catch (IOException e)
        {
            handleError(e);
            return false;
        }
        LinearLineOption.selectedGlobal = (byte) 0;
        if (optionsAdapter != null)
        {
            list_view_guesses.setBackgroundColor(getResources().getColor(R.color.colorBackGround));
            list_view_guesses.invalidate();
            optionsAdapter.notifyDataSetInvalidated();
            submitButton.setText(R.string.ok);
            enableGuess(true);

        }
        return true;
    }

    private void handleError(Exception e)
    {
        e.printStackTrace();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        if(settings==null)
            settings= SettingsActivity.initPreferences(this);;
        this.chooser = savedInstanceState.getParcelable("chooser");
        this.chooser.setManagerGame(this.managerGame);
        this.guesser = savedInstanceState.getParcelable("guesser");
        this.guesser.setManagerGame(this.managerGame);
        if(this.guesserThread!=null)
            this.guesserThread.close();
        this.guesserThread=new GuesserThread(this.workWaiter, this.guesser, false);
        this.optionResults = savedInstanceState.getParcelableArrayList("optionResults");
        this.currentOption = savedInstanceState.getParcelable("currentOption");
        this.inGame=savedInstanceState.getBoolean("inGame");
        if(!inGame)
        {
            list_view_guesses.setBackgroundColor(getResources().getColor(R.color.colorBackGroundWin));
            submitButton.setText(R.string.new_game);
        }
        this.canGuess=savedInstanceState.getBoolean("canGuess");
        if(this.guessButton!=null)
            guessButton.setEnabled(canGuess);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("guesser", this.guesser);
        outState.putParcelable("chooser", this.chooser);
        outState.putParcelableArrayList("optionResults", this.optionResults);
        outState.putParcelable("currentOption", this.currentOption);
        outState.putBoolean("inGame", this.inGame);
        outState.putBoolean("canGuess", canGuess);
        super.onSaveInstanceState(outState);
    }

    public void submit(View view)
    {
        if (!this.currentOption.getOption().isEmpty())
        {
            if(!inGame)
            {
                start();
            }
            else
            {
                submitMove();
            }
        }
    }

    private void submitMove()
    {
        final Result result = chooser.answerGuess(this.currentOption.getOption());
        currentOption.setResult(result);
        if (result.rightAnswer)
        {
            win();
        }
        else
        {
            guesserThread.newResult(currentOption);
            LinearLineOption.selectedGlobal = 0;
            currentOption = new OptionResult(managerGame.optionFinder.emptyOption());
//                    this.optionsAdapter.add(currentOption);
//                    this.list_view_guesses.setSelection(optionsAdapter.getCount() - 1);
            this.optionsAdapter.insert(currentOption,0);
            this.optionsAdapter.setNeedAnimtaion(true);
            this.list_view_guesses.setSelection(0);
            enableGuess(true);
        }
        this.optionsAdapter.notifyDataSetInvalidated();
    }

    private void win()
    {
        inGame=false;
        this.guesserThread.close();
        LinearLineOption.selectedGlobal = null;
        list_view_guesses.setBackgroundColor(getResources().getColor(R.color.colorBackGroundWin));
        list_view_guesses.invalidate();
        submitButton.setText(R.string.new_game);
        enableGuess(false);
        if(settings.isSound())
        {
            this.soundWinPlayer.playSound();
        }
    }

    public void restart(MenuItem view)
    {
        this.guesserThread.close();
        start();
    }
    public void guess(MenuItem view)
    {
        this.guess();
    }

    public void goToSettings(MenuItem item)
    {
        SettingsActivity.start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Consts.SETTING_REQUEST_CODE)
        {
            settings = SettingsActivity.initPreferences(this);
            this.chooseAnimation.setSound(settings.isSound());
            Configuration config = settings.getConfiguration();
            if (this.managerGame != null && !config.equals(this.managerGame.configuration))
            {
                this.managerGame = new ManagerGame(config);
                initConfig(true);
                this.pallete.setLength(config.getOptionPosibilities());
                pallete.invalidate();
            }
            return;
        }
    }

//    ExecutorService executor = Executors.newCachedThreadPool();

    public void guess(View view)
    {
        guess();
    }
    public void guess()
    {
        if (guesser.isCancelled())
            return;
        if (workWaiter.isReady())
        {
            tryGuess(guesserThread.getNextGuess());
        } else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.calculating));
            progressDialog.setMessage(getString(R.string.pleaseWait));
            new WorkWaitAsyncTask<Void, Void, Option>(progressDialog, this.workWaiter)
            {
                @Override
                protected Option doInBackground(Void... params)
                {
                    return guesserThread.getNextGuess();
                }

                @Override
                protected void onPostExecute(Option result)
                {
                    super.onPostExecute(result);
                    if (!canceled)
                        tryGuess(result);
                }
            }.execute();
        }
    }

    private void tryGuess(Option guess)
    {
        if (!guesser.isCancelled())
            if (guess == null)
                Toast.makeText(MainActivity.this, R.string.error_in_guess, Toast.LENGTH_LONG).show();
            else
                guess(guess);
    }
    private void enableGuess(boolean value)
    {
        canGuess=value;
        if(guessButton!=null)
        {
            guessButton.setEnabled(value);
        }
    }
    private void guess(final Option guess)
    {
        enableGuess(false);
        this.pallete.animate(guess, new Runnable() {
            @Override
            public void run()
            {
                currentOption.setOption(guess);
                MainActivity.this.optionsAdapter.getCurrentLine().invalidate();
                MainActivity.this.optionsAdapter.notifyDataSetInvalidated();
                submitMove();

            }
        });
    }
}
