package spb.academy.android.ex_4_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 24.04.2018.
 */

public class AsyncTaskActivity extends AppCompatActivity implements IAsyncTaskEvents {

    final static String LOG_TAG = "AsyncTaskActivity";
    final static String RESULT_TV_KEY = "result_tv_text";

    TextView resultTextView;
    CounterAsyncTask counterAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        resultTextView = findViewById(R.id.textView_result);

        if (savedInstanceState != null) {
            String restored_text = savedInstanceState.getString(RESULT_TV_KEY);

            resultTextView.setText(restored_text);

            try {
                int number = Integer.parseInt(restored_text);
                counterAsyncTask = new CounterAsyncTask(AsyncTaskActivity.this);
                counterAsyncTask.execute(number);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }

        }

        final Button createButton = findViewById(R.id.button_create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterAsyncTask != null) {
                    if (counterAsyncTask.getStatus() == AsyncTask.Status.FINISHED || counterAsyncTask.isCancelled()) {
                        counterAsyncTask.deattachUiEventsObject();
                        counterAsyncTask = new CounterAsyncTask(AsyncTaskActivity.this);
                        Toast.makeText(AsyncTaskActivity.this, "Task is created again", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AsyncTaskActivity.this, "Task already have been created", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    counterAsyncTask = new CounterAsyncTask(AsyncTaskActivity.this);
                    Toast.makeText(AsyncTaskActivity.this, "Task is created", Toast.LENGTH_SHORT).show();
                    resultTextView.setText("You may start task");
                    //Log.d(LOG_TAG, "created task #" + counterAsyncTask.hashCode());
                }
            }
        });


        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterAsyncTask == null) {
                    Toast.makeText(AsyncTaskActivity.this, "Create a task at first", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (counterAsyncTask.getStatus() == AsyncTask.Status.RUNNING && !counterAsyncTask.isCancelled()) {
                        Toast.makeText(AsyncTaskActivity.this, "Task already have been run", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        counterAsyncTask.execute();
                    }
                }
            }
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counterAsyncTask == null) {
                    Toast.makeText(AsyncTaskActivity.this, "Task haven't created yet", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (counterAsyncTask.isCancelled()) {
                        Toast.makeText(AsyncTaskActivity.this, "Task already have been canceled", Toast.LENGTH_SHORT).show();
                    }
                    else if (counterAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                        counterAsyncTask.cancel(true);
                        Toast.makeText(AsyncTaskActivity.this, "Task is canceled", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AsyncTaskActivity.this, "Task haven't been canceled, it's not running", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

        if (counterAsyncTask != null) {
            counterAsyncTask.cancel(true);
        }

        outState.putString(RESULT_TV_KEY, resultTextView.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "onDestroy");

        if (counterAsyncTask != null) {
            counterAsyncTask.cancel(true);
            counterAsyncTask.deattachUiEventsObject();
            counterAsyncTask = null;
        }
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

        counterAsyncTask.deattachUiEventsObject();
        counterAsyncTask = null;
    }

    @Override
    public void onProgressUpdate(Integer count) {
        resultTextView.setText(count.toString());
    }

    @Override
    public void onCancelled(Boolean result) {
        resultTextView.setText("Cancelled!");

        if (counterAsyncTask != null) {
            counterAsyncTask.deattachUiEventsObject();
            counterAsyncTask = null;
        }

    }

}
