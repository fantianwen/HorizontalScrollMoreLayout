package radasm.dooioo.com.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by RadAsm on 17/1/7.
 * <p>
 * Note：如果需要动态添加一些View，需要在添加完毕之后制动调用{@link View#requestLayout()}使其重新布局
 */
public class HorizontalScrollMoreLayout extends ViewGroup {

    private final Context mContext;
    private Scroller mScroller;

    // 显示load more动画的View
    private View mMoreView;
    private float mXDown;
    private float mXLastMove;
    private float mXMove;
    private float mTouchSlop;
    private int leftBorder;
    private int rightBorder;
    private int animationBorder;
    private int shouldBeginAnimationBorderX;
    private LoadMoreListener mLoadMoreDelegator;
    private boolean isLoadingAnimation;
    private boolean canLoadMore = true;

    public HorizontalScrollMoreLayout(Context context) {
        this(context, null);
    }

    public HorizontalScrollMoreLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        init();
    }

    private void init() {
        mScroller = new Scroller(mContext);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledPagingTouchSlop();

        // TODO: 17/1/7 属性设置更多的load More的View
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: 17/1/7 某些情况的判断
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (!childView.isClickable()) {
                // 很急很关键
                childView.setClickable(true);
            }
            if (i == 0) {
                childView.layout(0, 0, getChildAt(0).getMeasuredWidth(), getChildAt(0).getMeasuredHeight());
            } else {
                int left = getChildAt(i - 1).getRight();
                childView.layout(left, 0, left + getChildAt(i).getMeasuredWidth(), getChildAt(i).getMeasuredHeight());
            }
        }
        if (childCount > 0) {
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();

            shouldBeginAnimationBorderX = rightBorder - getChildAt(getChildCount() - 1).getMeasuredWidth();
            animationBorder = rightBorder - getChildAt(getChildCount() - 1).getMeasuredWidth() / 2;

            if (canLoadMore) {
                mMoreView = getChildAt(getChildCount() - 1);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mXLastMove = mXDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
                int rightlimitX = getScrollX() + getWidth() + scrolledX;
                if (getScrollX() + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (rightlimitX > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                } else if (rightlimitX < animationBorder && rightlimitX > shouldBeginAnimationBorderX) {
                    if (canLoadMore && isLoadingAnimation && mLoadMoreDelegator != null) {
                        mLoadMoreDelegator.loadMoreBackAnimation(mMoreView);
                        isLoadingAnimation = false;
                    }
                } else if (rightlimitX >= animationBorder) {
                    if (canLoadMore && !isLoadingAnimation && mLoadMoreDelegator != null) {
                        mLoadMoreDelegator.loadMoreAnimation(mMoreView);
                        isLoadingAnimation = true;
                    }
                }
                scrollBy(scrolledX, 0);
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:

                if (canLoadMore) {
                    int dx = getScrollX() + getWidth() - getChildAt(getChildCount() - 1).getLeft();
                    if (getScrollX() + getWidth() < animationBorder && getScrollX() + getWidth() > shouldBeginAnimationBorderX) {
                        mScroller.startScroll(getScrollX(), 0, -dx, 0);
                        if (mLoadMoreDelegator != null) {
                            mLoadMoreDelegator.onLoadMoreBack();
                        }
                    } else if (getScrollX() + getWidth() >= animationBorder) {
                        mScroller.startScroll(getScrollX(), 0, -dx, 0);
                        if (mLoadMoreDelegator != null) {
                            mLoadMoreDelegator.onLoadMore();
                        }
                    }
                    invalidate();
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreDelegator = loadMoreListener;
    }

    public void canLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public interface LoadMoreListener {
        void onLoadMore();

        void onLoadMoreBack();

        void loadMoreAnimation(View moreView);

        void loadMoreBackAnimation(View moreView);

    }

}
