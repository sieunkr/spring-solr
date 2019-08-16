package com.example.demo;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SolrQueryTests {

    @Autowired
    private SolrClient solrClient;


    @Test
    public void 기본_쿼리_검색이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.set("q", "name:라떼");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();

        Assert.assertEquals(3, docList.size());
    }


    @Test
    public void 결과_ROW_조정이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("name:라떼"); //query.set("q", "name:라떼");
        query.setRows(2);

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        //SolrDocumentList docList = response.getResults();
        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals(2, coffees.size());
    }


    @Test
    public void 정렬_조정이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("name:라떼"); //query.set("q", "name:라떼");
        query.setRows(2);
        query.setSort("price", SolrQuery.ORDER.desc);

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        //SolrDocumentList docList = response.getResults();
        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals(2, coffees.size());
        Assert.assertEquals("녹차 라떼", coffees.get(0).getName());
    }


    @Test
    public void 구문_쿼리_검색이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("name:녹차 라떼");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals(1, coffees.size());
        Assert.assertEquals("녹차 라떼", coffees.get(0).getName());
    }


    @Test
    public void 범위_쿼리_검색이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("price:[1400 TO 1500]");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals("아이스 라떼", coffees.get(0).getName());


    }


    @Test
    public void 기본_쿼리_AND_검색이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("milk:true AND price:[800 TO 1300]");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals("라떼", coffees.get(0).getName());
    }


    @Test
    public void 기본_쿼리_스코어_부스팅이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.setQuery("name:라떼");

        query.setIncludeScore(true); //fl=*,score
        query.set("defType", "edismax");
        query.set("bq", "category:아이스^2.0");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals("아이스 라떼", coffees.get(0).getName());
    }


    @Test
    public void 기본_쿼리_스코어_부스팅이_잘되는가_2(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.set("q", "name:라떼");
        query.setIncludeScore(true); //fl=*,score

        query.set("defType", "edismax");
        query.set("bq", "price:[1900 TO 2000]^2.0");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals("녹차 라떼", coffees.get(0).getName());
    }



    @Test
    public void 필터쿼리_검색이_잘되는가(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.set("q", "name:라떼");
        query.setIncludeScore(true); //fl=*,score

        query.addFilterQuery("price:[1300 TO 1600]");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        List<Coffee> coffees = response.getBeans(Coffee.class);

        Assert.assertEquals("아이스 라떼", coffees.get(0).getName());
    }



    @Test
    public void 필터쿼리_검색이_잘되는가2(){

        신규문서_색인();

        SolrQuery query = new SolrQuery();
        query.set("q", "name:라떼");
        query.setIncludeScore(true); //fl=*,score

        query.addFilterQuery("category:아이스");

        QueryResponse response = null;
        try {
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        SolrDocumentList docList = response.getResults();

        Assert.assertEquals("아이스 라떼", docList.get(0).get("name"));
    }


    @Test
    public void 문서_삭제(){
        문서_전체_삭제();
    }

    private void 신규문서_색인(){

        try {

            //테스트를 위한 데이터(문서) 이므로 일단 전부 지우고 시작
            문서_전체_삭제();

            SolrInputDocument document01 = new SolrInputDocument();
            //document.addField("id", "456789");
            document01.addField("name", "아메리카노");
            document01.addField("price", "900");
            document01.addField("milk", false);
            solrClient.add(document01);

            SolrInputDocument document = new SolrInputDocument();
            //document.addField("id", "456789");
            document.addField("name", "라떼");
            document.addField("price", "1200");
            document.addField("milk", true);
            solrClient.add(document);

            SolrInputDocument document02 = new SolrInputDocument();
            document02.addField("name", "아이스 라떼");
            document02.addField("price", "1500");
            document02.addField("category", "아이스");
            document02.addField("milk", true);
            solrClient.add(document02);

            SolrInputDocument document03 = new SolrInputDocument();
            document03.addField("name", "녹차 라떼");
            document03.addField("price", "2000");
            document03.addField("milk", true);
            solrClient.add(document03);

            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }


    private void 문서_전체_삭제(){

        try {
            solrClient.deleteByQuery("*");
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

    }






}
