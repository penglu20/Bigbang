
package com.forfan.bigbang.copy;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

public class CopyNode implements Parcelable {
    public static Creator<CopyNode> CREATOR = new Creator<CopyNode>() {

        @Override
        public CopyNode createFromParcel(Parcel source) {
            return new CopyNode(source);
        }

        @Override
        public CopyNode[] newArray(int size) {
            return new CopyNode[size];
        }
    };

    private Rect bound;
    private String content;

    public CopyNode(Rect var1, String var2) {
        this.bound = var1;
        this.content = var2;
    }

    public CopyNode(Parcel var1) {
        this.bound = new Rect(var1.readInt(), var1.readInt(), var1.readInt(), var1.readInt());
        this.content = var1.readString();
    }

    public long caculateSize() {
        return (long)(this.bound.width() * this.bound.height());
    }

    public Rect getBound() {
        return this.bound;
    }

    public String getContent() {
        return this.content;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeInt(this.bound.left);
        var1.writeInt(this.bound.top);
        var1.writeInt(this.bound.right);
        var1.writeInt(this.bound.bottom);
        var1.writeString(this.content);
    }

    @Override
    public String toString() {
        return "CopyNode{" +
                "bound=" + bound +
                ", content='" + content + '\'' +
                '}';
    }

//    public static byte[] parseArrayListToByte(ArrayList<CopyNode> nodes) throws IOException {
//
//        ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
//        ObjectOutputStream byteArrayOutputStream=new ObjectOutputStream(byteArray);
//        byteArrayOutputStream.writeInt(nodes.size());
//        for (int i=0;i<nodes.size();i++){
//            Parcel parcel=Parcel.obtain();
//            nodes.get(i).writeToParcel(parcel, 0);
//            byteArrayOutputStream.writeInt(parcel.dataSize());
//            parcel.setDataPosition(0);
//            byteArrayOutputStream.write(parcel.marshall());
//        }
//        byteArrayOutputStream.flush();
//        return byteArray.toByteArray();
//    }
//
//
//    public static ArrayList<CopyNode> parseByteToArrayList(byte[] byteStream) throws IOException {
//
//        ByteArrayInputStream byteArray=new ByteArrayInputStream(byteStream);
//        ObjectInputStream byteArrayOutputStream=new ObjectInputStream(byteArray);
//        int length = byteArrayOutputStream.readInt();
//        ArrayList<CopyNode> result=new ArrayList<>();
//        for (int i=0;i<length;i++){
//            int parcelLength = byteArrayOutputStream.readInt();
//            byte[] buffer=new byte[parcelLength];
//            byteArrayOutputStream.read(buffer);
//            Parcel parcel = Parcel.obtain();
//            parcel.unmarshall(buffer, 0, buffer.length);
//            parcel.setDataPosition(0);
//            result.add(CopyNode.CREATOR.createFromParcel(parcel));
//        }
//        return result;
//    }
}
