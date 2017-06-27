package com.mobiledevpro.youtubeauth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RestAPI client with Retrofit
 * <p>
 * Created by Dmitriy V. Chernysh on 25.01.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * www.mobile-dev.pro
 */

class RestClient {
    private static final String BASE_URL = "https://www.googleapis.com";

    private static final int HTTP_TIMEOUT = 25; //in seconds

    private static RestClient sRestClient;
    private IRestClient mApiInterface;
    private Retrofit mRetrofit;

    interface ICallBacks {
        void onSuccess(int respCode, Object respBody);

        void onFail(String errMessage);
    }

    private RestClient(Context appContext) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS);

        //когда логирование включено не будет отображаться прогресс загрузки файла!!!
        //for loggining -->

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        //<!-- for loggining

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        builder.client(httpClient.build())
                .build();

        mRetrofit = builder.build();
        mApiInterface = mRetrofit.create(IRestClient.class);
    }

    static synchronized RestClient getInstance(Context appContext) {
        if (sRestClient == null) {
            sRestClient = new RestClient(appContext);
        }

        return sRestClient;
    }

    void exchangeCodeForTokenAsync(AccessToken.Exchange.Request request, final ICallBacks callBacks) {
        Call<AccessToken.Exchange.Response> call = mApiInterface.getTokens(request.getQueryParams());

        Callback<AccessToken.Exchange.Response> callback = new Callback<AccessToken.Exchange.Response>() {
            @Override
            public void onResponse(Call<AccessToken.Exchange.Response> call, Response<AccessToken.Exchange.Response> response) {
                callBacks.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<AccessToken.Exchange.Response> call, Throwable t) {
                String errMsg = t.getMessage();
                if (TextUtils.isEmpty(errMsg)) {
                    errMsg = "Network error";
                }
                callBacks.onFail(errMsg);
            }
        };
        call.enqueue(callback);
    }

    void refreshAccessTokenAsync(AccessToken.Refresh.Request request, final ICallBacks callBacks) {
        Call<AccessToken.Refresh.Response> call = mApiInterface.refreshAccessToken(request.getQueryParams());

        Callback<AccessToken.Refresh.Response> callback = new Callback<AccessToken.Refresh.Response>() {
            @Override
            public void onResponse(Call<AccessToken.Refresh.Response> call, Response<AccessToken.Refresh.Response> response) {
                callBacks.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<AccessToken.Refresh.Response> call, Throwable t) {
                String errMsg = t.getMessage();
                if (TextUtils.isEmpty(errMsg)) {
                    errMsg = "Network error";
                }
                callBacks.onFail(errMsg);
            }
        };
        call.enqueue(callback);
    }


    /**
     * Method for checking network connection
     *
     * @param context - application context
     * @return true - device online
     */
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}