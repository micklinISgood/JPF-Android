package android.app;

import android.content.ComponentName;
import android.os.IBinder;

public interface IServiceConnection {
    void connected(ComponentName name, IBinder service);
}
