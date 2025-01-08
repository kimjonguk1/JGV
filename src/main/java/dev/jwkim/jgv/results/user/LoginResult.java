package dev.jwkim.jgv.results.user;

import dev.jwkim.jgv.results.Result;

public enum LoginResult implements Result {
    FAILURE_NOT_VERIFIED,
    FAILURE_SUSPENDED,
    FAILURE_DUPLICATE_USER,
}
