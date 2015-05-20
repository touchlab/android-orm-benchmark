package com.littleinc.orm_benchmark.squidb;

import android.content.Context;
import android.util.Log;

import com.littleinc.orm_benchmark.BenchmarkExecutable;
import com.littleinc.orm_benchmark.util.Util;
import com.yahoo.squidb.data.DatabaseDao;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static com.littleinc.orm_benchmark.util.Util.getRandomString;

/**
 * An unoptimized set of SQLite operations for reading and writing the test database objects.
 * <p/>
 * See {@link com.littleinc.orm_benchmark.sqliteoptimized.OptimizedSQLiteExecutor} for optimized
 * versions of these SQLite operations.
 */
public class SquiDbExecutor implements BenchmarkExecutable {

    private static final String TAG = "SquiDbExecutor";
    private DatabaseDao dao;
    private DataBaseHelper database;


    @Override
    public String getOrmName() {
        return "SquiDb";
    }

    @Override
    public void init(Context context, boolean useInMemoryDb) {

        database = new DataBaseHelper(context);
        dao = new DatabaseDao(database);
        database.dropTables();
    }

    @Override
    public long createDbStructure() throws SQLException {

        long start = System.nanoTime();
        database.createTables();
        return System.nanoTime() - start;
    }

    @Override
    public long writeWholeData() throws SQLException {
        List<User> users = new LinkedList<>();
        for (int i = 0; i < NUM_USER_INSERTS; i++) {
            User newUser = new User();
            newUser.setMFirstName(getRandomString(10));
            newUser.setMLastName(getRandomString(10));
            users.add(newUser);
        }

        List<Message> messages = new LinkedList<>();
        for (int i = 0; i < NUM_MESSAGE_INSERTS; i++) {
            Message newMessage = new Message();

            newMessage.setMCommandId((long) i);
            newMessage.setMSortedBy((double) System.nanoTime());
            newMessage.setMContent(Util.getRandomString(100));
            newMessage.setMClientId(System.currentTimeMillis());
            newMessage.setMSenderId(Math.round(Math.random() * NUM_USER_INSERTS));
            newMessage.setMChannelId(Math.round(Math.random() * NUM_MESSAGE_INSERTS));
            newMessage.setMCreatedAt((int) (System.currentTimeMillis() / 1000L));
            messages.add(newMessage);
        }

        long start = System.nanoTime();

        try {
            dao.beginTransaction();

            for (User user : users) {
                dao.persist(user);
            }
            Log.d(TAG, "Done, wrote " + NUM_USER_INSERTS + " users");

            for (Message message : messages) {
                dao.persist(message);
            }
            Log.d(TAG, "Done, wrote " + NUM_MESSAGE_INSERTS + " messages");
            dao.setTransactionSuccessful();
        } finally {
            dao.endTransaction();
        }
        return System.nanoTime() - start;
    }

    @Override
    public long readWholeData() throws SQLException {
        long start = System.nanoTime();
        SquidCursor<Message> cursor = null;
        try {
            Query messageQuery = Query.select(Message.PROPERTIES);
            cursor = dao.query(Message.class, messageQuery);
            List<Message> messages = new LinkedList<>();

            while (cursor != null && cursor.moveToNext()) {
                Message newMessage = new Message();
                newMessage.readPropertiesFromCursor(cursor);
                messages.add(newMessage);
            }

            Log.d(TAG, "Read, " + messages.size() + " rows");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return System.nanoTime() - start;
    }

    @Override
    public long readIndexedField() throws SQLException {
        long start = System.nanoTime();

        SquidCursor<Message> cursor = null;
        try {
            Query messageQuery = Query.select(Message.PROPERTIES)
                    .where(Message.M_COMMAND_ID.eq(LOOK_BY_INDEXED_FIELD));
            cursor = dao.query(Message.class, messageQuery);
            List<Message> messages = new LinkedList<>();

            if (cursor != null) {
                Log.d(TAG, "Read, " + cursor.getCount() + " rows");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return System.nanoTime() - start;
    }

    @Override
    public long readSearch() throws SQLException {
        long start = System.nanoTime();
        SquidCursor<Message> cursor = null;
        try {
            Query messageQuery = Query.select(Message.PROPERTIES)
                    .where(Message.M_CONTENT.like("%" + SEARCH_TERM + "%"))
                    .limit((int) SEARCH_LIMIT);
            cursor = dao.query(Message.class, messageQuery);
            List<Message> messages = new LinkedList<>();

            while (cursor != null && cursor.moveToNext()) {
                Message newMessage = new Message();
                newMessage.readPropertiesFromCursor(cursor);
                messages.add(newMessage);
            }
            Log.d(TAG, "Read, " + messages.size() + " rows");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return System.nanoTime() - start;
    }

    @Override
    public long dropDb() throws SQLException {
        long start = System.nanoTime();
        database.dropTables();
        return System.nanoTime() - start;
    }
}
