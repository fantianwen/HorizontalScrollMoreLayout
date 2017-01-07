### 这是一个横向拉动，可以load More的控件，你可以在回调loadMore接口中做一些事情（比如跳转什么的。。。）

> preview

![horizontalScrollMoreLayout](![http://o7zh7nhn0.bkt.clouddn.com/horizontalScrollMoreLayout.gif])

### 使用见demo，在xml文件中写入`horizontalScrollMoreLayout`最后一个view将成为loadmore展示的view

### 回调接口

```
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
                // 可以设置动画什么的
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
```


### 注意事项：

- 如果你希望动态的addView，则需要在addView完毕之后，主动调用`requestLayout()`方法。