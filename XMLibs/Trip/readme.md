 1.  RequestHotelsParm requestHotelsParm = new RequestHotelsParm();
        requestHotelsParm.countryId = "0001";
        requestHotelsParm.provinceId = "2000";
        requestHotelsParm.cityId = "2003";
        requestHotelsParm.checkIn = "2018-12-18";
        requestHotelsParm.checkOut = "2018-12-19";
        requestHotelsParm.hotelId = "";
        requestHotelsParm.pageNo = 1;
        requestHotelsParm.pageCount = 2;

        RequestManager.getInstance().getHotelsBaseInfo(requestHotelsParm, new ResultCallback<XMResult<HotelPageDataBean>>() {}
        参数过多的请求封装成bean类直接如上调用接口即可

2.      RequestManager.getInstance().queryOrders("123456", new ResultCallback<XMResult<List<OrdersBean>>>() {}
        参数少的请求，直接传入参数即可，所有出行接口均在RequestManager中