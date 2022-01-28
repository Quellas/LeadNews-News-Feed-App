package com.itheima.mail.pojo;

import lombok.Data;

@Data
public class AlarmDTO {
    private Integer scopeId;
    private String scope;
    private String name;
    private String id0;
    private String id1;
    private String ruleName;
    private String alarmMessage;
    private Long startTime;
}