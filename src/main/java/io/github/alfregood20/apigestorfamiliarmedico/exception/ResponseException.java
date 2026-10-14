package io.github.alfregood20.apigestorfamiliarmedico.exception;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseException {

    private int status;
    private String error;
    private Object menssaje;
    private String uri;
    private LocalDateTime timestamp;
}
