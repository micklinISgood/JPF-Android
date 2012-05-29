package android.os;

public abstract class AsyncTask<Params, Progress, Result> {
	Thread t;

	public AsyncTask() {
	}

	protected abstract Result doInBackground(Params... params);

	public final AsyncTask<Params, Progress, Result> execute(
			final Params... params) {
		t = new Thread(new Runnable() {
			public void run() {
				doInBackground(params);
			}
		});
		t.start();
		return null;
	}

}
