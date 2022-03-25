package com.xiaoma.assistant.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.GasPriceBean;
import com.xiaoma.assistant.model.HoroscopeBean;
import com.xiaoma.assistant.model.LimitInfo;
import com.xiaoma.assistant.model.WeatherInfoV2;
import com.xiaoma.assistant.model.parser.NewStockBean;
import com.xiaoma.assistant.model.parser.ParserLocation;
import com.xiaoma.assistant.view.GasView;
import com.xiaoma.assistant.view.LimitView;
import com.xiaoma.assistant.view.ObservableScrollView;
import com.xiaoma.assistant.view.StockView;
import com.xiaoma.assistant.view.TimeItemView;
import com.xiaoma.assistant.view.TranslationView;
import com.xiaoma.assistant.view.VerticalScrollBar;
import com.xiaoma.assistant.view.ViewHoroscope;
import com.xiaoma.assistant.view.WeatherView;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音助手内容列表adapter
 */
public class AssistantAdapter extends BaseAdapter {
    private Context mContext;
    private List<ConversationItem> conversationItemList;
    //item类型数目
    private int viewTypeCount = 23;
    private ViewGroup parent;

    public AssistantAdapter(Context context, List<ConversationItem> conversationItemList) {
        this.mContext = context;
        this.conversationItemList = conversationItemList;
    }

    public void clearData() {
        if (conversationItemList != null) {
            this.conversationItemList.clear();
        }
    }


    public void setData(List<ConversationItem> conversationItemList) {
        this.conversationItemList = conversationItemList;
    }


    @Override
    public int getCount() {
        return conversationItemList == null ? 0 : conversationItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        ConversationItem item = conversationItemList.get(position);
        return item.getAction();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        int viewType = getItemViewType(position);
        ConversationItem item = conversationItemList.get(position);
        switch (viewType) {
            case VrConstants.ConversationType.INPUT:
                convertView = handleInput(item, convertView);
                break;
//            case VrConstants.ConversationType.NAVIGATION:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.FLIGHT:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.MUSIC:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.RADIO:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
            case VrConstants.ConversationType.LOCATION:
                convertView = handleLocation(item, convertView);
                break;
            case VrConstants.ConversationType.WEATHER:
                convertView = handleWeather(item, convertView);
                break;
//            case VrConstants.ConversationType.TIME:
//                convertView = handleTime(item, convertView);
//                break;
//            case VrConstants.ConversationType.DATE:
//                convertView = handleTime(item, convertView);
//                break;
//            case VrConstants.ConversationType.FAULT_TABLE:
//                convertView = handleFaultTable(convertView);
//                break;
            case VrConstants.ConversationType.LIMIT:
                convertView = handleLimit(item, convertView);
                break;
//            case VrConstants.ConversationType.VIOLATION:
//                convertView = handleViolation(item, convertView);
//                break;
            case VrConstants.ConversationType.GAS:
                convertView = handleGas(item, convertView);
                break;
            case VrConstants.ConversationType.STOCK:
                convertView = handleStock(item, convertView);
                break;
//            case VrConstants.ConversationType.TRAIN:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
            case VrConstants.ConversationType.CONSTELLATION:
                convertView = handleConstellation(item, convertView);
                break;
//            case VrConstants.ConversationType.NEARBY_CAR:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.NEARBY_GROUP:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.NEARBY_PARK:
//                convertView = handleSearching(convertView, viewType, item);
//                break;
//            case VrConstants.ConversationType.PHONE:
//                convertView = handlePhoneCall(item, convertView);
//                break;
//            case VrConstants.ConversationType.TRANSLATION:
//                convertView = handleTranslation(item, convertView);
//                break;
//            case VrConstants.ConversationType.IMAGE:
//                convertView = handleImage(item, convertView);
//                break;//
//            case VrConstants.ConversationType.TRAFFIC:
//                convertView = handleTraffic(item, convertView);
//                break;
            case VrConstants.ConversationType.CREATE_SCHEDULE:
                convertView = handleCreateSchedule(item, convertView);
                break;
            case VrConstants.ConversationType.OTHER:
            case VrConstants.ConversationType.TRANSLATION:
            case VrConstants.ConversationType.TIME:
            case VrConstants.ConversationType.DATE:
            case VrConstants.ConversationType.TRAFFIC:
                // 穿透
            default:
                convertView = handleOther(item, convertView);
                break;
        }
        return convertView;
    }

