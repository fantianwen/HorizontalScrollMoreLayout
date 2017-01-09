### 这是一个横向拉动，可以load More的控件，你可以在回调loadMore接口中做一些事情（比如跳转什么的。。。）

> preview

![horizontalScrollMoreLayout](http://o7zh7nhn0.bkt.clouddn.com/horizontalScrollMoreLayout.gif)

### feature

- `loadMore` : 如果设置为`true`,则支持loadMore，否则就是一个普通的横向滑动的Layout；
- `loadMoreView` : 设置需要展示的loadMore的view，当然，已经默认有一个实现了。

- 设置自定义的loadMore的动画

    ```
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
    ```

- 设置当loadMore触发时候的回调

    ```
    horizontalScrollMoreLayout.setLoadMoreListener(new HorizontalScrollMoreLayout.LoadMoreListener() {
                @Override
                public void onLoadMore() {
    
                }
    
                @Override
                public void onLoadMoreBack() {
    
                }
            });
    ```

### TODO

- [ ] 支持内部可以横线滚动的View，如`LinearLayout`等
- [x] 暴露loadmoreView，可以自定义
- [x] 默认的动画


### 注意事项：

- 如果你希望动态的addView，则需要在addView完毕之后，主动调用`requestLayout()`方法。