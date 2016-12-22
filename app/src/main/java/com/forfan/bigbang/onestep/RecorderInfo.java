//package com.forfan.bigbang.onestep;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.BaseColumns;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecorderInfo {
//    public static final Uri RECORDER_URI = Uri
//            .parse("content://com.smartisanos.recorder.provider/recorderentry/recorder_files");
//
//    //private int mId;
//    private String mPath;
//    /**
//    private double mSize;
//    private String mFormat;
//    private long mSamplingRate;
//    private long mCreateTime;
//    private String mMark;
//    private long mDuration;
//    private String mName;
//    private int mOrder;
//    private boolean mIsHeader = false;
//    */
//
//    public RecorderInfo(Cursor cursor) {
//        //mId = cursor.getInt(cursor.getColumnIndex(Impl._ID));
//        mPath = cursor.getString(cursor.getColumnIndex(Impl.COLUMN_PATH));
//        /*
//        mName = cursor.getString(cursor.getColumnIndex(Impl.COLUMN_NAME));
//        mDuration = cursor.getLong(cursor.getColumnIndex(Impl.COLUMN_DURATION));
//        mCreateTime = cursor.getLong(cursor
//                .getColumnIndex(Impl.COLUMN_CREATE_TIME));
//        mMark = cursor.getString(cursor.getColumnIndex(Impl.COLUMN_MARK));
//        mOrder = cursor.getInt(cursor.getColumnIndex(Impl.COLUMN_ORDER));
//        */
//    }
//
//    public String getPath() {
//        return mPath;
//    }
//
//    public static final class Impl implements BaseColumns {
//        public static final String COLUMN_PATH = "path";
//        public static final String COLUMN_NAME = "name";
//        public static final String COLUMN_FORMAT = "format";
//        public static final String COLUMN_MARK = "mark";
//        public static final String COLUMN_SAMPLING_RATE = "samplingRate";
//        public static final String COLUMN_CREATE_TIME = "createTime";
//        public static final String COLUMN_DURATION = "duration";
//        public static final String COLUMN_ORDER = "recorder_order";
//    }
//
//    public static List<FileInfo> getFileInfoFromRecorder(Context context) {
//        List<FileInfo> ret = new ArrayList<FileInfo>();
//        Cursor cursor = context.getContentResolver().query(RECORDER_URI,
//                new String[] { Impl.COLUMN_PATH }, null, null, null);
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    do {
//                        RecorderInfo ri = new RecorderInfo(cursor);
//                        FileInfo fi = new FileInfo(ri.getPath());
//                        ret.add(fi);
//                    } while (cursor.moveToNext());
//                }
//            } catch (Exception e) {
//                // NA
//            } finally {
//                cursor.close();
//            }
//        }
//        return ret;
//    }
//}
