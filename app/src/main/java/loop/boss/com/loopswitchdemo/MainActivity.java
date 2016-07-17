package loop.boss.com.loopswitchdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private AutoSwitchView mAutoSwitchView;
  private AutoSwitchAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mAutoSwitchView = (AutoSwitchView) findViewById(R.id.loopswitch);

    List<LoopModel> datas = new ArrayList<LoopModel>();
    LoopModel model = null;

    model = new LoopModel("第一张", R.mipmap.loop_1);
    datas.add(model);
    model = new LoopModel("第二张", R.mipmap.loop_2);
    datas.add(model);
    model = new LoopModel("第三张", R.mipmap.loop_3);
    datas.add(model);
    model = new LoopModel("第四张", R.mipmap.loop_4);
    datas.add(model);
    mAdapter = new AutoSwitchAdapter(getApplicationContext(), datas);
    mAutoSwitchView.setAdapter(mAdapter);

    mAdapter.notifyDataSetChanged();
  }

}
