package utills;

import java.io.IOException;
import java.lang.annotation.Annotation;

import model.APIError;
import retrofit2.Converter;
import retrofit2.Response;
import webconnectionhandler.ApiClient;

/**
 * Created by Alok on 29-09-2016.
 */
public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Converter converter =
                ApiClient.getClient()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = (APIError) converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}
