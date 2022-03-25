package com.qiming.fawcard.synthesize.data.entity;

import com.qiming.fawcard.synthesize.base.BaseResponse;

public class DriveScoreCheckAvnResponse extends BaseResponse {

    /**
     * accountVo : {"aid":"85","mobile":"15844232520","idNumber":"230921198706102536","sex":"M","createdTime":1503036637000,"idType":"IDCARD","headPicPath":"https://...../rs//5995424178user/headPicPath/606026184c9c4a5cadb12df1a4b349f9.png","accType":"CAR_OWNER","name":"潘牧"}
     * token : NGtRM1JicjVuS1dFTUhIeFJMeE84czYvVU4yMVNDZDRHdG42bmNUWXBlVHVLUElFdmdiTWRTWTBQa1A4MEg5ZjZTN3BJcnMra3ZOMQpJWHVpVkg5WUVRPT0-___1531382925786___LFPL3APC7H6R60337___7905030SZCD01XM20170707A0009___AVN001___5
     * status : SUCCEED
     */

    private AccountVoBean accountVo;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class AccountVoBean {
        /**
         * aid : 85
         * mobile : 15844232520
         * idNumber : 230921198706102536
         * sex : M
         * createdTime : 1503036637000
         * idType : IDCARD
         * headPicPath : https://...../rs//5995424178user/headPicPath/606026184c9c4a5cadb12df1a4b349f9.png
         * accType : CAR_OWNER
         * name : 潘牧
         */
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
