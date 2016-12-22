//package com.forfan.bigbang.onestep;
//
//import android.content.ComponentName;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ResolveInfo;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.os.Message;
//import android.provider.BaseColumns;
//import android.text.TextUtils;
//import android.util.Pair;
//import android.widget.Toast;
//
//import com.smartisanos.sidebar.R;
//import com.smartisanos.sidebar.util.ResolveInfoGroup.SameGroupComparator;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class ResolveInfoManager extends SQLiteOpenHelper {
//    private volatile static ResolveInfoManager sInstance;
//    public static ResolveInfoManager getInstance(Context context){
//        if(sInstance == null){
//            synchronized(ResolveInfoManager.class){
//                if(sInstance == null){
//                    sInstance = new ResolveInfoManager(context);
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    private static final String DB_NAME ="resolveinfo";
//    private static final int DB_VERSION = 1;
//
//    private static final List<String> sAutoAddPackageList;
//    private static final List<ComponentName> sAutoAddComponentList;
//
//    static {
//        // package
//        sAutoAddPackageList = new ArrayList<String>();
//        sAutoAddPackageList.add("com.sina.weibo");
//        sAutoAddPackageList.add("com.smartisanos.notes");
//        sAutoAddPackageList.add("com.android.email");
//        sAutoAddPackageList.add("com.android.calendar");
//        sAutoAddPackageList.add("com.taobao.taobao");
//        sAutoAddPackageList.add("com.evernote");
//        sAutoAddPackageList.add("com.wunderkinder.wunderlistandroid");
//        sAutoAddPackageList.add("com.android.mms");
//        sAutoAddPackageList.add("com.meitu.meiyancamera");
//        sAutoAddPackageList.add("com.google.android.youtube");
//        sAutoAddPackageList.add("com.facebook.katana");
//        sAutoAddPackageList.add("com.whatsapp");
//        sAutoAddPackageList.add("com.instagram.android");
//        // component
//        sAutoAddComponentList = new ArrayList<ComponentName>();
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI"));
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.qfileJumpActivity"));
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mobileqqi", "com.tencent.mobileqq.activity.JumpActivity"));
//        sAutoAddComponentList.add(new ComponentName("com.tencent.mobileqqi", "com.tencent.mobileqq.activity.qfileJumpActivity"));
//        sAutoAddComponentList.add(new ComponentName("com.twitter.android", "com.twitter.android.composer.ComposerActivity"));
//
//        // check ourself
//        for (ComponentName cn : sAutoAddComponentList) {
//            if (sAutoAddPackageList.contains(cn.getPackageName())) {
//                throw new IllegalArgumentException("auto-add package contains auto-add component !");
//            }
//        }
//    }
//
//    private static final Set<String> sBlackList;
//    private static final Set<Pair<String, String>> sBlackCompList;
//    static {
//        sBlackList = new HashSet<String>();
//        sBlackList.add("com.smartisanos.sidebar");
//        sBlackList.add("com.android.phone");
//        sBlackList.add("com.android.contacts");
//        sBlackList.add("com.android.settings");
//        sBlackList.add("com.android.gallery3d");
//        sBlackList.add("com.android.nfc");
//        sBlackList.add("com.android.bluetooth");
//        sBlackList.add("com.intsig.BizCardReader");
//        sBlackList.add("com.intsig.camscanner");
//        sBlackList.add("com.tmall.wireless");
//        sBlackList.add("com.tencent.qqpimsecure");
//        sBlackList.add("com.eg.android.AlipayGphone");
//        sBlackList.add("com.mobisystems.office");
//        sBlackList.add("com.google.android.apps.maps");
//        sBlackList.add("com.UCMobile");
//        sBlackList.add("com.Qunar");
//        sBlackList.add("com.estrongs.android.pop");
//        sBlackList.add("com.alensw.PicFolder");
//        sBlackList.add("com.douban.frodo");
//        sBlackList.add("it.repix.android");
//        sBlackList.add("com.coolapk.market");
//        sBlackList.add("cn.poco.jane");
//        sBlackList.add("com.google.android.inputmethod.japanese");
//        sBlackList.add("tc.everphoto");
//        sBlackList.add("com.baidu.netdisk");
//        sBlackList.add("com.pinterest");
//        sBlackList.add("com.skype.raider");
//        sBlackList.add("com.wumii.android.mimi");
//        sBlackList.add("com.snapchat.android");
//        sBlackList.add("com.cleanmaster.mguard_cn");
//        sBlackList.add("com.taou.maimai");
//        sBlackList.add("com.instagram.layout");
//        sBlackList.add("com.soundcloud.android");
//        sBlackList.add("com.cyberlink.youcammakeup");
//        sBlackList.add("com.weheartit");
//
//        sBlackCompList = new HashSet<Pair<String, String>>();
//        sBlackCompList.add(new Pair<String, String>("com.tencent.mobileqq", "com.tencent.mobileqq.activity.ContactSyncJumpActivity"));
//        sBlackCompList.add(new Pair<String, String>("com.tencent.mm", "com.tencent.mm.plugin.accountsync.ui.ContactsSyncUI"));
//    }
//
//    public static final String[] ACTIONS = new String[] { Intent.ACTION_SEND,
//            Intent.ACTION_SEND_MULTIPLE };
//
//    private static final int sMaxNumber = 20;
//
//    private Context mContext;
//    private List<ResolveInfoGroup> mList = new ArrayList<ResolveInfoGroup>();
//    private List<ResolveInfoUpdateListener> mListeners = new ArrayList<ResolveInfoUpdateListener>();
//    private Handler mHandler;
//
//    private Toast mLimitToast;
//
//    private ResolveInfoManager(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//        context = context.getApplicationContext();
//        mContext = context;
//        HandlerThread thread = new HandlerThread(ResolveInfoManager.class.getName());
//        thread.start();
//        mHandler = new ResolveInfoManagerHandler(thread.getLooper());
//        mHandler.obtainMessage(MSG_UPDATE_LIST).sendToTarget();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + TABLE_RESOLVEINFO
//                + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + "packagename TEXT," + "names TEXT, " + "weight INTEGER"
//                + ");");
//
//        List<ResolveInfoGroup> rigs = new ArrayList<ResolveInfoGroup>();
//        // pre install part 1, component name
//        for (ComponentName cn : sAutoAddComponentList) {
//            List<ResolveInfoGroup> list = getAllResolveInfoGroupByPackageName(cn.getPackageName());
//            if (list != null) {
//                for (ResolveInfoGroup rig : list) {
//                    if (rig.containsComponent(cn)) {
//                        if (!rigs.contains(rig)) {
//                            rigs.add(rig);
//                        }
//                    }
//                }
//            }
//        }
//
//        // pre install part2, package name
//        for (String packageName : sAutoAddPackageList) {
//            List<ResolveInfoGroup> list = getAllResolveInfoGroupByPackageName(packageName);
//            if (list != null) {
//                rigs.addAll(list);
//            }
//        }
//
//        // update index
//        for (int i = 0; i < rigs.size(); ++i) {
//            rigs.get(i).setIndex(rigs.size() - 1 - i);
//        }
//
//        // add to database
//        for (ResolveInfoGroup rig : rigs) {
//            ContentValues cv = new ContentValues();
//            cv.put(ResolveInfoColumns.PACKAGE_NAME, rig.getPackageName());
//            cv.put(ResolveInfoColumns.COMPONENT_NAMES, rig.getComponentNames());
//            cv.put(ResolveInfoColumns.WEIGHT, rig.getIndex());
//            db.insert(TABLE_RESOLVEINFO, null, cv);
//        }
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // NA
//    }
//
//    public void addListener(ResolveInfoUpdateListener listener){
//        if (listener == null) {
//            return;
//        }
//        if (!mListeners.contains(listener)) {
//            mListeners.add(listener);
//        }
//    }
//
//    public void removeListener(ResolveInfoUpdateListener listener){
//        mListeners.remove(listener);
//    }
//
//    private void notifyUpdate(){
//        for(ResolveInfoUpdateListener li : mListeners){
//            li.onUpdate();
//        }
//    }
//
//    public void delete(ResolveInfoGroup rig) {
//        synchronized (mListeners) {
//            for (int i = 0; i < mList.size(); ++i) {
//                if (mList.get(i).equals(rig)) {
//                    mList.remove(i).newRemoved = true;
//                    notifyUpdate();
//                    mHandler.obtainMessage(MSG_DELETE, rig).sendToTarget();
//                    return;
//                }
//            }
//        }
//    }
//
//    public boolean addResolveInfoGroup(ResolveInfoGroup rig) {
//        return addResolveInfoGroupInternal(rig, true);
//    }
//
//    private boolean addResolveInfoGroupInternal(ResolveInfoGroup rig, boolean showToast) {
//        if (rig == null || rig.size() <= 0) {
//            return false;
//        }
//        synchronized (mList) {
//            if (mList.size() >= sMaxNumber) {
//                if (showToast) {
//                    showToastDueToLimit();
//                }
//                return false;
//            }
//            for (int i = 0; i < mList.size(); ++i) {
//                if (mList.get(i).equals(rig)) {
//                    return true;
//                }
//            }
//            if (mList.size() == 0) {
//                rig.setIndex(0);
//            } else {
//                int maxIndex = mList.get(0).getIndex();
//                for (int i = 1; i < mList.size(); ++i) {
//                    if (mList.get(i).getIndex() > maxIndex) {
//                        maxIndex = mList.get(i).getIndex();
//                    }
//                }
//                rig.setIndex(maxIndex + 1);
//            }
//            rig.newAdded = true;
//            mList.add(0, rig);
//        }
//        mHandler.obtainMessage(MSG_SAVE, rig).sendToTarget();
//        notifyUpdate();
//        return true;
//    }
//
//    public void updateOrder() {
//        synchronized (mList) {
//            Collections.sort(mList, new ResolveInfoGroup.IndexComparator());
//        }
//        notifyUpdate();
//        mHandler.obtainMessage(MSG_SAVE_ORDER).sendToTarget();
//    }
//
//    private void showToastDueToLimit() {
//        if (mLimitToast != null) {
//            mLimitToast.cancel();
//        }
//        mLimitToast = Toast.makeText(mContext,"最多显示",
//                Toast.LENGTH_SHORT);
//        mLimitToast.show();
//    }
//
//    private void saveOrderForList(){
//        List<ResolveInfoGroup> list = getAddedResolveInfoGroup();
//        SQLiteDatabase db = getWritableDatabase();
//        db.beginTransaction();
//        try {
//            for(ResolveInfoGroup rig : list){
//                int id = getId(rig);
//                if(id != 0){
//                    ContentValues cv = new ContentValues();
//                    cv.put(ResolveInfoColumns.PACKAGE_NAME, rig.getPackageName());
//                    cv.put(ResolveInfoColumns.COMPONENT_NAMES, rig.getComponentNames());
//                    cv.put(ResolveInfoColumns.WEIGHT, rig.getIndex());
//                    db.update(TABLE_RESOLVEINFO, cv, ResolveInfoColumns._ID + "=?", new String[] { id + "" });
//                }
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (db != null) {
//                db.endTransaction();
//                db.close();
//            }
//        }
//    }
//
//    private int getId(ResolveInfoGroup rig) {
//        Cursor cursor = null;
//        try {
//            cursor = getReadableDatabase().query(
//                    TABLE_RESOLVEINFO,
//                    null,
//                    ResolveInfoColumns.PACKAGE_NAME + "=? and "
//                            + ResolveInfoColumns.COMPONENT_NAMES + "=?",
//                    new String[] { rig.getPackageName(), rig.getComponentNames() },
//                    null, null, null);
//            if (cursor.moveToFirst()) {
//                return cursor.getInt(cursor.getColumnIndex(ResolveInfoColumns._ID));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        return 0;
//    }
//
//    private void deleteFromDatabase(ResolveInfoGroup rig){
//        int id = getId(rig);
//        if(id != 0){
//            getWritableDatabase().delete(TABLE_RESOLVEINFO,
//                    ResolveInfoColumns._ID + "=?", new String[] { id + "" });
//        }
//    }
//
//    private void saveToDatabase(ResolveInfoGroup rig){
//        ContentValues cv = new ContentValues();
//        cv.put(ResolveInfoColumns.PACKAGE_NAME, rig.getPackageName());
//        cv.put(ResolveInfoColumns.COMPONENT_NAMES, rig.getComponentNames());
//        cv.put(ResolveInfoColumns.WEIGHT, rig.getIndex());
//        getWritableDatabase().insert(TABLE_RESOLVEINFO, null, cv);
//    }
//
//    private void updateComponentList(){
//        List<ResolveInfoGroup> list = new ArrayList<ResolveInfoGroup>();
//        Cursor cursor = null;
//        try {
//            cursor = getReadableDatabase().query(TABLE_RESOLVEINFO, null,null, null, null, null, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    String pkgName = cursor.getString(cursor.getColumnIndex(ResolveInfoColumns.PACKAGE_NAME));
//                    String componentNames = cursor.getString(cursor.getColumnIndex(ResolveInfoColumns.COMPONENT_NAMES));
//                    int weight = cursor.getInt(cursor.getColumnIndex(ResolveInfoColumns.WEIGHT));
//                    ResolveInfoGroup rig = ResolveInfoGroup.fromData(mContext, pkgName, componentNames);
//                    if (rig != null) {
//                        rig.setIndex(weight);
//                        list.add(rig);
//                    } else {
//                        getWritableDatabase().delete(TABLE_RESOLVEINFO, "packagename=? and names=?", new String[] { pkgName, componentNames });
//                    }
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        Collections.sort(list, new ResolveInfoGroup.IndexComparator());
//        synchronized (mList) {
//            mList.clear();
//            mList.addAll(list);
//        }
//        notifyUpdate();
//    }
//
//    public boolean isResolveInfoGroupAdded(ResolveInfoGroup rig) {
//        synchronized (mList) {
//            for (ResolveInfoGroup addedItem : mList) {
//                if (addedItem == rig || addedItem.equals(rig)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public List<ResolveInfoGroup> getAddedResolveInfoGroup() {
//        List<ResolveInfoGroup> ret = new ArrayList<ResolveInfoGroup>();
//        synchronized (mList) {
//            ret.addAll(mList);
//        }
//        return ret;
//    }
//
//    public List<ResolveInfoGroup> getUnAddedResolveInfoGroup(){
//        List<ResolveInfoGroup> ret = getAllResolveInfoGroupByPackageName(null);
//        synchronized (mList) {
//            if (ret != null) {
//                for (int i = 0; i < ret.size(); ++i) {
//                    for (int j = 0; j < mList.size(); ++j)
//                        if (ret.get(i).equals(mList.get(j))) {
//                            ret.remove(i);
//                            i--;
//                            break;
//                        }
//                }
//            }
//        }
//        return ret;
//    }
//
//    public List<ResolveInfoGroup> getAllResolveInfoGroupByPackageName(String pkgName){
//        List<ResolveInfo> allri = getAllResolveInfoByPackageName(pkgName);
//        if(allri == null || allri.size() <= 0){
//            return null;
//        }
//        SameGroupComparator sgc = new SameGroupComparator();
//        Collections.sort(allri, sgc);
//        List<ResolveInfoGroup> ret = new ArrayList<ResolveInfoGroup>();
//        ret.add(new ResolveInfoGroup(mContext));
//        ret.get(0).add(allri.get(0));
//        for(int i = 1; i < allri.size(); ++ i){
//            if(sgc.compare(allri.get(i - 1), allri.get(i)) != 0){
//                ret.add(new ResolveInfoGroup(mContext));
//            }
//            ret.get(ret.size() - 1).add(allri.get(i));
//        }
//        return ret;
//    }
//
//    public List<ResolveInfo> getAllResolveInfoByPackageName(String packageName) {
//        List<ResolveInfo> ret = new ArrayList<ResolveInfo>();
//        for (String action : ACTIONS) {
//            Intent intent = new Intent(action);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.setType("*/*");
//            intent.setPackage(packageName);
//            List<ResolveInfo> infos = mContext.getPackageManager().queryIntentActivities(intent, 0);
//            for (ResolveInfo ri : infos) {
//                if (sBlackList.contains(ri.activityInfo.packageName)) {
//                    // NA
//                } else {
//                    Pair<String, String> comp = new Pair<String, String>(ri.activityInfo.packageName, ri.activityInfo.name);
//                    if (sBlackCompList.contains(comp)) {
//                        // NA
//                    } else {
//                        ret.add(ri);
//                    }
//                }
//            }
//        }
//        ListUtils.getDistinctList(ret, new MyComparator());
//        return ret;
//    }
//
//    public void onPackageRemoved(String packageName){
//        synchronized (mList) {
//            for (int i = 0; i < mList.size(); ++i) {
//                if (mList.get(i).getPackageName().equals(packageName)) {
//                    mHandler.obtainMessage(MSG_DELETE, mList.get(i)).sendToTarget();
//                    mList.remove(i);
//                    i--;
//                }
//            }
//        }
//        notifyUpdate();
//    }
//
//    public void onPackageAdded(String packageName) {
//        List<ResolveInfoGroup> rigList = getAllResolveInfoGroupByPackageName(packageName);
//        if (rigList == null) {
//            // no needed resolveinfo
//            return;
//        }
//        // see component list first
//        boolean addComponent = false;
//        for (int i = 0; i < sAutoAddComponentList.size(); ++i) {
//            ComponentName cn = sAutoAddComponentList.get(i);
//            if (cn.getPackageName().equals(packageName)) {
//                for (ResolveInfoGroup rig : rigList) {
//                    if (rig.containsComponent(cn)) {
//                        addResolveInfoGroupInternal(rig, false);
//                        addComponent = true;
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (!addComponent && sAutoAddPackageList.contains(packageName)) {
//            for (ResolveInfoGroup rig : rigList) {
//                addResolveInfoGroupInternal(rig, false);
//            }
//        }
//    }
//
//    public void onPackageUpdate(String packageName) {
//        if (!TextUtils.isEmpty(packageName)) {
//            boolean changed = false;
//            synchronized (mList) {
//                for (int i = 0; i < mList.size(); ++i) {
//                    ResolveInfoGroup rig = mList.get(i);
//                    if (packageName.equals(rig.getPackageName())) {
//                        if (!rig.isValid()) {
//                            mList.remove(i);
//                            i--;
//                            mHandler.obtainMessage(MSG_DELETE, rig).sendToTarget();
//                            changed = true;
//                        }
//                    }
//                }
//            }
//            if (changed) {
//                notifyUpdate();
//            }
//        }
//    }
//
//    public static class MyComparator implements Comparator<ResolveInfo> {
//        public final int compare(ResolveInfo a, ResolveInfo b) {
//            String pkgA = a.activityInfo.packageName;
//            String pkgB = b.activityInfo.packageName;
//            if (!pkgA.equals(pkgB)) {
//                return pkgA.compareTo(pkgB);
//            }
//            String nameA = a.activityInfo.name;
//            String nameB = b.activityInfo.name;
//            if (!nameA.equals(nameB)) {
//                return nameA.compareTo(nameB);
//            }
//            int iconA = a.getIconResource();
//            int iconB = b.getIconResource();
//            if (iconA != iconB){
//                if(iconA < iconB){
//                    return -1;
//                }else{
//                    return 1;
//                }
//            }else{
//                return 0;
//            }
//        }
//    }
//
//    private static final String TABLE_RESOLVEINFO = "resolveinfo";
//    static class ResolveInfoColumns implements BaseColumns {
//        static final String PACKAGE_NAME = "packagename";
//        static final String COMPONENT_NAMES = "names";
//        static final String WEIGHT = "weight";
//    }
//
//    public interface ResolveInfoUpdateListener{
//        void onUpdate();
//    }
//
//    private static final int MSG_SAVE = 0;
//    private static final int MSG_DELETE = 1;
//    private static final int MSG_UPDATE_LIST = 2;
//    private static final int MSG_SAVE_ORDER = 3;
//    private class ResolveInfoManagerHandler extends Handler {
//        public ResolveInfoManagerHandler(Looper looper) {
//            super(looper, null);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//            case MSG_SAVE:
//                saveToDatabase((ResolveInfoGroup) msg.obj);
//                break;
//            case MSG_DELETE:
//                deleteFromDatabase((ResolveInfoGroup) msg.obj);
//                break;
//            case MSG_UPDATE_LIST:
//                updateComponentList();
//                break;
//            case MSG_SAVE_ORDER:
//                saveOrderForList();
//                break;
//            }
//        }
//    }
//
//    public void onIconChanged(Set<String> packages){
//        synchronized(mList){
//            for(ResolveInfoGroup rig : mList){
//                if(packages.contains(rig.getPackageName())){
//                    rig.onIconChanged();
//                }
//            }
//        }
//        notifyUpdate();
//    }
//}
