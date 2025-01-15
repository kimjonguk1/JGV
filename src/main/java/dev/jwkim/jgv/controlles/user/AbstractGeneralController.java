package dev.jwkim.jgv.controlles.user;

import dev.jwkim.jgv.results.Result;
import org.json.JSONObject;

public abstract class AbstractGeneralController {
    protected final JSONObject generateRestResponse(Result result) {
        JSONObject response = new JSONObject();
        response.put(Result.NAME_SINGULAR, result.nameToLower());
        return response;
    }
}