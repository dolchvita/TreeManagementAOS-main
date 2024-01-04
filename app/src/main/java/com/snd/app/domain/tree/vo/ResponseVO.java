package com.snd.app.domain.tree.vo;

import lombok.Data;

@Data
public class ResponseVO {
    private String result;
    private String message;
    private Object data;
}
