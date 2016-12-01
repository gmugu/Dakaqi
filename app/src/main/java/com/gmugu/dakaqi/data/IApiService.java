package com.gmugu.dakaqi.data;

import com.gmugu.dakaqi.model.GameInfoResult;
import com.gmugu.dakaqi.model.MarkResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mugu on 16/11/24.
 */

public interface IApiService {

    /**
     * 初始化比赛数据
     *
     * @param password 密码
     * @return
     */
    @GET("initGame")
    Call<GameInfoResult> initGame(@Query("password") String password);

    /**
     * 上传每次打卡的数据
     *
     * @param data
     * @return
     */
    @GET("runner")
    Call<MarkResult> mark(@Query("data") String data);
}
