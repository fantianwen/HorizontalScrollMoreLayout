package radasm.dooioo.com.horizontalloadmorelayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import radasm.dooioo.com.library.HorizontalScrollMoreLayout;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollMoreLayout horizontalScrollMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalScrollMoreLayout = (HorizontalScrollMoreLayout) findViewById(R.id.horizontalScrollMoreLayout);

        horizontalScrollMoreLayout.setLoadingMoreAnimation(new HorizontalScrollMoreLayout.LoadMoreAnimation() {
            @Override
            public void loadMoreAnimation(View moreView) {
                rotate(moreView);
            }

            @Override
            public void loadMoreBackAnimation(View moreView) {
                rotate(moreView);
            }
        });

        horizontalScrollMoreLayout.addView(generateView());

        horizontalScrollMoreLayout.setLoadMoreListener(new HorizontalScrollMoreLayout.LoadMoreListener() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreBack() {

            }
        });
    }

    private View generateView() {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setText("addedView");
        textView.setBackgroundColor(Color.BLUE);
        return textView;
    }

    public static void rotate(View moreView) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(0);
        moreView.setAnimation(rotateAnimation);
        rotateAnimation.start();
    }

}
