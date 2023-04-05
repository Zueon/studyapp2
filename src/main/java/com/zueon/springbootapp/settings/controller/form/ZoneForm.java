package com.zueon.springbootapp.settings.controller.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZoneForm {
    private String zoneName;

    public String getCityName(){
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName(){
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }
}
