package dev.jwkim.jgv.results.user;

import dev.jwkim.jgv.results.Result;

public enum LoginResult implements Result {
    FAILURE_NOT_VERIFIED,
    FAILURE_SUSPENDED,
    FAILURE_DUPLICATE_USER,
    FAILURE_PASSWORD_MISMATCH,
    FAILURE_ID_MISMATCH,
    FAILURE_BLOCKED_IP,
}
