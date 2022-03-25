package com.xiaoma.personal.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 8:56
 *       desc：订单列表bean
 * </pre>
 */
public class OrderInfo implements Parcelable {

    private PageInfo pageInfo;
    private List<Order> orders;

    protected OrderInfo(Parcel in) {
        pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        orders = in.createTypedArrayList(Order.CREATOR);
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pageInfo, flags);
        dest.writeTypedList(orders);
    }


    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public static class PageInfo implements Parcelable {
        private int pageNum;
        private int pageSize;
        private int totalRecord;
        private int totalPage;

        protected PageInfo(Parcel in) {
            pageNum = in.readInt();
            pageSize = in.readInt();
            totalRecord = in.readInt();
            totalPage = in.readInt();
        }

        public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
            @Override
            public PageInfo createFromParcel(Parcel in) {
                return new PageInfo(in);
            }

            @Override
            public PageInfo[] newArray(int size) {
                return new PageInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(pageNum);
            dest.writeInt(pageSize);
            dest.writeInt(totalRecord);
            dest.writeInt(totalPage);
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }
    }


    public static class Order implements Parcelable{
        private long id;                    //订单ID
        private String channelId;           //渠道号
        private long createDate;            //创建日期
        private long modifyDate;            //修改日期
        private String type;                //订单类型、酒店Hotel；电影Film等
        private String orderNo;             //小马订单
        private String amount;              //订单价格
        private String ticketNum;           //下单数目 电影票数、房间数
        private String orderId;             //第三方订单
        private String bookPhone;           //下单号
        private String orderName;           //订单显示的抬头名；影片名/酒店名
        private String orderStatus;         //订单状态名称
        private int orderStatusId;          //订单状态code，0已取消  1待确认  2待支付   3已完成  4已删除
        private String orderDate;           //订单消费日期
        private int delFlag;                //删除标识  0正常 1已删除
        private long uid;                   //用户ID
        private String payQrcode;           //支付二维码
        private String voucherValue;        //电影票验证码、序列号、入场码
        private int orderDays;              //酒店预订天数
        private String paySource;           //商城订单支付类型  车币/现金
        private String checkIn;             //入店时间
        private String checkOut;            //离店时间
        private int payStatus;              //支付状态
        private long lastpayDate;           //支付限定时间
        private long currentDate;           //当前网络时间
        private Hotel hotelJsonVo;          //酒店信息
        private Film cinemaJsonVo;          //电影信息

        protected Order(Parcel in) {
            id = in.readLong();
            channelId = in.readString();
            createDate = in.readLong();
            modifyDate = in.readLong();
            type = in.readString();
            orderNo = in.readString();
            amount = in.readString();
            ticketNum = in.readString();
            orderId = in.readString();
            bookPhone = in.readString();
            orderName = in.readString();
            orderStatus = in.readString();
            orderStatusId = in.readInt();
            orderDate = in.readString();
            delFlag = in.readInt();
            uid = in.readLong();
            payQrcode = in.readString();
            voucherValue = in.readString();
            orderDays = in.readInt();
            checkIn = in.readString();
            checkOut = in.readString();
            payStatus = in.readInt();
            lastpayDate = in.readLong();
            currentDate = in.readLong();
            hotelJsonVo = in.readParcelable(Hotel.class.getClassLoader());
            cinemaJsonVo = in.readParcelable(Film.class.getClassLoader());
            paySource = in.readString();
        }

        public static final Creator<Order> CREATOR = new Creator<Order>() {
            @Override
            public Order createFromParcel(Parcel in) {
                return new Order(in);
            }

            @Override
            public Order[] newArray(int size) {
                return new Order[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(channelId);
            dest.writeLong(createDate);
            dest.writeLong(modifyDate);
            dest.writeString(type);
            dest.writeString(orderNo);
            dest.writeString(amount);
            dest.writeString(ticketNum);
            dest.writeString(orderId);
            dest.writeString(bookPhone);
            dest.writeString(orderName);
            dest.writeString(orderStatus);
            dest.writeInt(orderStatusId);
            dest.writeString(orderDate);
            dest.writeInt(delFlag);
            dest.writeLong(uid);
            dest.writeString(payQrcode);
            dest.writeString(voucherValue);
            dest.writeInt(orderDays);
            dest.writeString(checkIn);
            dest.writeString(checkOut);
            dest.writeInt(payStatus);
            dest.writeLong(lastpayDate);
            dest.writeLong(currentDate);
            dest.writeParcelable(hotelJsonVo, flags);
            dest.writeParcelable(cinemaJsonVo, flags);
            dest.writeString(paySource);
        }

        public String getVoucherValue() {
            return voucherValue;
        }

        public void setVoucherValue(String voucherValue) {
            this.voucherValue = voucherValue;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTicketNum() {
            return ticketNum;
        }

        public void setTicketNum(String ticketNum) {
            this.ticketNum = ticketNum;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getBookPhone() {
            return bookPhone;
        }

        public void setBookPhone(String bookPhone) {
            this.bookPhone = bookPhone;
        }

        public String getOrderName() {
            return orderName;
        }

        public void setOrderName(String orderName) {
            this.orderName = orderName;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public int getOrderStatusId() {
            return orderStatusId;
        }

        public void setOrderStatusId(int orderStatusId) {
            this.orderStatusId = orderStatusId;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public int getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(int delFlag) {
            this.delFlag = delFlag;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getPayQrcode() {
            return payQrcode;
        }

        public void setPayQrcode(String payQrcode) {
            this.payQrcode = payQrcode;
        }

        public int getOrderDays() {
            return orderDays;
        }

        public void setOrderDays(int orderDays) {
            this.orderDays = orderDays;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public void setCheckOut(String checkOut) {
            this.checkOut = checkOut;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
        }

        public long getLastpayDate() {
            return lastpayDate;
        }

        public void setLastpayDate(long lastpayDate) {
            this.lastpayDate = lastpayDate;
        }

        public long getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(long currentDate) {
            this.currentDate = currentDate;
        }

        public Hotel getHotelJsonVo() {
            return hotelJsonVo;
        }

        public void setHotelJsonVo(Hotel hotelJsonVo) {
            this.hotelJsonVo = hotelJsonVo;
        }

        public Film getCinemaJsonVo() {
            return cinemaJsonVo;
        }

        public void setCinemaJsonVo(Film cinemaJsonVo) {
            this.cinemaJsonVo = cinemaJsonVo;
        }

        public String getPaySource() {
            return paySource;
        }

        public void setPaySource(String paySource) {
            this.paySource = paySource;
        }

        public static class Hotel implements Parcelable{
            private String id;                  //酒店id
            private String address;             //地址
            private String lat;                 //纬度
            private String lon;                 //经度
            private String roomType;            //房间类型
            private String roomMsg;             //房间信息
            private String canCancel;           //是否可取消
            private String lastCancelDate;      //最后取消时间
            private String mobile;              //酒店号码
            private String hotelName;           //酒店名称
            private String iconUrl;            //icon地址

            protected Hotel(Parcel in) {
                id = in.readString();
                address = in.readString();
                lat = in.readString();
                lon = in.readString();
                roomType = in.readString();
                roomMsg = in.readString();
                canCancel = in.readString();
                lastCancelDate = in.readString();
                mobile = in.readString();
                hotelName = in.readString();
                iconUrl = in.readString();
            }

            public static final Creator<Hotel> CREATOR = new Creator<Hotel>() {
                @Override
                public Hotel createFromParcel(Parcel in) {
                    return new Hotel(in);
                }

                @Override
                public Hotel[] newArray(int size) {
                    return new Hotel[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(address);
                dest.writeString(lat);
                dest.writeString(lon);
                dest.writeString(roomType);
                dest.writeString(roomMsg);
                dest.writeString(canCancel);
                dest.writeString(lastCancelDate);
                dest.writeString(mobile);
                dest.writeString(hotelName);
                dest.writeString(iconUrl);
            }


            public String getIconUrl() {
                return iconUrl;
            }

            public void setIconUrl(String iconUrl) {
                this.iconUrl = iconUrl;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getRoomType() {
                return roomType;
            }

            public void setRoomType(String roomType) {
                this.roomType = roomType;
            }

            public String getRoomMsg() {
                return roomMsg;
            }

            public void setRoomMsg(String roomMsg) {
                this.roomMsg = roomMsg;
            }

            public String getCanCancel() {
                return canCancel;
            }

            public void setCanCancel(String canCancel) {
                this.canCancel = canCancel;
            }

            public String getLastCancelDate() {
                return lastCancelDate;
            }

            public void setLastCancelDate(String lastCancelDate) {
                this.lastCancelDate = lastCancelDate;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getHotelName() {
                return hotelName;
            }

            public void setHotelName(String hotelName) {
                this.hotelName = hotelName;
            }
        }


        public static class Film implements Parcelable{
            private String id;                  //影院id
            private String cinemaName;          //影院名
            private String address;             //地址
            private String lat;                 //纬度
            private String lon;                 //经度
            private String filmType;            //电影类型
            private String seat;                //座位
            private String mobile;              //影院号码
            private String iconUrl;            //icon地址

            protected Film(Parcel in) {
                id = in.readString();
                cinemaName = in.readString();
                address = in.readString();
                lat = in.readString();
                lon = in.readString();
                filmType = in.readString();
                seat = in.readString();
                mobile = in.readString();
                iconUrl = in.readString();
            }

            public static final Creator<Film> CREATOR = new Creator<Film>() {
                @Override
                public Film createFromParcel(Parcel in) {
                    return new Film(in);
                }

                @Override
                public Film[] newArray(int size) {
                    return new Film[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(cinemaName);
                dest.writeString(address);
                dest.writeString(lat);
                dest.writeString(lon);
                dest.writeString(filmType);
                dest.writeString(seat);
                dest.writeString(mobile);
                dest.writeString(iconUrl);
            }

            public String getIconUrl() {
                return iconUrl;
            }

            public void setIconUrl(String iconUrl) {
                this.iconUrl = iconUrl;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCinemaName() {
                return cinemaName;
            }

            public void setCinemaName(String cinemaName) {
                this.cinemaName = cinemaName;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getFilmType() {
                return filmType;
            }

            public void setFilmType(String filmType) {
                this.filmType = filmType;
            }

            public String getSeat() {
                return seat;
            }

            public void setSeat(String seat) {
                this.seat = seat;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }
        }
    }


}
