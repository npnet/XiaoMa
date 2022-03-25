package com.xiaoma.xting.common.adapter;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.utils.Transformations;

import java.util.ArrayList;
import java.util.List;

/**
 * 由于需要在本类中 获取到recyclerView对象，所以需要用到该Adapter的地方
 * 需要使用 {@link BaseQuickAdapter#bindToRecyclerView}来绑定recyclerView
 * 而不是直接使用 {@link RecyclerView#setAdapter}进行设置
 *
 * @author KY
 * @date 2018/10/12
 */
public class EditableAdapter<T extends IGalleryData> extends XMBaseAbstractBQAdapter<T, MyBaseViewHolder> {

    private boolean isOnEdit;
    private boolean draggable = true;
    private Animation zoomUpAnimation;
    private List<BaseViewHolder> visibleItem = new ArrayList<>();
    private OnEditListener<T> onEditListener;
    private int marqueeResId = R.id.title;
    private AutoScrollTextView lastMarqueeView;
    //    private MiniPlayerControlHelper.LoadErrorListener loadErrorListener;
    private boolean isLongPress;

    public EditableAdapter(@Nullable List<T> data) {
        super(R.layout.item_gallery, data);
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (R.id.iv_close == view.getId()) {
                    T t = mData.get(position);
                    remove(position);
                    if (onEditListener != null) onEditListener.onRemove(t);
                    if (getData().size() == 0) {
                        setOnEdit(false);
                    }
                }
            }
        });
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        CharSequence title = getData().get(position).getTitleText(mContext);
        if (title == null) {
            return new ItemEvent(mContext.getString(R.string.state_unknown), String.valueOf(position));
        }
        return new ItemEvent(getData().get(position).getTitleText(mContext).toString(), String.valueOf(position));
    }

    public void setOnEditListener(OnEditListener<T> onEditListener) {
        this.onEditListener = onEditListener;
    }

    public boolean isOnEdit() {
        return isOnEdit;
    }

    public boolean isLongPress() {
        return isLongPress;
    }

    public void updateLongPressd() {
        if (isLongPress) {
            isLongPress = false;
        }
    }

    public void setOnEdit(boolean isOnEdit) {
        this.isOnEdit = isOnEdit;
        if (lastMarqueeView != null) {
            lastMarqueeView.stopMarquee();
        }
        for (BaseViewHolder viewHolder : visibleItem) {
            viewHolder.setVisible(R.id.iv_close, isOnEdit);
            View view = viewHolder.itemView;
            if (isOnEdit) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake));
            } else {
                view.clearAnimation();
            }
        }
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyBaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        visibleItem.remove(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        super.bindToRecyclerView(recyclerView);
        zoomUpAnimation = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.zoom_up);

        // create MyItemTouchHelperCallback
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback();
        // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        // Attach ItemTouchHelper to RecyclerView
        touchHelper.attachToRecyclerView(getRecyclerView());

