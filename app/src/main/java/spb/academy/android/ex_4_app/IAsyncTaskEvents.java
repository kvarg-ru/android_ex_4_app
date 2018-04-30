package spb.academy.android.ex_4_app;

/**
 * Created by User on 23.04.2018.
 */

public interface IAsyncTaskEvents {

    void onPreExecute();

    void onPostExecute(Boolean result);

    void onProgressUpdate(Integer count);

    void onCancelled(Boolean result);

}
