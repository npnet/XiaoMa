
# ControllableViewPager
控制 ViewPager 是否滑动

# FlowLayout
流式布局，支持单行显示

# XmDialog 使用
- 正常使用
```
    new XmDialog.Builder(mActivity)
            .setLayoutRes(R.layout.dialog_normal)    //设置弹窗展示的xml布局
            .setView(view)  //设置弹窗布局,直接传入View
            .setWidth(600)  //设置弹窗宽度(px)
            .setHeight(800)  //设置弹窗高度(px)
            .setScreenWidthAspect(mActivity, 0.8f)   //设置弹窗宽度(参数aspect为屏幕宽度比例 0 - 1f)
            .setScreenHeightAspect(mActivity, 0.3f)  //设置弹窗高度(参数aspect为屏幕宽度比例 0 - 1f)
            .setGravity(Gravity.CENTER)     //设置弹窗展示位置
            .setTag(TAG)   //设置Tag
            .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
            .setCancelableOutside(true)     //弹窗在界面外是否可以点击取消
            .setDialogAnimationRes(R.style.animate_dialog_scale) //设置弹窗动画
            .setOnDismissListener(dialog -> Toast.makeText(mActivity, "弹窗消失回调", Toast.LENGTH_SHORT).show())
            .addOnClickListener(R.id.btn_left, R.id.btn_right, R.id.tv_title)   //添加进行点击控件的id
            .setOnViewClickListener((view, tDialog) -> {
                switch (view.getId()) {
                    case R.id.btn_left:
                        Toast.makeText(mActivity, "点击确定", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btn_right:
                        Toast.makeText(mActivity, "点击了取消", Toast.LENGTH_SHORT).show();
                        tDialog.dismiss();
                        break;
                    case R.id.tv_title:
                        Toast.makeText(mActivity, "我是标题，点我干嘛", Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .create()
            .show();
```

