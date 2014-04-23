package za.android.vdm.rssreader.models;

public class RSSBase {
  private int id = 0;
  private String title = null;
  private String description = null;
  private String link = null;
  private long pubdate = 0;
  
  public RSSBase(){
    
  }
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public long getPubdate() {
    return pubdate;
  }

  public void setPubdate(long pubdate) {
    this.pubdate = pubdate;
  }

  @Override
  public String toString() {
    return "RSSFeed [id=" + id + ", title=" + title + ", link=" + link + ", pubdate=" + pubdate + "]";
  }
}
