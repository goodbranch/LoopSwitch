package loop.boss.com.loopswitch;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 手动自动可以滚动
 *
 * @author aoaoboss
 * @since 6.4.3 2015/11/16
 */
public abstract class AutoLoopSwitchBaseView extends RelativeLayout implements ViewPager.OnPageChangeListener {

  private final int VIEWPAGER_SCROLL_DURTION = 400;

  private ViewPager mViewPager;
  private TextView mTitleTv;
  private PageShowView mPageShowView;
  //
  private int mCurrentItem = 1;

  private LoopHandler mHandler;

  protected AutoLoopSwitchBaseAdapter mPagerAdapter;

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
   */
  protected abstract void onSwitch(int index, Object o);

  /**
   * 滚动延时时间
   */
  protected abstract long getDurtion();

  private void initView() {
    mViewPager = new ViewPager(getContext());
    mViewPager.setId(R.id.autoloopswitch_viewpager_id);
    mViewPager.addOnPageChangeListener(this);
    addView(mViewPager, generalLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    controlViewPagerSpeed();

    LayoutParams params;

    mTitleTv = new TextView(getContext());
    mTitleTv.setId(R.id.autoloopswitch_title_textview_id);
    mTitleTv.setMaxLines(2);
    mTitleTv.setTextColor(getResources().getColor(R.color.white));
    mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.loopswitch_text_size));
    mTitleTv.setShadowLayer(1, 1, 1, Color.BLACK);
    mTitleTv.setGravity(Gravity.CENTER_VERTICAL);
    params = generalLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    params.setMargins(getResources().getDimensionPixelOffset(R.dimen.loopswitch_title_margin_left), 0,
        getResources().getDimensionPixelOffset(R.dimen.loopswitch_title_margin_left),
        getResources().getDimensionPixelOffset(R.dimen.loopswitch_title_margin_bottom));
    addView(mTitleTv, params);

    mPageShowView = new PageShowView(getContext());
    mPageShowView.setId(R.id.autoloopswitch_pagershow_id);
    params = generalLayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.loopswitch_pageshow_height));
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

    mCurrentItem = i;

    if (mPagerAdapter.getDataCount() > 1) {
      int index = mPagerAdapter.getActualIndex(i);
      mPageShowView.setCurrentView(index, mPagerAdapter.getDataCount());
      onSwitch(index, mPagerAdapter.getItem(index));
    }

  }

  @Override
  public void onPageScrollStateChanged(int i) {

    if (i == ViewPager.SCROLL_STATE_DRAGGING) {
      mHandler.sendEmptyMessage(LoopHandler.MSG_STOP);
    } else if (i == ViewPager.SCROLL_STATE_IDLE) {
      if (mViewPager.getCurrentItem() == 0) {
        mViewPager.setCurrentItem(mPagerAdapter.getCount() - 2, false);
      } else if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
        mViewPager.setCurrentItem(1, false);
      }
      mCurrentItem = mViewPager.getCurrentItem();
      mHandler.sendEmptyMessageDelayed(LoopHandler.MSG_UPDATE, getDurtion());
    }
  }


  public final void notifyDataSetChanged() {
    if (mPagerAdapter != null) {
      mViewPager.setAdapter(mPagerAdapter);
      mPagerAdapter.notifyDataSetChanged();
      mViewPager.setCurrentItem(mCurrentItem);
      if (mPagerAdapter.getDataCount() > 1) {
        mPageShowView.setCurrentView(mPagerAdapter.getActualIndex(mCurrentItem), mPagerAdapter.getDataCount());
      }
    }
    startAutoSwtch();
  }

  public void setAdapter(AutoLoopSwitchBaseAdapter mPagerAdapter) {
    this.mPagerAdapter = mPagerAdapter;
    notifyDataSetChanged();
  }

  public void startAutoSwtch() {
    mHandler.sendEmptyMessageDelayed(LoopHandler.MSG_UPDATE, getDurtion());
  }

  public void startAutoSwitch(long delayTime) {
    mHandler.sendEmptyMessageDelayed(LoopHandler.MSG_UPDATE, delayTime);
  }

  public void stopAutoSwitch() {
    mHandler.sendEmptyMessage(LoopHandler.MSG_STOP);
  }


  private static class LoopHandler extends Handler {
    /**
     * 请求更新显示的View。
     */
    public static final int MSG_UPDATE = 1;
    /**
     * 请求暂停轮播。
     */
    public static final int MSG_STOP = 2;
    /**
     * 请求恢复轮播。
     */
    public static final int MSG_REGAIN = 3;

    private AutoLoopSwitchBaseView mView;

    public LoopHandler(AutoLoopSwitchBaseView mView) {
      this.mView = new WeakReference<AutoLoopSwitchBaseView>(mView).get();
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (mView == null || mView.mPagerAdapter == null) {
        return;
      }
      if (mView.mHandler.hasMessages(MSG_UPDATE)) {
        mView.mHandler.removeMessages(MSG_UPDATE);
      }

      switch (msg.what) {
        case MSG_UPDATE:
          if (mView.mPagerAdapter.getCount() > 1) {
            mView.mCurrentItem++;
            mView.mCurrentItem %= mView.mPagerAdapter.getCount();
            mView.mViewPager.setCurrentItem(mView.mCurrentItem, true);
            mView.mHandler.sendEmptyMessageDelayed(MSG_UPDATE, mView.getDurtion());
          }
          break;
        case MSG_STOP:
          break;
        case MSG_REGAIN:
          mView.mHandler.sendEmptyMessageDelayed(MSG_UPDATE, mView.getDurtion());
          break;
      }
    }
  }

  public TextView getTitleTv() {
    return mTitleTv;
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 修复ViewPager滚动速度不能修改问题
   *
   * @author ryze
   * @since 6.4.3 2015/11/16
   */
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
