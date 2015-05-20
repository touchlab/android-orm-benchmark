package com.littleinc.orm_benchmark.squidb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yahoo.squidb.data.AbstractDatabase;
import com.yahoo.squidb.sql.Index;
import com.yahoo.squidb.sql.Table;

/**
 * Created by bradleymcdermott on 5/20/15.
 */
public class DataBaseHelper extends AbstractDatabase {
    // DB CONFIG
    private static int DB_VERSION = 1;

    private static String DB_NAME = "squi_db";

    /**
     * Create a new AbstractDatabase
     *
     * @param context the Context, must not be null
     */
    public DataBaseHelper(Context context) {
        super(context);
    }

    @Override
    protected String getName() {
        return DB_NAME;
    }

    @Override
    protected int getVersion() {
        return DB_VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[]{
                // List all tables here
                User.TABLE, Message.TABLE};
    }

    @Override
    protected boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return false;
    }

    @Override
    protected Index[] getIndexes() {
        return new Index[]{new Index("ix_message_command_id", Message.TABLE, false,
                Message.M_COMMAND_ID)};
    }

    public void createTables() {
        for (Table table : getTables()) {
            tryCreateTable(table);
        }

        for (Index index : getIndexes()) {
            tryCreateIndex(index);
        }
    }

    public void dropTables() {
        for (Table table : getTables()) {
            tryDropTable(table);
        }

        for (Index index : getIndexes()) {
            tryDropIndex(index);
        }
    }
}
