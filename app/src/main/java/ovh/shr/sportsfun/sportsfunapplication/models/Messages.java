package ovh.shr.sportsfun.sportsfunapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Messages implements Parcelable {

    //region Declarations

    @SerializedName("_id")
    private String id;

    //endregion Declarations

    //region Constructor

    //endregion Constructor

    //region Public Methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    //endregion Public Methods

}
