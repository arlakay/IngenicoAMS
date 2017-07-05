package id.co.octolink.erd.ilm.ingenicoassetsmanagement.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ILM on 6/29/2016.
 */
public class RestApi {
//    public static final String BASE_URL = "http://octolink.co.id/api/ingenicoams/index.php/api/";        //Debugging
    public static final String BASE_URL = "http://182.23.81.179/api/ingenicoams/index.php/api/";        //Production
//    public static final String BASE_URL = "http://10.17.72.77/Ingenico/index.php/api/";        //Local Production


    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
