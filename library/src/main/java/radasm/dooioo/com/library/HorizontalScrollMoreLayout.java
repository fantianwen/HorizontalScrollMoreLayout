package radasm.dooioo.com.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by RadAsm on 17/1/7.
 * <p>
 * Note：如果需要动态添加一些View，需要在添加完毕之后制动调用{@link View#requestLayout()}使其重新布局
 */
public class HorizontalScrollMoreLayout extends ViewGroup {

    private final Context mContext;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    // 显示load more动画的View
    private View mMoreView;
    private float mXDown;
    private float mXLastMove;
    private float mXMove;
    private float mTouchSlop;
    private int leftBorder;
    private int rightBorder;
    private int mFlingXMax;
    private int animationBorder;
    private int shouldBeginAnimationBorderX;
    private LoadMoreListener mLoadMoreDelegator;
    private boolean isLoadingAnimation;
    private boolean canLoadMore;
    private int loadMoreViewLayoutResId;
    private LoadMoreAnimation mLoadMoreAnimationDelegator;
    private int mMaxVelocity;
    private int mMinVelocity;

    public HorizontalScrollMoreLayout(Context context) {
        this(context, null);
    }

    public HorizontalScrollMoreLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollMoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mScroller = new Scroller(mContext);
        mTouchSlop = ViewConfiguration.get(mContext).getScaledPagingTouchSlop();
        mMaxVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();
        mMinVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollMoreLayout);
        canLoadMore = typedArray.getBoolean(R.styleable.HorizontalScrollMoreLayout_loadMore, true);
        loadMoreViewLayoutResId = typedArray.getResourceId(R.styleable.HorizontalScrollMoreLayout_loadMoreView, -1);

        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (canLoadMore) {
            View moreView = provideMoreView();
            addView(moreView, -1);
        }
    }

    private View provideMoreView() {
        if (loadMoreViewLayoutResId < 0) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
            return imageView;
        } else {
            View view = View.inflate(mContext, loadMoreViewLayoutResId, null);
            return view;
        }
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
            mFlingXMax = getChildAt(getChildCount() - 1).getLeft() - getWidth();

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
        obtainVelocityTracker();
        mVelocityTracker.addMovement(event);
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
                    if (canLoadMore && isLoadingAnimation) {
                        if (mLoadMoreAnimationDelegator != null) {
                            mLoadMoreAnimationDelegator.loadMoreBackAnimation(mMoreView);
                        } else {
                            rotate(mMoreView);
                        }
                        isLoadingAnimation = false;
                    }
                } else if (rightlimitX >= animationBorder) {
                    if (canLoadMore && !isLoadingAnimation) {
                        if (mLoadMoreAnimationDelegator != null) {
                            mLoadMoreAnimationDelegator.loadMoreAnimation(mMoreView);
                        } else {
                            rotate(mMoreView);
                        }
                        isLoadingAnimation = true;
                    }
                }
                scrollBy(scrolledX, 0);
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float xVelocity = mVelocityTracker.getXVelocity();

                if (Math.abs(xVelocity) > mMinVelocity) {
                    mScroller.fling(getScrollX(), 0, (int) -(xVelocity + 0.5), 0, 0, mFlingXMax, 0, getHeight());
                    ViewCompat.postInvalidateOnAnimation(this);
                }

                int dx = getScrollX() + getWidth() - getChildAt(getChildCount() - 1).getLeft();
                if (canLoadMore) {
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
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void obtainVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public void setLoadingMoreAnimation(LoadMoreAnimation loadingMoreAnimation) {
        this.mLoadMoreAnimationDelegator = loadingMoreAnimation;
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreDelegator = loadMoreListener;
    }

    public void canLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public interface LoadMoreAnimation {
        void loadMoreAnimation(View moreView);

        void loadMoreBackAnimation(View moreView);
    }

    public interface LoadMoreListener {
        void onLoadMore();

        void onLoadMoreBack();
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static void rotate(View moreView) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(0);
        moreView.setAnimation(rotateAnimation);
        rotateAnimation.start();
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

}
