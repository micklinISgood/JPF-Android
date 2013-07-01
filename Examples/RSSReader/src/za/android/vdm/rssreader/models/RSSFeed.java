package za.android.vdm.rssreader.models;

public class RSSFeed extends RSSBase {
  /** newest created time of a post inserted into database */
  private long timeInserted = 0;

  public long getTimeInserted() {
    return timeInserted;
  }

  public void setTimeInserted(long timeInserted) {
    this.timeInserted = timeInserted;
  }

  @Override
  public String toString() {
    return "RSSFeed [id=" + getId() + ", title=" + getTitle() + ", link=" + getLink() + ", pubdate="
        + getPubdate() + "]";
  }

}
