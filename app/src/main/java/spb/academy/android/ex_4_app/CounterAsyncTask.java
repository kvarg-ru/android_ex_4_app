package spb.academy.android.ex_4_app;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by User on 23.04.2018.
 */

public class CounterAsyncTask extends AsyncTask<Integer, Integer, Boolean> {

    final static String LOG_TAG = "AsyncTaskClass";

    IAsyncTaskEvents iAsyncTaskEvents;

    public CounterAsyncTask (IAsyncTaskEvents iAsyncTaskEvents) {
        super();
        this.iAsyncTaskEvents = iAsyncTaskEvents;
        Log.d(LOG_TAG, "created task #" + this.hashCode());
    }

    public void attachUiEventsObject(IAsyncTaskEvents iAsyncTaskEvents) {
        this.iAsyncTaskEvents = iAsyncTaskEvents;
    }

    public void deattachUiEventsObject() {
        this.iAsyncTaskEvents = null;
    }

    @Override
    protected Boolean doInBackground(Integer... valList) {

        int counter = 0;
        if ((valList.length != 0) && valList[0] > 0 && valList[0] < 10) {
            counter = valList[0];
        }

        while (counter < 10) {
            counter++;
            publishProgress(counter);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
               // e.printStackTrace();
            }

            if (isCancelled()) {
                break;
            }
        }

        return !isCancelled();

    }

    // UI Events in AsyncTask


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onPostExecute(aBoolean);
        }
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onCancelled(aBoolean);
        }
    }

}
