package com.xiaoma.travel.view.citypick.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.travel.R;
import com.xiaoma.travel.view.citypick.holder.CharacterHolder;
import com.xiaoma.travel.view.citypick.holder.CityHolder;
import com.xiaoma.travel.view.citypick.holder.HotCityHolder;
import com.xiaoma.travel.view.citypick.holder.LocateViewHolder;
import com.xiaoma.travel.view.citypick.model.City;
import com.xiaoma.travel.view.citypick.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author wutao
 * @date 2018/11/6
 */
public class CityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOCATING = 111;
    public static final int FAILED = 666;
    public static final int SUCCESS = 888;
    private static final int VIEW_TYPE_COUNT = 3;
    private final int ITEM_TYPE_CHARACTER = 5;
    private final int ITEM_TYPE_CITY = 6;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<City> mCitys;
    private List<String> characterList; // 字母List
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> mHotCitys;
    private HashMap<String, Integer> letterIndexes;
    private OnCityClickListener onCityClickListener;
    private int locateState = LOCATING;
    private String locatedCity;


    public CityRecyclerAdapter(Context mContext, List<City> mCitys) {
        this.mContext = mContext;
        this.mCitys = mCitys == null ? new ArrayList<City>() : mCitys;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mCitys.add(0, new City(mContext.getString(R.string.location), "0"));
        this.mCitys.add(1, new City(mContext.getString(R.string.hot), "1"));
        handleCity();
    }

    private void handleCity() {
        resultList = new ArrayList<>();
        characterList = new ArrayList<>();
        letterIndexes = new HashMap<>();


        letterIndexes.put("定", 0);
        letterIndexes.put("热", 1);

        for (int index = 2; index < mCitys.size(); index++) {
            //当前城市拼音首字母
            String name = getFirstLetter(mCitys.get(index).getPinyin());
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE_CHARACTER));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE_CHARACTER));
                    }
                }
            }
            resultList.add(new Contact(mCitys.get(index).getName(), ITEM_TYPE_CITY));
        }

        for (int i = 0; i < resultList.size(); i++) {
            Contact contact = resultList.get(i);
            if (contact.getmType() == ITEM_TYPE_CHARACTER) {
                letterIndexes.put(contact.getmName(), i + 2);
            }
        }

        mHotCitys = new ArrayList<>();
        mHotCitys.add("北京");
        mHotCitys.add("上海");
        mHotCitys.add("深圳");
        mHotCitys.add("广州");
        mHotCitys.add("成都");
        mHotCitys.add("杭州");

    }

    private String getFirstLetter(final String pinyin) {
        if (TextUtils.isEmpty(pinyin)) return mContext.getString(R.string.location);
        String c = pinyin.substring(0, 1);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c).matches()) {
            return c.toUpperCase();
        } else if ("0".equals(c)) {
            return mContext.getString(R.string.location);
        } else if ("1".equals(c)) {
            return mContext.getString(R.string.hot);
        }
        return mContext.getString(R.string.location);
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, String city) {
        this.locateState = state;
        this.locatedCity = city;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CHARACTER) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else if (viewType == ITEM_TYPE_CITY) {
            View allCityView = mLayoutInflater.inflate(R.layout.item_city, parent, false);

            return new CityHolder(allCityView);
        } else if (viewType == 0) {
            View locateView = mLayoutInflater.inflate(R.layout.item_locate_city, parent, false);
            locateView.findViewById(R.id.layout_locate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (locateState == FAILED) {
                        //重新定位
                        if (onCityClickListener != null) {
                            onCityClickListener.onLocateClick();
                        }
                    } else if (locateState == SUCCESS) {
                        //返回定位城市
                        if (onCityClickListener != null) {
                            onCityClickListener.onCityClick(locatedCity);
                        }
                    }
                }
            });
            return new LocateViewHolder(locateView);
        } else {
            return new HotCityHolder(mLayoutInflater.inflate(R.layout.item_hot_city, parent, false), mContext);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mCharater.setText(resultList.get(position - 2).getmName());
        } else if (holder instanceof CityHolder) {
            ((CityHolder) holder).mCityName.setText(resultList.get(position - 2).getmName());
            ((CityHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //返回定位城市
                    if (onCityClickListener != null) {
                        onCityClickListener.onCityClick(((CityHolder) holder).mCityName.getText().toString());
                    }
                }
            });
        } else if (holder instanceof LocateViewHolder) {

            switch (locateState) {
                case LOCATING:
                    ((LocateViewHolder) holder).mTvLocatedCity.setText(R.string.locating);
                    break;
                case FAILED:
                    ((LocateViewHolder) holder).mTvLocatedCity.setText(R.string.located_failed);
                    break;
                case SUCCESS:
                    ((LocateViewHolder) holder).mTvLocatedCity.setText(locatedCity);
                    break;
            }

        } else if (holder instanceof HotCityHolder) {
            ((HotCityHolder) holder).setDate(mHotCitys, onCityClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < VIEW_TYPE_COUNT - 1 ? position : resultList.get(position - 2).getmType();
    }

    @Override
    public int getItemCount() {
        return resultList.size() + 2;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    /**
     * 获取字母索引的位置
     */
    public int getPositionForSection(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    public interface OnCityClickListener {
        void onCityClick(String name);

        void onLocateClick();
    }


}