//        loadErrorListener = new MiniPlayerControlHelper.LoadErrorListener() {
//            @Override
//            public void onLoadError() {
//                stopMarquee();
//            }
//        };
//        MiniPlayerControlHelper.getInstance().addLoadErrorListener(loadErrorListener);

        // 设置recycler的点击事件以取消编辑模式
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            private long downTime;
            private PointF downPoint;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                    downPoint = new PointF(event.getX(), event.getY());

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (this != null
                            && System.currentTimeMillis() - downTime <= 400
                            && getPointDistance(downPoint, new PointF(event.getX(), event.getY())) < ViewConfiguration.get(mContext).getScaledTouchSlop()) {
                        setOnEdit(false);
                    }
                }
                return false;
            }
        });
    }

    private float getPointDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2)
                + Math.pow(p1.y - p2.y, 2));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        visibleItem.add(holder);
        holder.setVisible(R.id.iv_close, isOnEdit);
        if (isOnEdit) {
            startAnimation(holder);
        } else {
            holder.itemView.clearAnimation();
        }
    }

    @Override
    protected void convert(final MyBaseViewHolder helper, T item) {
        ImageLoader.with(mContext)
                .load(item.getCoverUrl())
                .placeholder(R.drawable.fm_default_cover)
                .transform(Transformations.getRoundedCorners())
                .into((ImageView) helper.getView(R.id.iv_cover));

        CharSequence title = item.getTitleText(mContext);
        if (TextUtils.isEmpty(title)) {
            helper.setVisible(R.id.title, false);
        } else {
            helper.setVisible(R.id.title, true);
            ((AutoScrollTextView) helper.getView(R.id.title)).setText((String) title);
        }

        CharSequence footer = item.getFooterText(mContext);
        if (TextUtils.isEmpty(footer)) {
            helper.setVisible(R.id.footer, false);
        } else {
            helper.setVisible(R.id.footer, true);
            helper.setText(R.id.footer, footer);
        }

        CharSequence bottom = item.getBottomText(mContext);
        if (TextUtils.isEmpty(bottom)) {
            helper.setVisible(R.id.bottom, false);
        } else {
            helper.setVisible(R.id.bottom, true);
            if (item instanceof BaseChannelBean) {
                ((AutoScrollTextView) helper.getView(R.id.bottom)).setText((String) bottom);
            } else {
                helper.setText(R.id.bottom, bottom);
            }
        }

        helper.addOnClickListener(R.id.iv_close);

        if (isOnEdit) {
            helper.setVisible(R.id.iv_close, true);
        } else {
            helper.setVisible(R.id.iv_close, false);
        }
    }

    @Override
    public void setOnItemClick(View v, int position) {
        if (isOnEdit && !isLongPress) {
            setOnEdit(false);
        } else {
            super.setOnItemClick(v, position);
            if (NetworkUtils.isConnected(mContext)) {
                if (lastMarqueeView != null && lastMarqueeView.isStarting) {
                    lastMarqueeView.stopMarquee();
                }
                lastMarqueeView = v.findViewById(marqueeResId);
                if (lastMarqueeView != null && !lastMarqueeView.isStarting && !isOnEdit) {
                    lastMarqueeView.startMarquee();
                }
            }
        }

        if (isLongPress) {
            isLongPress = false;
        }
    }

    /**
     * 用于设置自动滚动的 AutoScrollTextView 的resId，默认为 title
     *
     * @param marqueeResId 自动滚动的 AutoScrollTextView 的resId
     */
    public void setMarqueeResId(int marqueeResId) {
        this.marqueeResId = marqueeResId;
    }

    /**
     * 如果当前列表有正在滚动的文字，停止滚动
     */
    public void stopMarquee() {
        if (lastMarqueeView != null && lastMarqueeView.isStarting) {
            lastMarqueeView.stopMarquee();
        }
    }

    public void startMarquee(View v) {
        if (NetworkUtils.isConnected(mContext)) {
            if (lastMarqueeView == null) {
                lastMarqueeView = v.findViewById(marqueeResId);
            }
            if (lastMarqueeView != null && !lastMarqueeView.isStarting) {
                lastMarqueeView.stopMarquee();
                lastMarqueeView.startMarquee();
            }
        }
    }

    public void refreshMarquee(View v) {
        if (NetworkUtils.isConnected(mContext)) {
            AutoScrollTextView curMarqueeView = v.findViewById(marqueeResId);
            if (lastMarqueeView != null) {
                if (curMarqueeView.equals(lastMarqueeView)) {
                    lastMarqueeView.startMarquee();
                } else {
                    lastMarqueeView.stopMarquee();
                    curMarqueeView.startMarquee();
                    lastMarqueeView = curMarqueeView;
                }
            } else {
                lastMarqueeView = curMarqueeView;
                lastMarqueeView.startMarquee();
            }
        }
    }

    private void startAnimation(RecyclerView.ViewHolder holder) {
        Animation animation = holder.itemView.getAnimation();
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        }
        holder.itemView.startAnimation(animation);
    }

    public interface OnEditListener<T> {
        void onChange(List<T> data);

        void onRemove(T data);
    }

    public class MyItemTouchHelperCallback<T> extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // movements drag
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            // as parameter, action drag and flags drag
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, dragFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            List<T> mList = ((EditableAdapter) recyclerView.getAdapter()).getData();
            mList.add(target.getAdapterPosition(), mList.remove(viewHolder.getAdapterPosition()));
            recyclerView.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            if (onEditListener != null) onEditListener.onChange(getData());
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            if (!isOnEdit() && !ListUtils.isEmpty(getData())) {
                setOnEdit(true);
                isLongPress = true;
                return false;
            } else {
                return draggable;
            }
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            // swiped enabled
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // swiped disabled
        }

        @Override
        public void onSelectedChanged(final RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                final Animation originalAnimation = viewHolder.itemView.getAnimation();
                if (originalAnimation != null) {
                    zoomUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewHolder.itemView.startAnimation(originalAnimation);
                            zoomUpAnimation.setAnimationListener(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                viewHolder.itemView.startAnimation(zoomUpAnimation);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    }
}

