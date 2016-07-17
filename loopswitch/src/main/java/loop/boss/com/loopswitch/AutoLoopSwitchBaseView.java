package loop.boss.com.loopswitch;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 手动自动可以滚动
 *
 * @author ryze
 * @since 1.0  2016/07/17
 */
public abstract class AutoLoopSwitchBaseView extends RelativeLayout implements ViewPager.OnPageChangeListener {

  private final int VIEWPAGER_SCROLL_DURTION = 400;

  protected ViewPager mViewPager;
  protected PageShowView mPageShowView;

  protected View mFailtView;
  //
  private int mCurrentItem = 1;

  protected LoopHandler mHandler;

  protected boolean mIsDragging = false;

  protected AutoLoopSwitchBaseAdapter mPagerAdapter;

  //监听数据变化，用于去除 重试View
  private DataSetObserver mObserver;


  //正在切页
  private boolean isLoopSwitch = false;

  private boolean mCurrentVisible = true;

  public AutoLoopSwitchBaseView(Context context) {
    super(context);
    initView();
  }

  public AutoLoopSwitchBaseView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public AutoLoopSwitchBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public AutoLoopSwitchBaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView();
  }

  /**
   * 页面切换回调
   *
   * @param model adapter getItem
   */
  protected abstract void onSwitch(int index, Object model);

  /**
   * 滚动延时时间
   */
  protected abstract long getDurtion();

  /**
   * 如果需要网络异常处理
   */
  protected abstract View getFailtView();

  @Override
  protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (visibility == VISIBLE) {
      mCurrentVisible = true;
    } else {
      mCurrentVisible = false;
    }
  }

  public boolean isCurrentVisible() {
    return mCurrentVisible;
  }

  public void setCurrentVisible(boolean mCurrentVisible) {
    this.mCurrentVisible = mCurrentVisible;
  }

  private void initView() {
    mViewPager = new ViewPager(getContext());
    mViewPager.setId(R.id.autoloopswitch_viewpager_id);
    mViewPager.addOnPageChangeListener(this);
    addView(mViewPager, generalLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    controlViewPagerSpeed();

    LayoutParams params;
    mPageShowView = new PageShowView(getContext());
    mPageShowView.setId(R.id.autoloopswitch_pagershow_id);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    params = generalLayoutParams(LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics));
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
    addView(mPageShowView, params);

    mHandler = new LoopHandler(this);
  }

  private LayoutParams generalLayoutParams(int w, int h) {
    LayoutParams params = new LayoutParams(w, h);
    return params;
  }

  @Override
  public void onPageScrolled(int i, float v, int i1) {

  }

  @Override
  public void onPageSelected(int i) {

    if (isLoopSwitch) {
      isLoopSwitch = false;
      return;
    }

    mCurrentItem = i;

    int datacount = mPagerAdapter.getDataCount();

    if (datacount > 1) {
      int index = mPagerAdapter.getActualIndex(i) % datacount;
      mPageShowView.setCurrentView(index, datacount);
      onSwitch(index, mPagerAdapter.getItem(index));
    }

  }

  @Override
  public void onPageScrollStateChanged(int i) {

    if (i == ViewPager.SCROLL_STATE_DRAGGING) {
      mIsDragging = true;
    } else if (i == ViewPager.SCROLL_STATE_IDLE) {
      if (mViewPager.getCurrentItem() == 0) {
        isLoopSwitch = true;
        mViewPager.setCurrentItem(mPagerAdapter.getCount() - 2, false);
      } else if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
        isLoopSwitch = true;
        mViewPager.setCurrentItem(1, false);
      }
      mCurrentItem = mViewPager.getCurrentItem();

      if (mIsDragging && mHandler != null) {
        //如果从dragging状态到不是mIsDragging
        mHandler.sendEmptyMessageDelayed(LoopHandler.MSG_UPDATE, getDurtion());
      }

      mIsDragging = false;

      Log.e("ryze", "onPageScrollStateChanged  " + i);
    }
  }


  private void notifyDataSetChanged() {
    if (mPagerAdapter != null) {
      int datacount = mPagerAdapter.getDataCount();

      int currentIndex = 0;
      if (datacount > 1) {
        mCurrentItem = mPagerAdapter.getCount() / 2;
        currentIndex = mPagerAdapter.getActualIndex(mCurrentItem) % datacount;
      } else {
        mCurrentItem = 1;
        currentIndex = 0;
      }
      mViewPager.setCurrentItem(mCurrentItem);
      mPageShowView.setCurrentView(currentIndex, datacount);

      if (mFailtView != null && datacount > 0) {
        removeView(mFailtView);
        mFailtView = null;
      }

      updateView();
    }
  }

  private void updateView() {
    for (int i = 0; i < mViewPager.getChildCount(); i++) {
      View v = mViewPager.getChildAt(i);
      if (v != null) {
        int position = (Integer) v.getTag();
        mPagerAdapter.updateView(v, position);
      }
    }

  }


  public void setAdapter(AutoLoopSwitchBaseAdapter adapter) {
    if (mPagerAdapter != null) {
      mPagerAdapter.unregisterDataSetObserver(mObserver);
    }
    this.mPagerAdapter = adapter;
    if (mPagerAdapter != null) {
      if (mObserver == null) {
        mObserver = new PagerObserver();
      }
      mPagerAdapter.registerDataSetObserver(mObserver);

      if (mViewPager != null) {
        mViewPager.setAdapter(mPagerAdapter);
      }
      //如果没有数据，同时没有网络的情况
      if (mPagerAdapter.getDataCount() <= 0) {
        mFailtView = getFailtView();
        if (mFailtView != null) {
          addView(mFailtView, generalLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
      }

    } else {
      throw new NullPointerException("AutoLoopSwitchBaseAdapter can not null");
    }
  }


  public void destory() {
    if (mHandler != null) {
      mHandler.close();
    }
  }

  protected static class LoopHandler extends Handler {
    //请求更新显示的View。
    private static final int MSG_UPDATE = 1;
    //请求暂停轮播。
    public static final int MSG_STOP = 2;
    //请求恢复轮播。
    public static final int MSG_REGAIN = 3;

    private AutoLoopSwitchBaseView mView;

    private boolean mIsStop = false;

    public LoopHandler(AutoLoopSwitchBaseView mView) {
      this.mView = new WeakReference<AutoLoopSwitchBaseView>(mView).get();
    }

    public boolean isStop() {
      return mIsStop;
    }

    public void close() {
      removeMessages(MSG_UPDATE);
      removeMessages(MSG_REGAIN);
      removeMessages(MSG_STOP);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (mView == null || mView.mHandler == null || mView.mPagerAdapter == null || mView.mIsDragging) {
        return;
      }

      Log.e("ryze", "stop: " + mIsStop);

      switch (msg.what) {
        case MSG_UPDATE:
          if (mIsStop || hasMessages(MSG_UPDATE)) {
            return;
          }
          if (mView.mPagerAdapter.getCount() > 1) {
            mView.mCurrentItem++;
            mView.mCurrentItem %= mView.mPagerAdapter.getCount();
            mView.mViewPager.setCurrentItem(mView.mCurrentItem, true);
            sendEmptyMessageDelayed(MSG_UPDATE, mView.getDurtion());
          }
          break;
        case MSG_STOP:
          if (hasMessages(MSG_UPDATE)) {
            removeMessages(MSG_UPDATE);
          }
          mIsStop = true;
          Log.e("ryze", "stop: MSG_STOP " + mIsStop);
          break;
        case MSG_REGAIN:
          if (hasMessages(MSG_UPDATE)) {
            removeMessages(MSG_UPDATE);
          }
          sendEmptyMessageDelayed(MSG_UPDATE, mView.getDurtion());
          mIsStop = false;
          Log.e("ryze", "stop: MSG_REGAIN " + mIsStop);
          break;
      }
    }
  }

  private class PagerObserver extends DataSetObserver {
    private PagerObserver() {
    }

    public void onChanged() {
      Log.e("ryze", "PagerObserver onChanged ");
      notifyDataSetChanged();
    }

    public void onInvalidated() {
      Log.e("ryze", "PagerObserver onInvalidated ");

      notifyDataSetChanged();
    }
  }


  public ViewPager getViewPager() {
    return mViewPager;
  }


  private void controlViewPagerSpeed() {
    try {
      Field mField;

      mField = ViewPager.class.getDeclaredField("mScroller");
      mField.setAccessible(true);

      FixedSpeedScroller mScroller = new FixedSpeedScroller(getContext(),
          new DecelerateInterpolator());
      mScroller.setmDuration(VIEWPAGER_SCROLL_DURTION);
      mField.set(mViewPager, mScroller);

      mField = ViewPager.class.getDeclaredField("mFlingDistance");
      mField.setAccessible(true);
      mField.set(mViewPager, 20);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private class FixedSpeedScroller extends Scroller {

    private int mDuration = 600; // default time is 600ms

    public FixedSpeedScroller(Context context) {
      super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
      super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
      // Ignore received duration, use fixed one instead
      super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
      // Ignore received duration, use fixed one instead
      super.startScroll(startX, startY, dx, dy, mDuration);
    }

    /**
     * set animation time
     */
    public void setmDuration(int time) {
      mDuration = time;
    }

    /**
     * get current animation time
     */
    public int getmDuration() {
      return mDuration;
    }
  }

}
