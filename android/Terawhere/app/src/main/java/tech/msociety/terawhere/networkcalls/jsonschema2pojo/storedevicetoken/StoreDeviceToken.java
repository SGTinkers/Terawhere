package tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreDeviceToken {
    @SerializedName("message")
    @Expose
    String message;
}

/*
{
  "message": "Device token added successfully.",
  "data": {
    "platform": "ios",
    "device_token": "test",
    "user_id": "11c48e41fde34f7183e9088788aaa07e",
    "updated_at": "2017-05-16 16:16:40",
    "created_at": "2017-05-16 16:16:40",
    "id": 1
  }
}
 */