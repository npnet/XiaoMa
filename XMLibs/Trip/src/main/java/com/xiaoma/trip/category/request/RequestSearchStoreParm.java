package com.xiaoma.trip.category.request;

import java.io.Serializable;

public class RequestSearchStoreParm  implements Serializable{
    //参数名	           必选         	 类型	       说明
    //query	                是	             string	    查询关键词
    //pos	 city 和 pos参数两个必传一个     string	    经纬度。格式为： 39.994337,116.43323
    //city		                             string	    城市名称,接口1返回的城市名
    //districtid	        否	             string	    城市下区域id,接口2返回的数据
    //cateid	            否	             string	    分类id（一级分类或者二级分类，若不传，默认所以分类）
    //dist	                否	             string	    距离用户的距离。单位 米。(只有传入经纬查询是此字段有效。若不传，默认为5000米)
    //orderType	            否	             string	    排序类型。 1：按照距离 2：按照评分 3：按照平均价格 （默认为 1）
    //orderStandard	        否	             string	    排序标准。 1：按照升序 2：按照降序 。 （默认为 1）
    //limit	                否	             string	    每页显示多少条。（默认为 20。最大传入50）
    //offset                否               string     开始位置
    public String query;
    public String pos;
    public String city;
    public String districtid;
    public String cateid;
    public String dist;
    public String orderType;
    public String orderStandard;
    public String limit;
    public String offset;

}
