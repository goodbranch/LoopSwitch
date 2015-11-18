package loop.boss.com.loopswitch;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 订阅页面轮播图适配器
 *
 * @author aoaoboss
 * @since 6.4.3  2015/11/16
 */
public abstract class AutoLoopSwitchBaseAdapter extends PagerAdapter {


  private SparseArray<View> mCachePagerViews;

  public AutoLoopSwitchBaseAdapter() {
    this.mCachePagerViews = new SparseArray<View>();
  }

  /**
   * 实际轮播图个数
   */
  public abstract int getDataCount();

  public abstract View getView(int position);

  public abstract Object getItem(int position);

  @Override
  public final int getCount() {
    if (getDataCount() > 1) {
      //如果轮播个数大于1个，那么需要轮播，则会在首尾加上一个，
      return getDataCount() + 2;
    } else {
      return getDataCount();
    }
  }

  /**
   * 得到实际页面index
   */
  public final int getActualIndex(int index) {
    int position = index;
    if (getDataCount() > 1) {
      if (index == 0) {
        position = getDataCount() - 1;
      } else if (index == getCount() - 1) {
        position = 0;
      } else {
        position = index - 1;
      }
    }
    return position;
  }

  @Override
  public final Object instantiateItem(ViewGroup container, int position) {
    View v = null;
    int posi = getActualIndex(position);
    v = mCachePagerViews.get(posi);
    if (v == null) {
      v = getView(posi);
      mCachePagerViews.put(posi, v);
    } else {
      ViewGroup parent = (ViewGroup) v.getParent();
      if (parent != null) {
        parent.removeView(v);
      }
    }

    container.addView(v);

    return v;
  }

  @Override
  public final void destroyItem(ViewGroup container, int position, Object object) {
//    super.destroyItem(container, position, object);
  }


  @Override
  public final boolean isViewFromObject(View view, Object o) {
    return view == o;
  }


  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    if (mCachePagerViews != null) {
      Log.e("ryze", "AutoLoopSwitchBaseAdapter notifyDataSetChanged");
      mCachePagerViews.clear();
    }
  }
}
