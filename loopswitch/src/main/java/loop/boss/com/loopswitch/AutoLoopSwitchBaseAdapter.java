package loop.boss.com.loopswitch;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 订阅页面轮播图适配器
 *
 * @author ryze
 * @since 1.0  2016/07/17
 */
public abstract class AutoLoopSwitchBaseAdapter extends PagerAdapter {

  public AutoLoopSwitchBaseAdapter() {
  }

  public static final int VIEWPAGER_RADIX = 100;

  /**
   * 实际轮播图个数
   */
  public abstract int getDataCount();

  public abstract View getView(int position);

  public abstract Object getItem(int position);

  /**
   * 如果数据为空的时候,这里最好不为空
   */
  public abstract View getEmptyView();

  /**
   * 当数据改变时，ViewPager预加载的View需要重新设置数据
   */
  public abstract void updateView(View view, int position);

  @Override
  public final int getCount() {
    if (getDataCount() > 1) {
      //如果轮播个数大于1个，那么需要轮播，增加基数，同时在首尾加上一个，
      return getDataCount() * VIEWPAGER_RADIX + 2;
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

  /**
   *
   * @param container
   * @param position
   * @return
   */
  @Override
  public final Object instantiateItem(ViewGroup container, int position) {

    position = getActualIndex(position);

    position %= getDataCount();

    View v = getView(position);

    if (v == null) {
      v = getEmptyView();
    }

    v.setTag(position);

    container.addView(v);

    return v;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
//    super.destroyItem(container, position, object);
    container.removeView((View) object);
  }


  @Override
  public final boolean isViewFromObject(View view, Object o) {
    return view == o;
  }


}

