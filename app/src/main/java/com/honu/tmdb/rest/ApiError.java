package com.honu.tmdb.rest;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 */
public class ApiError {

    private int statusCode;

    private String reason;


    public ApiError(int statusCode, String reason, Exception exception) {
        this.statusCode = statusCode;
        this.reason = reason;
    }


    public ApiError(RetrofitError error) {
        Response response = error.getResponse();
        if (response != null) {
            this.statusCode = response.getStatus();
            this.reason = response.getReason();
        }
    }


    public int getStatusCode() {
        return statusCode;
    }


    public String getReason() {
        return reason;
    }
}
