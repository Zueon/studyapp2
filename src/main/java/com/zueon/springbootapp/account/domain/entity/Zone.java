package com.zueon.springbootapp.account.domain.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Builder @AllArgsConstructor
public class Zone {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = true)
    private String localNameOfCity;

    private String province;

    public static Zone map(String line){
        String[] split = line.split(",");
        Zone zone = new Zone();
        zone.city = split[0];
        zone.localNameOfCity = split[1];;
        zone.province = split[2];

        return zone;
    }

    @Override
    public String toString(){
        return String.format("%s(%s)/%s", city, localNameOfCity, province);
    }

}


