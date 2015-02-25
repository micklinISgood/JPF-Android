package com.example.android.musicplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.view.KeyEvent;

public class TestMusicIntentReceiver extends AndroidTestCase {

  public static class TestContext extends MockContext {
    private List<Intent> mReceivedIntents = new ArrayList<Intent>();

    @Override
    public void startActivity(Intent xiIntent) {
      mReceivedIntents.add(xiIntent);
    }

    @Override
    public ComponentName startService(Intent xiIntent) {
      mReceivedIntents.add(xiIntent);
      return null;
    }

    public List<Intent> getReceivedIntents() {
      return mReceivedIntents;
    }
  }

  private MusicIntentReceiver mReceiver;
  private TestContext mContext;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    mReceiver = new MusicIntentReceiver();
    mContext = new TestContext();
  }

  public void testStartActivity() {
    Intent intent = new Intent(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    KeyEvent keyCode = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);

    intent.putExtra(Intent.EXTRA_KEY_EVENT, keyCode);

    mReceiver.onReceive(mContext, intent);

  }
}