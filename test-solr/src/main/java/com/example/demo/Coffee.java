package com.example.demo;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

@Data
public class Coffee {

    @Field
    private String name;
    @Field
    private long price;
    @Field
    private boolean milk;
    @Field
    private String category;
}
