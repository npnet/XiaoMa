package com.xiaoma.trip.movie.response;

import java.io.Serializable;
import java.util.List;

/**
 * 座位信息
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class HallSeatsInfoBean implements Serializable{

    /**
     * seats : [{"sectionId":"","sectionName":"","axis":"0","seatId":"2482","seatRow":"1","seatCol":"17","graphRow":"1","graphCol":"2","seatType":"0","state":"1"},{"sectionId":"","sectionName":"","axis":"1","seatId":"2483","seatRow":"1","seatCol":"16","graphRow":"1","graphCol":"3","seatType":"0","state":"1"}]
     * min_Col_Num : 1
     * max_Row_Num : 11
     * min_Row_Num : 1
     * max_Col_Num : 20
     */

    private int min_Col_Num;
    private int max_Row_Num;
    private int min_Row_Num;
    private int max_Col_Num;
    private List<SeatsBean> seats;

    public int getMin_Col_Num() {
        return min_Col_Num;
    }

    public void setMin_Col_Num(int min_Col_Num) {
        this.min_Col_Num = min_Col_Num;
    }

    public int getMax_Row_Num() {
        return max_Row_Num;
    }

    public void setMax_Row_Num(int max_Row_Num) {
        this.max_Row_Num = max_Row_Num;
    }

    public int getMin_Row_Num() {
        return min_Row_Num;
    }

    public void setMin_Row_Num(int min_Row_Num) {
        this.min_Row_Num = min_Row_Num;
    }

    public int getMax_Col_Num() {
        return max_Col_Num;
    }

    public void setMax_Col_Num(int max_Col_Num) {
        this.max_Col_Num = max_Col_Num;
    }

    public List<SeatsBean> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatsBean> seats) {
        this.seats = seats;
    }

    public static class SeatsBean implements Serializable {
        /**
         * sectionId :
         * sectionName :
         * axis : 0
         * seatId : 2482
         * seatRow : 1
         * seatCol : 17
         * graphRow : 1
         * graphCol : 2
         * seatType : 0
         * state : 1
         */

        private String sectionId;
        private String sectionName;
        private String axis;
        private String seatId;
        private String seatRow;
        private String seatCol;
        private String graphRow;
        private String graphCol;
        private String seatType;
        private String state;

        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getAxis() {
            return axis;
        }

        public void setAxis(String axis) {
            this.axis = axis;
        }

        public String getSeatId() {
            return seatId;
        }

        public void setSeatId(String seatId) {
            this.seatId = seatId;
        }

        public String getSeatRow() {
            return seatRow;
        }

        public void setSeatRow(String seatRow) {
            this.seatRow = seatRow;
        }

        public String getSeatCol() {
            return seatCol;
        }

        public void setSeatCol(String seatCol) {
            this.seatCol = seatCol;
        }

        public String getGraphRow() {
            return graphRow;
        }

        public void setGraphRow(String graphRow) {
            this.graphRow = graphRow;
        }

        public String getGraphCol() {
            return graphCol;
        }

        public void setGraphCol(String graphCol) {
            this.graphCol = graphCol;
        }

        public String getSeatType() {
            return seatType;
        }

        public void setSeatType(String seatType) {
            this.seatType = seatType;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
