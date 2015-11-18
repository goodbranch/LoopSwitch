package loop.boss.com.loopswitchdemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

import loop.boss.com.loopswitch.AutoLoopSwitchBaseAdapter;

/**
 * Created by Ryze on 2015/11/18.
 */
public class AutoSwitchAdapter extends AutoLoopSwitchBaseAdapter {

  private Context mContext;

  private List<LoopModel> mDatas;

  public AutoSwitchAdapter(Context mContext, List<LoopModel> mDatas) {
    this.mContext = mContext;
    this.mDatas = mDatas;
  }

  @Override
  public int getDataCount() {
    return mDatas == null ? 0 : mDatas.size();
  }

  @Override
  public View getView(int position) {
    ImageView imageView = new ImageView(mContext);
    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
    LoopModel model = (LoopModel) getItem(position);
    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    imageView.setImageResource(model.getResId());

    return imageView;
  }

  @Override
  public Object getItem(int position) {
    if (position >= 0 && position < getDataCount()) {

      return mDatas.get(position);
    }
    return null;
  }
}
