package com.littleinc.orm_benchmark.squidb;

import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className = "Message", tableName = "message")
public class MessageSpec {
    public long mId;

    public long mClientId;

    public long mCommandId;

    public double mSortedBy;

    public int mCreatedAt;

    public String mContent;

    public long mSenderId;

    public long mChannelId;
}
