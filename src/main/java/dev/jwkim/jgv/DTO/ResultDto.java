package dev.jwkim.jgv.DTO;

import dev.jwkim.jgv.results.Result;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto<TResult extends Result, TPayload> {
    private TResult result;
    private TPayload payload;
}