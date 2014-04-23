package za.android.vdm.rssreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebView;

public class RSSWebView extends Activity {

  private WebView webView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String url = null;
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      url = extras.getString("url");
    }

    setContentView(R.layout.webview);
    if (url != null) {
      webView = (WebView) findViewById(R.id.webView1);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.loadUrl(url);
      webView.setOnKeyListener(new OnKeyListener() {
        
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
          System.out.println("$$$$$$$$$$$$$$$$");
          return false;
        }
      });

    }
  }

  @Override
  public void onBackPressed() {
    webView.destroy();
    finish();
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      // Finish Activity
      super.onBackPressed();
      return true;
    }

    return super.dispatchKeyEvent(event);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // TODO Auto-generated method stub
    return super.onKeyDown(keyCode, event);
  }
  
  

}