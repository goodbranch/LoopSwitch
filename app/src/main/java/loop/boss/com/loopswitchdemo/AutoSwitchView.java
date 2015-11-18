package loop.boss.com.loopswitchdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import loop.boss.com.loopswitch.AutoLoopSwitchBaseView;

/**
 * Created by aoaoboss on 2015/11/18.
 */
public class AutoSwitchView extends AutoLoopSwitchBaseView {

  public AutoSwitchView(Context context) {
    super(context);
  }

  public AutoSwitchView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AutoSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public AutoSwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }


  @Override
  protected void onSwitch(int index, Object o) {
    LoopModel model = (LoopModel) o;
    if (model != null) {
      titleSwitchAnimation(model.getTitle());
    }
  }

  /**
   * 标题
   */
  private void titleSwitchAnimation(String title) {

    getTitleTv().clearAnimation();
    getTitleTv().setText(title);

    AlphaAnimation alpha = new AlphaAnimation(1.0f, 0f);
    alpha.setDuration(250);
    alpha.setAnimationListener(new Animation.AnimationListener() {

      @Override
      public void onAnimationStart(Animation arg0) {

      }

      @Override
      public void onAnimationRepeat(Animation arg0) {

      }

      @Override
      public void onAnimationEnd(Animation arg0) {

      }
    });

    alpha.setRepeatCount(1);
    alpha.setRepeatMode(Animation.REVERSE);
    getTitleTv().startAnimation(alpha);

  }

  @Override
  protected long getDurtion() {
    return 5000;
  }

}