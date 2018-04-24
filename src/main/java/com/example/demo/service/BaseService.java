package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.service.dto.SearchDto;
import com.example.demo.service.dto.UserDto;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.cluster.Health;
import io.searchbox.cluster.NodesInfo;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.indices.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dashuai on 2018/1/9.
 */
@Service
public class BaseService {

    @Autowired
    private JestHttpClient jestHttpClient;

    //查询
    public List<User> search(SearchDto searchDto) throws Exception{
        List<User> users = new ArrayList<>();
        String query = "{ \"query\":{\"match\":{\"" + searchDto.getKey() + "\":\""+ searchDto.getValue() + "\"}}}";
        Search search = new Search.Builder(query).addIndex(searchDto.getIndex()).addType(searchDto.getType()).build();
        SearchResult result = jestHttpClient.execute(search);
        List<SearchResult.Hit<User,Void>> hits = result.getHits(User.class);
        for(SearchResult.Hit<User,Void> hit : hits){
            users.add(hit.source);
        }
        return users;
    }

    public List<User> search1(SearchDto searchDto) throws Exception{
        List<User> users = new ArrayList<>();

        //String query = "{ \"query\":{\"match\":{\"" + searchDto.getKey() + "\":\""+ searchDto.getValue() + "\"}}}";
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(searchDto.getKey(),searchDto.getValue()));

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(searchDto.getIndex()).addType(searchDto.getType()).build();
        List<SearchResult.Hit<User,Void>> result = jestHttpClient.execute(search).getHits(User.class);
        for(SearchResult.Hit<User,Void> hit : result){
            users.add(hit.source);
        }
        return users;
    }

    public void commonInsert(SearchDto searchDto) throws Exception{
        long start = new Date().getTime();
        DeleteIndex deleteIndex = new DeleteIndex.Builder(searchDto.getIndex()).build();
        jestHttpClient.execute(deleteIndex);
        CreateIndex createIndex = new CreateIndex.Builder(searchDto.getIndex()).build();
        jestHttpClient.execute(createIndex);
        for(int i=0;i<5000;i++){
            User user = new User("男神" + i,"1" + i,"亚洲-中国-华东-上海-青浦-华新镇-嘉松中路-" + i + "号");
            Index index = new Index.Builder(user).index(searchDto.getIndex()).type(searchDto.getType()).id("1" + i).build();
            jestHttpClient.execute(index);
        }
        long end = new Date().getTime();
        System.out.println("简单插入花费的时间:" + (end-start) + "ms");
    }

    public void bulkInsert(SearchDto searchDto) throws Exception{
        long start = new Date().getTime();
        DeleteIndex deleteIndex = new DeleteIndex.Builder(searchDto.getIndex()).build();
        jestHttpClient.execute(deleteIndex);
        CreateIndex createIndex = new CreateIndex.Builder(searchDto.getIndex()).build();
        jestHttpClient.execute(createIndex);
        Bulk.Builder bulk = new Bulk.Builder();
        for(int i=0;i<2000;i++){
            User user = new User("男神" + i,"1" + i,"亚洲-中国-华东-上海-青浦-华新镇-嘉松中路-" + i + "号");
            Index index = new Index.Builder(user).index(searchDto.getIndex()).type(searchDto.getType()).id("1" + i).build();
            bulk.addAction(index);
        }
        jestHttpClient.execute(bulk.build());
        long end = new Date().getTime();
        System.out.println("使用bulk插入花费的时间:" + (end-start) + "ms");
    }

    public void updateByDoc(UserDto userDto) throws Exception{
        String script = "{" +
                "    \"doc\" : {" +
                "        \"name\" : \"" + userDto.getName() + "\"," +
                "        \"age\" : \""+ userDto.getAge()+"\"," +
                "        \"address\": \""+ userDto.getAddress() + "\"" +
                "    }" +
                "}";
        Update update = new Update.Builder(script).index(userDto.getIndex()).type(userDto.getType()).id(userDto.getId()).build();
        jestHttpClient.execute(update);
    }

    public void others(String indexname,String typename,String id,String script){
        OpenIndex openIndex = new OpenIndex.Builder(indexname).build();     //打开索引
        CloseIndex closeIndex = new CloseIndex.Builder(indexname).build();  //关闭索引
        Optimize optimize = new Optimize.Builder().build();              //优化索引
        Flush flush = new Flush.Builder().build();                     //刷新索引
        //判断单个/多个索引是否存在
        IndicesExists indicesExists = new IndicesExists.Builder(indexname).build();
        NodesInfo nodesInfo = new NodesInfo.Builder().build();    //查询节点信息
        Health health = new Health.Builder().build();     //查看集群健康信息

        Index index = new Index.Builder(script).index(indexname).type(typename).build();

        //新增索引(有) 新增文档(无)
        CreateIndex createIndex = new CreateIndex.Builder(indexname).build();

        //修改索引(无) 修改文档(有)
        Update update = new Update.Builder(script).index(indexname).type(typename).id(id).build();

        //删除索引(有) 删除文档(有)
        DeleteIndex deleteIndex = new DeleteIndex.Builder(indexname).build();
        Delete delete = new Delete.Builder(id).index(indexname).type(typename).build();
    }

    public void simpleaggs(SearchDto searchDto) throws Exception{
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.aggregation(AggregationBuilders.terms("pop_color").field("color"));
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(searchDto.getIndex()).addType(searchDto.getType()).build();
        SearchResult result = jestHttpClient.execute(search);
        List<TermsAggregation.Entry> nameAgg = result.getAggregations().getTermsAggregation("pop_color").getBuckets();
        for(TermsAggregation.Entry name : nameAgg){
            System.out.println("key:" + name.getKey() +  "------------ count:" + name.getCount());
        }
    }

    //聚合 index --- "cars" , type ---  "usa"
    public void aggs(SearchDto searchDto) throws Exception{
        //1、查询语句(因为聚合是在查询结果的基础上进行的)
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //2、聚合语句
//        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("pop_color").field("color")
//                .subAggregation(AggregationBuilders.terms("factory").field("make")
//                        .subAggregation(AggregationBuilders.min("min_price").field("price"))
//                        .subAggregation(AggregationBuilders.max("max_price").field("price"))
//                );
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("pop_color").field("color")
                .subAggregation(AggregationBuilders.terms("factory").field("make"))
                .subAggregation(AggregationBuilders.min("min_price").field("price"))
                .subAggregation(AggregationBuilders.max("max_price").field("price"));
        searchSourceBuilder.aggregation(aggregationBuilder);

        //3、在哪个索引、哪个type下进行操作
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(searchDto.getIndex()).addType(searchDto.getType()).build();
        SearchResult result = jestHttpClient.execute(search);

        //4.1、获取第一层的桶(桶是terms产生的，只要是terms语句，就能产生桶)
        List<TermsAggregation.Entry> aggs = result.getAggregations().getTermsAggregation("pop_color").getBuckets();
//        for(TermsAggregation.Entry entry : aggs){
//            List<TermsAggregation.Entry> aggs1 = entry.getTermsAggregation("factory").getBuckets();
//            System.out.println("key:" + entry.getKey() + " count:" + entry.getCount());
//            for(TermsAggregation.Entry entry1 : aggs1){
//                System.out.println("--> factory:" + entry1.getKey()
//                        + " min_price:" + entry1.getMinAggregation("min_price").getMin()
//                        + " max_price:" + entry1.getMaxAggregation("max_price").getMax());
//            }
//        }
        //4.2、遍历第一层桶,判断里面是否还有桶，如果还有桶，继续遍历
        for(TermsAggregation.Entry entry : aggs){
            List<TermsAggregation.Entry> aggs1 = entry.getTermsAggregation("factory").getBuckets();
            System.out.println("min_price:" + entry.getMinAggregation("min_price").getMin() + " max_price:" + entry.getMaxAggregation("max_price").getMax());
            for(TermsAggregation.Entry entry1 : aggs1){
                System.out.println("--> key:" + entry1.getKey() + " count:" + entry1.getCount());
            }
        }
    }
}
