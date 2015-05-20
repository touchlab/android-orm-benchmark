package com.littleinc.orm_benchmark.squidb;

import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className = "User", tableName = "user")
public class UserSpec {
    public long mId;

    public String mLastName;

    public String mFirstName;
}