    private View handleCreateSchedule(ConversationItem item, View convertView) {
        ScheduleViewHolder holder;
        if (convertView == null) {
            holder = new ScheduleViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_create_schedule, parent, false);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvItem = convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ScheduleViewHolder) convertView.getTag();
        }

//        ScheduleBean info = (ScheduleBean) item.getAttachment();
//        if (info != null) {
//            holder.tvDate.setText("日期："+info.getDatetime().getDateOrig());
//            holder.tvTime.setText("时间"+info.getDatetime().getTimeOrig());
//            holder.tvItem.setText(info.getContent());
//        }
        return convertView;
    }


    private View handleInput(ConversationItem item, View convertView) {
        InputViewHolder inputHolder = new InputViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_input, parent, false);
            inputHolder.input = convertView.findViewById(R.id.tv_input);
            inputHolder.input.setMovementMethod(ScrollingMovementMethod.getInstance());
            // TODO: 2018/10/19 onTouch 待确认
            /*inputHolder.input.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //通知父控件不要干扰
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //通知父控件不要干扰
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    return false;
                }
            });*/

            convertView.setTag(inputHolder);
        } else {
            inputHolder = (InputViewHolder) convertView.getTag();
        }
        inputHolder.input.setText(item.getData());
        return convertView;
    }


    private View handleOther(ConversationItem item, View convertView) {
        TextViewHolder textHolder;
        if (convertView == null) {
            textHolder = new TextViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_text, parent, false);
            textHolder.receiverMsg = convertView.findViewById(R.id.tv_received_msg);

            final ObservableScrollView scrollView = convertView.findViewById(R.id.scrollView);
            VerticalScrollBar scrollBar = convertView.findViewById(R.id.scrollbar);
            scrollBar.setScrollView(scrollView);

            final View relativeLayout = convertView;
            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int paddingRight = scrollView.getVerticalScrollRange() > scrollView.getHeight() ? 0 : mContext.getResources().getDimensionPixelSize(R.dimen.width_dialing_box_padding_left);
                    relativeLayout.setPadding(relativeLayout.getPaddingLeft(), relativeLayout.getPaddingTop(), paddingRight ,relativeLayout.getPaddingBottom());
                }
            });
            convertView.setTag(textHolder);

        } else {
            textHolder = (TextViewHolder) convertView.getTag();
        }
        String data = (String) item.getAttachment();
        if (TextUtils.isEmpty(data)) {
            item.setData(mContext.getString(R.string.can_not_understand));
        }
        textHolder.receiverMsg.setText(data);
        return convertView;
    }


    private View handleWeather(ConversationItem item, View convertView) {
        WeatherViewHolder holder;
        if (convertView == null) {
            holder = new WeatherViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_assistant_weather, parent, false);
            holder.weatherView = convertView.findViewById(R.id.wv_assistant);
            convertView.setTag(holder);
        } else {
            holder = (WeatherViewHolder) convertView.getTag();
        }

        WeatherInfoV2 infoV2 = (WeatherInfoV2) item.getAttachment();
        if (infoV2 != null) {
            holder.weatherView.setData(infoV2);
        }
        return convertView;
    }


    private View handleTranslation(ConversationItem item, View convertView) {
        TranslationViewHolder holder;
        if (convertView == null) {
            holder = new TranslationViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_translation, parent, false);
            holder.translationView = convertView.findViewById(R.id.tlv_assistant);
            convertView.setTag(holder);
        } else {
            holder = (TranslationViewHolder) convertView.getTag();
        }

        String attachment = (String) item.getAttachment();
        if (attachment != null) {
            holder.translationView.setData(attachment);
        }

        return convertView;
    }

    private View handleLocation(ConversationItem item, View convertView) {
        LocationViewHolder locationHolder;
        if (convertView == null) {
            locationHolder = new LocationViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_location, parent, false);
            locationHolder.address = convertView.findViewById(R.id.tv_location);
            convertView.setTag(locationHolder);
        } else {
            locationHolder = (LocationViewHolder) convertView.getTag();
        }
        final ParserLocation locationInfo = (ParserLocation) item.getAttachment();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMToast.showToast(mContext, "Go to Navigation");
            }
        });
        locationHolder.address.setText(locationInfo.getAreaAddr());
        return convertView;
    }


    private View handleTime(ConversationItem item, View convertView) {
        TimeViewHolder timeHolder;
        if (convertView == null) {
            timeHolder = new TimeViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_time, parent, false);
            timeHolder.timeItemView = convertView.findViewById(R.id.tiv_assistant_time);
            convertView.setTag(timeHolder);
        } else {
            timeHolder = (TimeViewHolder) convertView.getTag();
        }
        String time = (String) item.getAttachment();
        timeHolder.timeItemView.setData(time);
        return convertView;
    }


    private View handleLimit(ConversationItem item, View convertView) {
        LimitViewHolder limitViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_limit, parent, false);
            limitViewHolder = new LimitViewHolder();
            limitViewHolder.limitView = convertView.findViewById(R.id.tiv_assistant_limit);
            convertView.setTag(limitViewHolder);
        } else {
            limitViewHolder = (LimitViewHolder) convertView.getTag();
        }
        limitViewHolder.setData(item);
        return convertView;
    }


    private View handleTraffic(ConversationItem item, View convertView) {
        TrafficViewHolder limitViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_traffic, parent, false);
            limitViewHolder = new TrafficViewHolder();
            limitViewHolder.trafficContent = convertView.findViewById(R.id.tv_assistant_traffic_content);
            convertView.setTag(limitViewHolder);
        } else {
            limitViewHolder = (TrafficViewHolder) convertView.getTag();
        }

        String content = (String) item.getAttachment();
        limitViewHolder.trafficContent.setText(content);
        return convertView;
    }


    private View handleConstellation(ConversationItem item, View convertView) {
        ConstellationHolder constellationHolder;
        if (convertView == null) {
            constellationHolder = new ConstellationHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_constellation, parent, false);
            constellationHolder.horoscopeView = convertView.findViewById(R.id.horoscope);
            convertView.setTag(constellationHolder);
        } else {
            constellationHolder = (ConstellationHolder) convertView.getTag();
        }
        HoroscopeBean horoscopeBean = (HoroscopeBean) item.getAttachment();
        if (horoscopeBean != null)
            constellationHolder.horoscopeView.setData(horoscopeBean);
        return convertView;
    }

    private View handleGas(ConversationItem item, View convertView) {
        GasHolder gasHolder;
        if (convertView == null) {
            gasHolder = new GasHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_gas, parent, false);
            gasHolder.gasView = convertView.findViewById(R.id.gas);
            convertView.setTag(gasHolder);
        } else {
            gasHolder = (GasHolder) convertView.getTag();
        }
        GasPriceBean bean = (GasPriceBean) item.getAttachment();
        if (bean != null) {
            gasHolder.gasView.setData(bean.getGasPrices());
        }
        return convertView;
    }

    private View handleStock(ConversationItem item, View convertView) {
        StockHolder stockHolder;
        if (convertView == null) {
            stockHolder = new StockHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation_stock, parent, false);
            stockHolder.stockView = convertView.findViewById(R.id.stock_view);
            convertView.setTag(stockHolder);
        } else {
            stockHolder = (StockHolder) convertView.getTag();
        }
        stockHolder.setData(item);
        return convertView;
    }

    private class InputViewHolder {
        private TextView input;
    }

    private class ScheduleViewHolder {
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvItem;
    }

    private class TextViewHolder {
        private TextView receiverMsg;
    }


    private class WeatherViewHolder {
        private WeatherView weatherView;
    }


    private class TranslationViewHolder {
        private TranslationView translationView;
    }

    private class LocationViewHolder {
        private TextView address;
    }

    private class TimeViewHolder {
        private TimeItemView timeItemView;
    }

    private class LimitViewHolder {
        private LimitView limitView;

        private void setData(ConversationItem item) {
            if (item == null || item.getAttachment() == null) {
                return;
            }
            LimitInfo limitInfo = (LimitInfo) item.getAttachment();
            if (limitInfo == null) {
                return;
            }
            limitView.setInfo(limitInfo);
        }
    }

    private class TrafficViewHolder {
        private TextView trafficContent;
    }

    private class ConstellationHolder {
        private ViewHoroscope horoscopeView;
    }

    private class GasHolder {
        GasView gasView;
    }

    private class StockHolder {
        StockView stockView;

        private void setData(ConversationItem item) {
            if (item == null || item.getAttachment() == null) {
                return;
            }
//            XfStockBean.StockBean bean = (XfStockBean.StockBean) item.getAttachment();
            NewStockBean.DataBean bean = (NewStockBean.DataBean) item.getAttachment();
            if (stockView != null && bean != null) {
                stockView.setData(bean);
            }
        }
    }

}
