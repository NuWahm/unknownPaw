package com.seroter.unknownPaw.dto.KakaoMapsDTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GeocodeResponse {
    private List<Document> documents;
    private Meta meta;

    @Getter
    @Setter
    public static class Document {
        private String address_name;
        private String x;
        private String y;
        private String address_type;
        private Address address;
        private RoadAddress road_address;
    }

    @Getter
    @Setter
    public static class Address {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;
    }

    @Getter
    @Setter
    public static class RoadAddress {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String road_name;
        private String underground_yn;
        private String main_building_no;
        private String sub_building_no;
        private String building_name;
        private String zone_no;
    }

    @Getter
    @Setter
    public static class Meta {
        private int total_count;
        private int pageable_count;
        private boolean is_end;
    }
} 