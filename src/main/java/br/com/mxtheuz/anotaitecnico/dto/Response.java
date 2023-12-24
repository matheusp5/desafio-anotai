package br.com.mxtheuz.anotaitecnico.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Response {
    private int code;
    private Object content;
}