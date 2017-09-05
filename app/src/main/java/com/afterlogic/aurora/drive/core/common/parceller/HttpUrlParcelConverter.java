package com.afterlogic.aurora.drive.core.common.parceller;

import android.os.Parcel;

import org.parceler.ParcelConverter;

import okhttp3.HttpUrl;

/**
 * Created by sunny on 31.08.17.
 */

public class HttpUrlParcelConverter implements ParcelConverter<HttpUrl> {

    @Override
    public void toParcel(HttpUrl input, Parcel parcel) {
        parcel.writeByte(input == null ? (byte) 0 : 1);
        if (input != null) {
            parcel.writeString(input.toString());
        }
    }

    @Override
    public HttpUrl fromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) return null;
        return HttpUrl.parse(parcel.readString());
    }

}
