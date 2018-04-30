package spb.academy.android.ex_4_app;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by User on 27.04.2018.
 */

public class CounterAsyncTaskOnHandler extends MyAsyncTaskOnHandlerBase<Integer, Integer, Boolean> {

    final static String LOG_TAG = "CounterAsyncTaskOnHandler";

    IAsyncTaskEvents iAsyncTaskEvents;

    CounterAsyncTaskOnHandler(HandlerThread handlerThread, IAsyncTaskEvents iAsyncTaskEvents) {
        super(handlerThread);
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
    protected Boolean doInBackround(Integer... params) {

        int counter = 0;
        if ((params.length != 0) && params[0] > 0 && params[0] < 10) {
            counter = params[0];
        }

        while (counter < 10) {
            counter++;
            publishProgress(counter);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isCancelled()) {
                break;
            }
        }

        return !isCancelled();
    }


    // UI Events in AsyncTask

    @Override
    protected void onProgressUpdate(Integer... params) {
        super.onProgressUpdate(params);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onProgressUpdate(params[0]);
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
    protected void onPostExecute(Boolean params) {
        super.onPostExecute(params);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onPostExecute(params);
        }
    }

    @Override
    protected void onCancelled(Boolean params) {
        super.onCancelled(params);
        if (iAsyncTaskEvents != null) {
            iAsyncTaskEvents.onCancelled(params);
        }
    }

}
