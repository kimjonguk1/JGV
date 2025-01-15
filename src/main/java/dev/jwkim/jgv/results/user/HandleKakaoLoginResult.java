package dev.jwkim.jgv.results.user;

import dev.jwkim.jgv.results.Result;

public enum HandleKakaoLoginResult implements Result {
    FAILURE_NOT_REGISTERED,
    IS_DELETED,
    IS_SUSPENDED
}
