package za.android.vdm.rssreader.models;

public class RSSItem extends RSSBase {
  private int rssfeed_id = 0;

  public int getRssfeed_id() {
    return rssfeed_id;
  }

  public void setRssfeed_id(int rssfeed_id) {
    this.rssfeed_id = rssfeed_id;
  }

  @Override
  public String toString() {
    return "RSSItem [ id=" + getId() + ", title=" + getTitle() + ", rssfeed_id="
        + rssfeed_id + ",  pubdate=" + getPubdate() + "]";
  }
}