- List列表使用
```
    RecyclerView recycleView = (RecyclerView) LayoutInflater.from(mActivity).inflate(R.layout.recycleview, null);
    recycleView.setLayoutManager(new LinearLayoutManager(mActivity));
    ArrayList<TestBean> list = new ArrayList<>();
    list.add(new TestBean("小王",1));
    list.add(new TestBean("小李",2));
    list.add(new TestBean("小张",3));
    MyAdapter adapter = new MyAdapter(R.layout.item_text, list);
    recycleView.setAdapter(adapter);
    adapter.setOnItemClickListener((adapter1, view, position) ->
            Toast.makeText(mActivity, "点击了条目---> " + position, Toast.LENGTH_SHORT).show());
    new XmDialog.Builder(mActivity)
            .setView(recycleView)
            .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
            .create()
            .show();
```
-XMBaseAbstractRyAdapter (带item点击事件) 使用
```eg:
    public class XmPictureAdapter extends XMBaseAbstractRyAdapter<UsbMediaInfo> {

        private static final int WIDTH = 200;
        /**
         * CENTER_CROP
         */
        private static final int SCALE_MODE = 1;

        public XmPictureAdapter(Context context, List<UsbMediaInfo> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        protected void convert(XMViewHolder holder, UsbMediaInfo usbMediaInfo, int position) {
            holder.setText(R.id.tv_name, usbMediaInfo.getMediaName());
            ProgressBar pb = holder.getView(R.id.progress);
            PlayImageView playImageView = holder.getView(R.id.iv_cover);
            playImageView.setImageType(XkanConstants.FILE_TYPE_PIC);
            ImageLoader.with(mContext)
                    .file(usbMediaInfo.getPath())
                    .override((int) ConvertUtils.px2dp(mContext, WIDTH), (int) ConvertUtils.px2dp(mContext, WIDTH))
                    .scale(SCALE_MODE)
                    .listener(new ImageLoaderCallBack(playImageView, pb))
                    .into(playImageView);
        }


    }
    pictureAdapter = new XmPictureAdapter(getActivity(),new ArrayList<UsbMediaInfo>(),R.layout.item_picture);
    xmPictureAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                intentToXmPhotoActivity(position);
            }
        });
```
XMBaseAbstractLvAdapter (ListView 和 GridView 基类adapter) 的使用
```eg:
     public class XmLvAdapter extends XMBaseAbstractLvAdapter<UsbMediaInfo> {

            private static final int WIDTH = 200;
            /**
             * CENTER_CROP
             */
            private static final int SCALE_MODE = 1;

            public XmPictureAdapter(Context context, List<UsbMediaInfo> datas, int layoutId) {
                super(context, datas, layoutId);
            }

            @Override
            protected void convert(XMViewHolder holder, UsbMediaInfo usbMediaInfo, int position) {
                holder.setText(R.id.tv_name, usbMediaInfo.getMediaName());
                ProgressBar pb = holder.getView(R.id.progress);
                PlayImageView playImageView = holder.getView(R.id.iv_cover);
                playImageView.setImageType(XkanConstants.FILE_TYPE_PIC);
                ImageLoader.with(mContext)
                        .file(usbMediaInfo.getPath())
                        .override((int) ConvertUtils.px2dp(mContext, WIDTH), (int) ConvertUtils.px2dp(mContext, WIDTH))
                        .scale(SCALE_MODE)
                        .listener(new ImageLoaderCallBack(playImageView, pb))
                        .into(playImageView);
            }

            @Override
            public ItemEvent returnPositionEventMsg(int position){
                return new ItemEvent(mDatas.get(position).getMediaName(),mDatas.get(position).getMediaName());
            }
     }

     pictureAdapter = new XmLvAdapter(getActivity(),new ArrayList<UsbMediaInfo>(),R.layout.item_picture);

```
XMBaseAbstractBQAdapter (带打点接口的BaseQuickAdapter)
```eg:
    public class GridTextAdapter<T extends INamed> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {
        public GridTextAdapter(@Nullable List<T> data) {
        super(R.layout.item_category, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, T item) {
            helper.setText(R.id.board_or_class_name, item.getName());
        }

        @Override
        public ItemEvent returnPositionEventMsg(int position) {
            return null;
        }
    }

---------------------------------------------------------------------------------------------------------------------
DiscreteScrollView
注意:1.该库是基于RecyclerView的可滚动列表的实现，其中当前项目居中并且可以使用滑动来更改。它类似于ViewPager，但您可以快速轻松地创建布局，其中当前所选视图旁边的视图在屏幕上部分或完全可见。
    2.该库使用自定义LayoutManager来调整项目在屏幕上的位置并处理滚动，但它不会暴露给客户端代码。所有公共API都可以通过DiscreteScrollView类访问，该类是RecyclerView的简单后代。
      如果您曾经使用过RecyclerView  - 您已经知道如何使用这个库。
      有一点需要特别注意 - 你不应该设置LayoutManager。

使用:
    <com.discretescrollview.DiscreteScrollView
      android:id="@+id/picker"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      app:dsv_orientation="horizontal|vertical" />  <!-- orientation is optional, default is horizontal -->

    DiscreteScrollView scrollView = findViewById(R.id.picker);
    scrollView.setAdapter(new YourAdapterImplementation());

通用:
    scrollView.setOrientation(DSVOrientation o); //Sets an orientation of the view
    scrollView.setOffscreenItems(count); //在视图的每一侧保留等于（childSize * count）的额外空间 该设置对于通过InfiniteScrollAdapter.wrap过的adapter无效
    scrollView.setOverScrollEnabled(enabled); //Can also be set using android:overScrollMode xml attribute

涉及当前item:
    scrollView.getCurrentItem(); //returns adapter position of the currently selected item or -1 if adapter is empty.
    scrollView.scrollToPosition(int position); //position becomes selected
    scrollView.smoothScrollToPosition(int position); //position becomes selected with animated scroll
    scrollView.setItemTransitionTimeMillis(int millis); //determines how much time it takes to change the item on fling, settle or smoothScroll


正常使用:自己构造Adapter通过InfiniteScrollAdapter.wrap可实现自动循环效果，默认首个元素会居中显示
  DiscreteScrollView itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
  itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
  itemPicker.addOnItemChangedListener(this);
  infiniteAdapter = InfiniteScrollAdapter.wrap(new ShopAdapter(data));
  itemPicker.setAdapter(infiniteAdapter);
  itemPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
  itemPicker.setItemTransformer(new ScaleTransformer.Builder()
            .setMinScale(0.8f)
            .build());

 通过实现DiscreteScrollView.OnItemChangedListener接口onCurrentItemChanged方法可以更新当前item

 ---------------------------------------------------------------------------------------------------------------------

