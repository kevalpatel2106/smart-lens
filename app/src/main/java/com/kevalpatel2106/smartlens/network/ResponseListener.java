package com.kevalpatel2106.smartlens.network;

/**
 * Created by Keval on 01-Jun-17.
 */

public interface ResponseListener {

    void onSuccess(BaseResult result);

    void onError(String message);
}
