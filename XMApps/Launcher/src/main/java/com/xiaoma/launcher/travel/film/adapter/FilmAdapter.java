package com.xiaoma.launcher.travel.film.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.FilmsBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

public class FilmAdapter extends XMBaseAbstractBQAdapter<FilmsBean,BaseViewHolder> {
    private ImageView mFilmImg;
    private TextView mFilmScore;
    private TextView mFilmType;
    private TextView mFilmTime;
    LinearLayout mFilmLayout;

    public FilmAdapter() {
        super(R.layout.film_item);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    protected void convert(BaseViewHolder helper, FilmsBean item) {
        initView(helper);
        initData(item);
    }

    private void initData(FilmsBean item) {
        mFilmScore.setText(item.getFilmScore());
        String[] strings = item.getFilmType().split(",");
        String typeString = new String();
        if (strings!=null){
            if (strings.length>=2){
                typeString = strings[0]+"/"+strings[1];
            }else {
                typeString = strings[0];
            }
        }
        mFilmType.setText(typeString);
        mFilmTime.setText(String.format(mContext.getString(R.string.film_duration),item.getDuration()));
        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_film_img)
                .error(R.drawable.not_film_img)
                .into(mFilmImg);

    }
    private void initView(BaseViewHolder helper) {
        mFilmImg = helper.getView(R.id.film_img);
        mFilmScore = helper.getView(R.id.film_score);
        mFilmLayout = helper.getView(R.id.film_layout);
        mFilmType = helper.getView(R.id.film_type);
        mFilmTime = helper.getView(R.id.film_time);

//        helper.addOnClickListener(R.id.film_collection);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent("","");
    }

}

