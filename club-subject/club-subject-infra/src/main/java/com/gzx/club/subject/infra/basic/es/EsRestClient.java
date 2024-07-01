package com.gzx.club.subject.infra.basic.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @program: club
 * @description: ES通信客户端
 * @author: gzx
 * @create: 2024-06-30
 **/
@Component
@Slf4j
public class EsRestClient {

    private static Map<String, RestHighLevelClient> clientMap = new HashMap<>();

    @Resource
    private EsConfigProperties esConfigProperties;

    private static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @PostConstruct
    public void initClients() {
        List<EsClusterConfig> esClusterConfigList = esConfigProperties.getEsConfigs();
        for (EsClusterConfig esClusterConfig : esClusterConfigList) {
            log.info("EsRestClient.initClients.config.name: {}, node: {}", esClusterConfig.getName(), esClusterConfig.getNodes());
            RestHighLevelClient restHighLevelClient = initClient(esClusterConfig);
            clientMap.put(esClusterConfig.getName(), restHighLevelClient);
        }
    }

    private RestHighLevelClient initClient(EsClusterConfig esClusterConfig) {
        String[] nodes = esClusterConfig.getNodes().split(",");
        List<HttpHost> httpHostList = new ArrayList<>(nodes.length);

        for (String node : nodes) {
            String[] ipPort = node.split(":");
            if (ipPort.length == 2) {
                HttpHost httpHost = new HttpHost(ipPort[0], NumberUtils.toInt(ipPort[1]));
                httpHostList.add(httpHost);
            }
        }
        HttpHost[] httpHosts = httpHostList.toArray(new HttpHost[0]);

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(httpHosts));
        return client;
    }

    private static RestHighLevelClient getClient(String clusterName) {
        return clientMap.get(clusterName);
    }

    /**
     * @Author: gzx
     *
     * @Description: 插入文档
     * @Date: 2024-06-30
     */
    public static boolean insertDoc(EsIndexInfo esIndexInfo, EsSourceData esSourceData) {
        try {
            IndexRequest indexRequest = new IndexRequest(esIndexInfo.getIndexName());
            indexRequest.source(esSourceData.getData());
            indexRequest.id(esSourceData.getDocId());
            getClient(esIndexInfo.getClusterName()).index(indexRequest, COMMON_OPTIONS);
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.insertDoc.exception: {}", e.getMessage(), e);
            return false;
        }
    }

    public static boolean insertDocBatch(EsIndexInfo esIndexInfo, List<EsSourceData> esSourceDataList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            boolean flag = false;
            for (EsSourceData esSourceData : esSourceDataList) {
                String docId = esSourceData.getDocId();
                if (StringUtils.isNotBlank(docId)) {
                    IndexRequest indexRequest = new IndexRequest(esIndexInfo.getIndexName());
                    indexRequest.id(esSourceData.getDocId());
                    indexRequest.source(esSourceData.getData());
                    bulkRequest.add(indexRequest);
                    flag = true;
                }
            }
            if (flag) {
                BulkResponse bulk = getClient(esIndexInfo.getClusterName()).bulk(bulkRequest, COMMON_OPTIONS);
                return !bulk.hasFailures();
            }
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.insertDocBatch.exception: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * @Author: gzx
     *
     * @Description: 更新文档
     * @Date: 2024-06-30
     */
    public static boolean updateDoc(EsIndexInfo esIndexInfo, EsSourceData esSourceData) {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(esIndexInfo.getIndexName());
            updateRequest.id(esSourceData.getDocId());
            updateRequest.doc(esSourceData.getData());
            getClient(esIndexInfo.getClusterName()).update(updateRequest, COMMON_OPTIONS);
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.updateDoc.exception: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * @Author: gzx
     *
     * @Description: 根据条件更新文档
     * @Date: 2024-06-30
     */
    public static boolean updateByQuery(EsIndexInfo esIndexInfo, QueryBuilder queryBuilder, Script script, int batchSize) {
        try {
            UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(esIndexInfo.getIndexName());
            updateByQueryRequest.setQuery(queryBuilder);
            updateByQueryRequest.setScript(script);
            updateByQueryRequest.setBatchSize(batchSize);
            updateByQueryRequest.setAbortOnVersionConflict(false);
            BulkByScrollResponse response = getClient(esIndexInfo.getClusterName()).updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
            List<BulkItemResponse.Failure> failures = response.getBulkFailures();
        } catch (Exception e) {
            log.error("EsRestClient.updateByQuery.exception: {}", e.getMessage(), e);
        }
        return true;
    }

    /**
     * @Author: gzx
     *
     * @Description: 批量更新文档
     * @Date: 2024-06-30
     */
    public static boolean updateDocBatch(EsIndexInfo esIndexInfo, List<EsSourceData> esSourceDataList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            boolean flag = false;
            for (EsSourceData esSourceData : esSourceDataList) {
                String docId = esSourceData.getDocId();
                if (StringUtils.isNotBlank(docId)) {
                    UpdateRequest updateRequest = new UpdateRequest();
                    updateRequest.index(esIndexInfo.getIndexName());
                    updateRequest.id(esSourceData.getDocId());
                    updateRequest.doc(esSourceData.getData());
                    bulkRequest.add(updateRequest);
                    flag = true;
                }
            }
            if (flag) {
                BulkResponse bulk = getClient(esIndexInfo.getClusterName()).bulk(bulkRequest, COMMON_OPTIONS);
                return !bulk.hasFailures();
            }
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.updateDocBatch.exception: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * @Author: gzx
     *
     * @Description: 删除所有文档
     * @Date: 2024-06-30
     */
    public static boolean deleteAllDoc(EsIndexInfo esIndexInfo) {
        try {
            DeleteByQueryRequest deleteByQueryRequest =
                    new DeleteByQueryRequest(esIndexInfo.getIndexName());
            deleteByQueryRequest.setQuery(QueryBuilders.matchAllQuery());
            BulkByScrollResponse response = getClient(esIndexInfo.getClusterName()).deleteByQuery(
                    deleteByQueryRequest, COMMON_OPTIONS
            );
            long deleted = response.getDeleted();
            log.info("EsRestClient.deleteAllDoc.deleted.size:{}", deleted);
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.deleteAllDoc.exception:{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * @Author: gzx
     *
     * @Description: 根据id删除文档
     * @Date: 2024-06-30
     */
    public static boolean deleteDoc(EsIndexInfo esIndexInfo, String docId) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(esIndexInfo.getIndexName());
            deleteRequest.id(docId);
            DeleteResponse response = getClient(esIndexInfo.getClusterName()).delete(deleteRequest, COMMON_OPTIONS);
            log.info("EsRestClient.deleteDoc.response:{}", JSON.toJSONString(response));
            return true;
        } catch (Exception e) {
            log.error("EsRestClient.deleteDoc.exception:{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * @Author: gzx
     *
     * @Description: 根据id判断文档是否存在
     * @Date: 2024-06-30
     */
    public static boolean isExistDoc(EsIndexInfo esIndexInfo, String docId) {
        try {
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());
            getRequest.id(docId);
            return getClient(esIndexInfo.getClusterName()).exists(getRequest, COMMON_OPTIONS);
        } catch (Exception e) {
            log.error("EsRestClient.isExistDoc.exception:{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * @Author: gzx
     *
     * @Description: 根据id获取文档
     * @Date: 2024-06-30
     */
    public static Map<String, Object> getDocById(EsIndexInfo esIndexInfo, String docId) {
        try {
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());
            getRequest.id(docId);
            GetResponse response = getClient(esIndexInfo.getClusterName()).get(getRequest, COMMON_OPTIONS);
            Map<String, Object> source = response.getSource();
            return source;
        } catch (Exception e) {
            log.error("EsRestClient.getDocById.exception:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * @Author: gzx
     *
     * @Description: 根据id获取文档的某些字段
     * @Date: 2024-06-30
     */
    public static Map<String, Object> getDocById(EsIndexInfo esIndexInfo, String docId,
                                                 String[] fields) {
        try {
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());
            getRequest.id(docId);
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, fields, null);
            getRequest.fetchSourceContext(fetchSourceContext);
            GetResponse response = getClient(esIndexInfo.getClusterName()).get(getRequest, COMMON_OPTIONS);
            Map<String, Object> source = response.getSource();
            return source;
        } catch (Exception e) {
            log.error("EsRestClient.getDocById.exception:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * @Author: gzx
     *
     * @Description: term查询
     * @Date: 2024-06-30
     */
    public static SearchResponse searchWithTermQuery(EsIndexInfo esIndexInfo,
                                                     EsSearchRequest esSearchRequest) {
        try {
            BoolQueryBuilder bq = esSearchRequest.getBq();
            String[] fields = esSearchRequest.getFields();
            int from = esSearchRequest.getFrom();
            int size = esSearchRequest.getSize();

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(bq);
            searchSourceBuilder.fetchSource(fields, null).from(from).size(size);

            HighlightBuilder highlightBuilder = esSearchRequest.getHighlightBuilder();
            if (Objects.nonNull(highlightBuilder)) {
                searchSourceBuilder.highlighter(highlightBuilder);
            }

            String sortName = esSearchRequest.getSortName();
            if (Objects.nonNull(sortName)) {
                searchSourceBuilder.sort(sortName);
            }

            SortOrder sortOrder = esSearchRequest.getSortOrder();
            if (Objects.nonNull(sortOrder)) {
                searchSourceBuilder.sort(new ScoreSortBuilder().order(sortOrder));
            }
            else {
                searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
            }

            SearchRequest searchRequest = new SearchRequest();
            searchRequest.searchType(SearchType.DEFAULT);
            searchRequest.indices(esIndexInfo.getIndexName());
            searchRequest.source(searchSourceBuilder);
            if (esSearchRequest.getNeedScroll()) {
                Scroll scroll = new Scroll(TimeValue.timeValueMinutes(esSearchRequest.getMinutes()));
                searchRequest.scroll(scroll);
            }
            SearchResponse search = getClient(esIndexInfo.getClusterName()).search(searchRequest, COMMON_OPTIONS);
            return search;
        } catch (Exception e) {
            log.error("EsRestClient.searchWithTermQuery.exception: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @Author: gzx
     *
     * @Description: 获取分词结果
     * @Date: 2024-06-30
     */
    public static List<String> getAnalyze(EsIndexInfo esIndexInfo, String text) throws Exception {
        List<String> list = new ArrayList<String>();
        Request request = new Request("GET", "_analyze");
        JSONObject entity = new JSONObject();
        entity.put("analyzer", "ik_smart");
        entity.put("text", text);
        request.setJsonEntity(entity.toJSONString());
        Response response = getClient(esIndexInfo.getClusterName()).getLowLevelClient().performRequest(request);
        JSONObject tokens = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        JSONArray arrays = tokens.getJSONArray("tokens");
        for (int i = 0; i < arrays.size(); i++) {
            JSONObject obj = JSON.parseObject(arrays.getString(i));
            list.add(obj.getString("token"));
        }
        return list;
    }


}
