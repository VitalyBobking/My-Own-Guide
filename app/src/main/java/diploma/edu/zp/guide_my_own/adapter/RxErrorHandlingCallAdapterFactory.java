package diploma.edu.zp.guide_my_own.adapter;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import diploma.edu.zp.guide_my_own.service.RetrofitException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJavaCallAdapterFactory original;

    private RxErrorHandlingCallAdapterFactory() {
        original = RxJavaCallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final Retrofit retrofit;
        private final CallAdapter<?> wrapped;

        RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            Log.e("wrapped", String.valueOf(wrapped));
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call)).onErrorResumeNext
                    (throwable -> Observable.error(asRetrofitException((Throwable) throwable)));
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            Log.e("RetrofitException", String.valueOf(throwable));

            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
            }
            else if (throwable instanceof JsonSyntaxException) {
                return RetrofitException.jsonSyntaxException((JsonSyntaxException) throwable);
            } else if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            return RetrofitException.unexpectedError(throwable);
        }
    }
}
