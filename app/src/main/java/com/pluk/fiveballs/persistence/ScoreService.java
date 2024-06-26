package com.pluk.fiveballs.persistence;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ScoreService {

    @GET("topscores")
    Call<List<ScoreData>> topScores(@Query("value") String name, @Query("pagedir") String pagedir);

    @GET("weeklyscores")
    Call<List<ScoreData>> weeklyScores(@Query("value") String name, @Query("pagedir") String pagedir);

    @GET("newscore")
    Call<String> newScore(@Query("name") String name, @Query("score") int score);
}
