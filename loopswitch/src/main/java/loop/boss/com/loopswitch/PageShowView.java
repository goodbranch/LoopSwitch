package loop.boss.com.loopswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 页码显示类
 *
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class PageShowView extends View {

  int colorCurrent = 0;

  int colorOther = 0;

  int total = 0;

  int current = 0;

  private Paint mPaint = null;

  public PageShowView(Context context) {
    this(context, null);
  }

  public PageShowView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initColor();
  }

  protected void initColor() {
    colorCurrent = getResources().getColor(R.color.loopswitch_page_current);

    colorOther = getResources().getColor(R.color.loopswitch_page_other);

    mPaint = new Paint();
  }

  public void setCurrentView(int current, int total) {
    this.current = current;
    this.total = total;
    invalidate();
  }


  @Override
  protected void dispatchDraw(Canvas canvas) {

    super.dispatchDraw(canvas);

    int view_height = getHeight() - getPaddingBottom() - getPaddingBottom();

    int view_width = getWidth() - getPaddingLeft() - getPaddingRight();

    int height = view_height / 10;

    int width = height * 6;

    if (total > 1) {

      if (width * total + height * (total - 1) > view_width) {
        width = (view_width - (height * (total - 1))) / total;
      }

      int posX = view_width / 2 - (width * total + height * (total - 1) * 3) / 2;

      mPaint.setStrokeWidth(height);

      for (int i = 0; i < total; i++) {
        if (i != current) {
          mPaint.setColor(colorOther);
        } else {
          mPaint.setColor(colorCurrent);
        }

        canvas.drawLine(posX, view_height / 2, posX + width, view_height / 2, mPaint);

        posX += height * 3 + width;
      }
    }

  }

  /**
   * 获取当前显示的位置
   */
  public int getCurrent() {
    return this.current;
  }

}
