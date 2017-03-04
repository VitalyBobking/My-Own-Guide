package diploma.edu.zp.guide_my_own.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import diploma.edu.zp.guide_my_own.adapter.RxErrorHandlingCallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private Retrofit.Builder builder;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    public <S> S createService(Class<S> serviceClass, String api_server) {
        builder = new Retrofit.Builder()
                .baseUrl(api_server)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create());

        return getService(serviceClass);
    }

    private <S> S getService(Class<S> serviceClass) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(loggingInterceptor);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}

