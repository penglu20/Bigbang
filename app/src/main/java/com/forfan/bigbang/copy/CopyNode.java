
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


}
