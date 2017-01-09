package radasm.dooioo.com.horizontalloadmorelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import radasm.dooioo.com.library.HorizontalScrollMoreLayout;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollMoreLayout horizontalScrollMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalScrollMoreLayout = (HorizontalScrollMoreLayout) findViewById(R.id.horizontalScrollMoreLayout);

        horizontalScrollMoreLayout.setLoadMoreListener(new HorizontalScrollMoreLayout.LoadMoreListener() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreBack() {

            }

            @Override
            public void loadMoreAnimation(View moreView) {
                if(moreView instanceof TextView){
                    TextView moretextView = (TextView) moreView;
                    moretextView.setText("更多");
                }
            }

            @Override
            public void loadMoreBackAnimation(View moreView) {
                if(moreView instanceof TextView){
                    TextView moretextView = (TextView) moreView;
                    moretextView.setText("回来");
                }
            }
        });

    }
}
