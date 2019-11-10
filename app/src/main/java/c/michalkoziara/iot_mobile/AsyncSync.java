package c.michalkoziara.iot_mobile;

import android.os.AsyncTask;

public class AsyncSync extends AsyncTask<String, String, String> {

    public interface AsyncResponse {
        String createRequest(String[] params);

        void processFinish(String output);
    }

    private AsyncResponse delegateAsync;

    AsyncSync(AsyncResponse delegate) {
        this.delegateAsync = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        return delegateAsync.createRequest(params);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        delegateAsync.processFinish(result);
    }

}