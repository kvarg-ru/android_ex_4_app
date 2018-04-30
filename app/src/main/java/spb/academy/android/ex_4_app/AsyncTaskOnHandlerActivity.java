package spb.academy.android.ex_4_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 27.04.2018.
 */

public class AsyncTaskOnHandlerActivity extends AppCompatActivity implements IAsyncTaskEvents {

    final static String RESULT_TV_KEY = "result_tv_text";
    final static String LOG_TAG = "AsyncTaskActOnHandler";

    private TextView resultTextView;
    private CounterAsyncTaskOnHandler counterAsyncTaskOnHandler;
    private HandlerThread backgroundThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        resultTextView = findViewById(R.id.textView_result);

        backgroundThread = new HandlerThread("BackgroundThread");
        backgroundThread.start();

        if (savedInstanceState != null) {
            String restored_text = savedInstanceState.getString(RESULT_TV_KEY);

            resultTextView.setText(restored_text);

            try {
                int number = Integer.parseInt(restored_text);
                counterAsyncTaskOnHandler = new CounterAsyncTaskOnHandler(backgroundThread, AsyncTaskOnHandlerActivity.this);
                counterAsyncTaskOnHandler.execute(number);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }

        }

        final Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counterAsyncTaskOnHandler != null) {
                    if (counterAsyncTaskOnHandler.getStatus() == MyAsyncTaskOnHandlerBase.States.FINISHED || counterAsyncTaskOnHandler.isCancelled()) {
                        counterAsyncTaskOnHandler.deattachUiEventsObject();
                        counterAsyncTaskOnHandler = new CounterAsyncTaskOnHandler(backgroundThread, AsyncTaskOnHandlerActivity.this);
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task is created again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task already have been created", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    counterAsyncTaskOnHandler = new CounterAsyncTaskOnHandler(backgroundThread, AsyncTaskOnHandlerActivity.this);
                    Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task is created", Toast.LENGTH_SHORT).show();
                    resultTextView.setText("You may start task");
                    //Log.d(LOG_TAG, "created task #" + counterAsyncTaskOnHandler.hashCode());
                }

            }
        });


        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterAsyncTaskOnHandler == null) {
                    Toast.makeText(AsyncTaskOnHandlerActivity.this, "Create a task at first", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (counterAsyncTaskOnHandler.getStatus() == MyAsyncTaskOnHandlerBase.States.RUNNING && !counterAsyncTaskOnHandler.isCancelled()) {
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task already have been run", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        counterAsyncTaskOnHandler.execute();
                    }
                }
            }
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterAsyncTaskOnHandler == null) {
                    Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task haven't created yet", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (counterAsyncTaskOnHandler.isCancelled()) {
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task already have been canceled", Toast.LENGTH_SHORT).show();
                    }
                    else if (counterAsyncTaskOnHandler.getStatus() ==  MyAsyncTaskOnHandlerBase.States.RUNNING) {
                        counterAsyncTaskOnHandler.cancel();
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task is canceled", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AsyncTaskOnHandlerActivity.this, "Task haven't been canceled, it's not running", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

        if (counterAsyncTaskOnHandler != null) {
            counterAsyncTaskOnHandler.cancel();
        }

        outState.putString(RESULT_TV_KEY, resultTextView.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "onDestroy");

        if (counterAsyncTaskOnHandler != null) {
            counterAsyncTaskOnHandler.cancel();
            counterAsyncTaskOnHandler.deattachUiEventsObject();
            counterAsyncTaskOnHandler = null;
        }

        backgroundThread.quit();
        backgroundThread = null;
    }

    // Interface methods for UI Events in AsyncTask
    @Override
    public void onPreExecute() {
        resultTextView.setText("PreExecute");
    }

    @Override
    public void onPostExecute(Boolean result) {
        if (result) resultTextView.setText("Done!");
        else resultTextView.setText("Haven't done!");

        counterAsyncTaskOnHandler.deattachUiEventsObject();
        counterAsyncTaskOnHandler = null;
    }

    @Override
    public void onProgressUpdate(Integer count) {
        resultTextView.setText(count.toString());
    }

    @Override
    public void onCancelled(Boolean result) {
        resultTextView.setText("Cancelled!");

        if (counterAsyncTaskOnHandler != null) {
            counterAsyncTaskOnHandler.deattachUiEventsObject();
            counterAsyncTaskOnHandler = null;
        }

    }

}
