package com.example.user.lovemessages;

/**
 * Created by User on 23/02/2017.
 */

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token= FirebaseInstanceId.getInstance().getToken();
        luuTokenVaoCSDLRieng(token);
    }

    private void luuTokenVaoCSDLRieng(String token) {
        new FireBaseIDTask().execute(token);
    }
}
