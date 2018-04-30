package spb.academy.android.ex_4_app;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by User on 27.04.2018.
 */

public abstract class MyAsyncTaskOnHandlerBase <InputType,PublishType,ResultType> {

    final Handler backgroundHandler;
    final Handler UiHandler;
    boolean isCancelledFlag;
    enum States {PENDING, RUNNING, FINISHED};
    States taskState;

    public MyAsyncTaskOnHandlerBase(HandlerThread handlerThread) {
        this.backgroundHandler =  new Handler(handlerThread.getLooper());
        this.isCancelledFlag = false;
        taskState = States.PENDING;
        UiHandler = new Handler(Looper.getMainLooper());
    }

    protected abstract ResultType doInBackround(InputType... params);

    protected void publishProgress(final PublishType... params) {
        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(params);
            }
        });
    }

    protected void onProgressUpdate(PublishType... params) {
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(ResultType params) {

    }

    protected void onCancelled(ResultType params) {

    }

    protected boolean isCancelled() {
        return isCancelledFlag;
    }

    protected void execute(final InputType... params) {
        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                taskState = States.RUNNING;
                onPreExecute();
                backgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final ResultType results = doInBackround(params);
                        if (isCancelled()) {
                            UiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onCancelled(results);
                                    taskState = States.FINISHED;
                                }
                            });
                        } else {
                            UiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onPostExecute(results);
                                    taskState = States.FINISHED;
                                }
                            });
                        }
                    }
                });


            }
        });

    }

    protected States getStatus() {
        return taskState;
    }

    protected void cancel() {
        isCancelledFlag = true;
    }

}
