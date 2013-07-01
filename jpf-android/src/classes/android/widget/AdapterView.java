package android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class AdapterView<T extends Adapter> extends ViewGroup {
  
  
  
  public AdapterView(Context context) {
    super(context);
  }

  /**
   * The listener that receives notifications when an item is clicked.
   */
  OnItemClickListener mOnItemClickListener;

  /**
   * Interface definition for a callback to be invoked when an item in this
   * AdapterView has been clicked.
   */
  public interface OnItemClickListener {

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     * 
     * @param adapterView
     *          The AdapterView where the click happened.
     * @param view
     *          The view within the AdapterView that was clicked (this
     *          will be a view provided by the adapter)
     * @param position
     *          The position of the view in the adapter.
     * @param id
     *          The row id of the item that was clicked.
     */
    void onItemClick(AdapterView<?> adapterView, View view, int position, long id);
  }

  public class OnItemSelectedListener {

  }
  
  /**
   * Register a callback to be invoked when an item in this AdapterView has
   * been clicked.
   * 
   * @param listener
   *          The callback that will be invoked.
   */
  public void setOnItemClickListener(OnItemClickListener listener) {
    mOnItemClickListener = listener;
  }

  /**
   * @return The callback to be invoked with an item in this AdapterView has
   *         been clicked, or null id no callback has been set.
   */
  public final OnItemClickListener getOnItemClickListener() {
    return mOnItemClickListener;
  }

  /**
   * Call the OnItemClickListener, if it is defined.
   * 
   * @param view
   *          The view within the AdapterView that was clicked.
   * @param position
   *          The position of the view in the adapter.
   * @param id
   *          The row id of the item that was clicked.
   * @return True if there was an assigned OnItemClickListener that was
   *         called, false otherwise is returned.
   */
  public boolean performItemClick(View view, int position, long id) {
      mOnItemClickListener.onItemClick(this, view, position, id);
    return false;
  }

  public Adapter getAdapter() {
    return null;
  }
}
