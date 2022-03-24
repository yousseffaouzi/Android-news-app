package fr.isep.news.NewsAPI;

import fr.isep.news.Model.News;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("top-headlines?country=us")
    Call<News> getNewsByCategory(@Query("category") String category, @Query("apiKey") String apiKey);
}
