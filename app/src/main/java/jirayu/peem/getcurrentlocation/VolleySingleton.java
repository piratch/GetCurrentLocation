package jirayu.peem.getcurrentlocation;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class VolleySingleton {
    private RequestQueue requestQueue;
    private Context mContext;



    private static VolleySingleton instance;
    private VolleySingleton() {

        mContext = Contextor.getInstance().getContext();
        requestQueue = Volley.newRequestQueue(mContext);
    }
    public static VolleySingleton getInstance() {
        if (instance == null)
            instance = new VolleySingleton();
        return instance;
    }
    public RequestQueue getRequestQueue()
    {
        return this.requestQueue;
    }


}
