package diploma.edu.zp.guide_my_own.service;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitException extends RuntimeException {
    public RetrofitException(String message, String url, Response response, Kind badAuthenticationData, Object o, Retrofit retrofit) {
        super(message);

        this.url = url;
        this.response = response;
        this.kind = badAuthenticationData;
        this.retrofit = retrofit;
    }

    public static RetrofitException httpError(String url, Response response, Retrofit retrofit) {
        String message = response.code() + " " + response.message();
        return new RetrofitException(message, url, response, Kind.HTTP, null, retrofit);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.NETWORK, exception, null);
    }

    public static RetrofitException jsonSyntaxException(JsonSyntaxException response) {
        return new RetrofitException(response.getMessage(), null, null, Kind.JSON_SYNTAX_EXCEPTION, null, null);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        Log.e("exception", String.valueOf(exception));

        return new RetrofitException(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
    }

    public enum Kind {
        UNEXPECTED,
        NETWORK,
        HTTP,
        JSON_SYNTAX_EXCEPTION
    }

    private final String url;
    private final Response response;
    private final Kind kind;
    private final Retrofit retrofit;


    public String getUrl() {
        return url;
    }

    /** Response object containing status code, headers, body, etc. */
    public Response getResponse() {
        return response;
    }

    /** The event kind which triggered this error. */
    public Kind getKind() {
        return kind;
    }

    /** The Retrofit this request was executed on */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }
}
