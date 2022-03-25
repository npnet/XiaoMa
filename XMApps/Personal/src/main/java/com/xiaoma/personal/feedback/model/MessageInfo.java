package com.xiaoma.personal.feedback.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Gillben
 * date: 2018/12/05
 */
public class MessageInfo implements Parcelable {

    private int total;
    private int totalPages;
    private int pageSize;
    private int currentPage;
    private List<MessageList> list;
    private int noRead;

    protected MessageInfo(Parcel in) {
        total = in.readInt();
        totalPages = in.readInt();
        pageSize = in.readInt();
        currentPage = in.readInt();
        list = in.createTypedArrayList(MessageList.CREATOR);
        noRead = in.readInt();
    }

    public static final Creator<MessageInfo> CREATOR = new Creator<MessageInfo>() {
        @Override
        public MessageInfo createFromParcel(Parcel in) {
            return new MessageInfo(in);
        }

        @Override
        public MessageInfo[] newArray(int size) {
            return new MessageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeInt(totalPages);
        dest.writeInt(pageSize);
        dest.writeInt(currentPage);
        dest.writeTypedList(list);
        dest.writeInt(noRead);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<MessageList> getList() {
        return list;
    }

    public void setList(List<MessageList> list) {
        this.list = list;
    }

    public int getNoRead() {
        return noRead;
    }

    public void setNoRead(int noRead) {
        this.noRead = noRead;
    }

    public static class MessageList implements Parcelable {
        private int id;
        private String createDate;
        private long uid;
        private String channelId;
        private int questionId;
        private String comments;
        private int isSatisfied;
        private int isRead;
        private int isReview;


        protected MessageList(Parcel in) {
            id = in.readInt();
            createDate = in.readString();
            uid = in.readLong();
            channelId = in.readString();
            questionId = in.readInt();
            comments = in.readString();
            isSatisfied = in.readInt();
            isRead = in.readInt();
            isReview = in.readInt();
        }

        public static final Creator<MessageList> CREATOR = new Creator<MessageList>() {
            @Override
            public MessageList createFromParcel(Parcel in) {
                return new MessageList(in);
            }

            @Override
            public MessageList[] newArray(int size) {
                return new MessageList[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(createDate);
            dest.writeLong(uid);
            dest.writeString(channelId);
            dest.writeInt(questionId);
            dest.writeString(comments);
            dest.writeInt(isSatisfied);
            dest.writeInt(isRead);
            dest.writeInt(isReview);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public int isRead() {
            return isRead;
        }

        public void setRead(int read) {
            isRead = read;
        }

        public int getIsSatisfied() {
            return isSatisfied;
        }

        public void setIsSatisfied(int isSatisfied) {
            this.isSatisfied = isSatisfied;
        }

        public int getIsReview() {
            return isReview;
        }

        public void setIsReview(int isReview) {
            this.isReview = isReview;
        }

    }
}
