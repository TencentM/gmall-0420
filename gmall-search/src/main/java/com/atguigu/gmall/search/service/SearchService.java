package com.atguigu.gmall.search.service;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseAttrValueVo;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//http://localhost:18086/search?keyword=%E6%89%8B%E6%9C%BA&sort=5&pageNum=1&pageSize=20&brandId=2&cid3=225&props=5:128G-256G-521G&priceFrom=10&priceTo=100000&store=true
@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

//    jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public SearchResponseVo search(SearchParamVo paramVo) {

        try {
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, this.buildDsl(paramVo));
            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            System.out.println(searchResponse);
            SearchResponseVo responseVo = parseResult(searchResponse);
            responseVo.setPageNum(paramVo.getPageNum());
            responseVo.setPageSize(paramVo.getPageSize());

            return responseVo;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SearchResponseVo parseResult(SearchResponse searchResponse) {
        SearchResponseVo responseVo = new SearchResponseVo();
        // 总命中数
        SearchHits hits = searchResponse.getHits();
        responseVo.setTotal(hits.getTotalHits());

        // 获取当前页的记录
        SearchHit[] hitsHits = hits.getHits();
        // 需要把每一个命中对象转化成goods对象
        List<Goods> goodsList = Stream.of(hitsHits).map(hitsHit -> {
            try {
                String json = hitsHit.getSourceAsString();
                Goods goods = MAPPER.readValue(json, Goods.class);
                Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
                HighlightField highlightField = highlightFields.get("title");
                Text[] fragments = highlightField.getFragments();
                goods.setTitle(fragments[0].string());
                return goods;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        responseVo.setGoodsList(goodsList);

        // 获取所有聚合结果集
        Map<String, Aggregation> allAggregationMap = searchResponse.getAggregations().asMap();
        // 解析品牌聚合获取品牌过滤信息
        ParsedLongTerms brandIdAgg = (ParsedLongTerms) allAggregationMap.get("brandIdAgg");

        List<? extends Terms.Bucket> brandBuckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(brandBuckets)){
            // 需要把桶集合转化成品牌集合
            List<BrandEntity> brands = brandBuckets.stream().map(bucket -> {
                BrandEntity brandEntity = new BrandEntity();
                brandEntity.setId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                // 解析子聚合获取品牌名称和logo
                Map<String, Aggregation> subAggregationMap = bucket.getAggregations().asMap();
                ParsedStringTerms brandNameAgg = (ParsedStringTerms) subAggregationMap.get("brandNameAgg");
                List<? extends Terms.Bucket> nameAggBuckets = brandNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(nameAggBuckets)) {
                    brandEntity.setName(nameAggBuckets.get(0).getKeyAsString());
                }
                // 解析品牌logo
                ParsedStringTerms logoAgg = (ParsedStringTerms) subAggregationMap.get("logoAgg");
                List<? extends Terms.Bucket> logoAggBuckets = logoAgg.getBuckets();
                if (!CollectionUtils.isEmpty(logoAggBuckets)) {
                    brandEntity.setLogo(logoAggBuckets.get(0).getKeyAsString());
                }

                return brandEntity;
            }).collect(Collectors.toList());
            responseVo.setBrands(brands);
        }

        // 解析分类的聚合结果集获取分类
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms) allAggregationMap.get("categoryIdAgg");
        List<? extends Terms.Bucket> catBuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(catBuckets)){
            List<CategoryEntity> categoryEntities = catBuckets.stream().map(catBucket -> {
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setId(catBucket.getKeyAsNumber().longValue());
                ParsedStringTerms categoryNameAgg = catBucket.getAggregations().get("categoryNameAgg");
                List<? extends Terms.Bucket> nameAggBuckets = categoryNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(nameAggBuckets)) {
                    categoryEntity.setName(nameAggBuckets.get(0).getKeyAsString());
                }
                return categoryEntity;
            }).collect(Collectors.toList());
            responseVo.setCategories(categoryEntities);
        }

        // 获取规格参数的聚合结果集，解析出规格参数
        ParsedNested attrAgg = (ParsedNested) allAggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(attrIdAggBuckets)){
            List<SearchResponseAttrValueVo> responseAttrValueVos = attrIdAggBuckets.stream().map(bucket -> {
                SearchResponseAttrValueVo responseAttrValueVo = new SearchResponseAttrValueVo();
                responseAttrValueVo.setAttrId(bucket.getKeyAsNumber().longValue());
                // 获取每个规格参数id聚合下的子聚合
                Map<String, Aggregation> subAggregationMap = bucket.getAggregations().asMap();
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) subAggregationMap.get("attrNameAgg");
                List<? extends Terms.Bucket> nameAggBuckets = attrNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(nameAggBuckets)) {
                    responseAttrValueVo.setAttrName(nameAggBuckets.get(0).getKeyAsString());
                }
                // 解析规格参数值
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) subAggregationMap.get("attrValueAgg");
                List<? extends Terms.Bucket> valueAggBuckets = attrValueAgg.getBuckets();
                if (!CollectionUtils.isEmpty(valueAggBuckets)) {
                    responseAttrValueVo.setAttrValues(
                            valueAggBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList())
                    );
                }

                return responseAttrValueVo;
            }).collect(Collectors.toList());
            responseVo.setFilters(responseAttrValueVos);
        }

        return responseVo;
    }

    private SearchSourceBuilder buildDsl(SearchParamVo paramVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String keyword = paramVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            // TODO 此处可打广告
            return sourceBuilder;
        }
        // 1. 构建搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQueryBuilder);
        // 1.1 构建匹配查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));
        // 1.2. 构建过滤条件
        // 1.2.1 品牌过滤
        List<Long> brandId = paramVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
        // 1.2.2 分类过滤
        List<Long> cid3 = paramVo.getCid3();
        if (!CollectionUtils.isEmpty(cid3)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId", cid3));
        }
        // 1.2.3 价格区间过滤
        Double priceFrom = paramVo.getPriceFrom();
        Double priceTo = paramVo.getPriceTo();
        if (priceFrom != null || priceTo != null) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            if (priceFrom != null) {
                rangeQueryBuilder.gte(priceFrom);
            }
            if (priceTo != null) {
                rangeQueryBuilder.lte(priceTo);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        // 1.2.4 库存过滤
        Boolean store = paramVo.getStore();
        if (store != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("store", store));
        }
        // 1.2.5 规格参数嵌套过滤
        List<String> props = paramVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

                String[] attrs = StringUtils.split(":");
                if (attrs != null && attrs.length == 2) {
                    boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrId", attrs[0]));
                    String[] attrValues = StringUtils.split(attrs[1], "-");
                    boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue", attrValues));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs", null, ScoreMode.None));
                }
            });
        }
        // 2 构建排序条件
        Integer sort = paramVo.getSort();
        if (sort != null) {
            switch (sort) {
                case 1:
                    sourceBuilder.sort("price", SortOrder.ASC);
                    break;
                case 2:
                    sourceBuilder.sort("price", SortOrder.DESC);
                    break;
                case 3:
                    sourceBuilder.sort("createTime", SortOrder.DESC);
                    break;
                case 4:
                    sourceBuilder.sort("salse", SortOrder.DESC);
                    break;
                default:
                    sourceBuilder.sort("_score", SortOrder.DESC);
                    break;
            }
        }

        // 3. 构建分页条件
        Integer pageNum = paramVo.getPageNum();
        Integer pageSize = paramVo.getPageSize();
        if (pageNum != null && pageSize != null) {
            sourceBuilder.from((pageNum - 1) * pageSize);
            sourceBuilder.size(pageSize);
        }
        // 4.构建高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
        // 5.构建聚合
        // 5.1 品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo"))
        );
        // 5.2 分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName"))
        );
        // 5.3 规格参数嵌套聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue"))
                )
        );

        // 6. 结果集过滤
        sourceBuilder.fetchSource(new String[]{"skuId","title","subTitle","price","defaultImage"},null);
        System.out.println(sourceBuilder);
        return sourceBuilder;
    }
}
