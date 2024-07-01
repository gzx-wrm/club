package com.gzx.club.subject.infra.basic.service.impl;

import com.gzx.club.subject.infra.basic.entity.EsSubjectFields;
import com.gzx.club.subject.infra.basic.entity.EsSubjectInfo;
import com.gzx.club.subject.infra.basic.es.EsIndexInfo;
import com.gzx.club.subject.infra.basic.es.EsRestClient;
import com.gzx.club.subject.infra.basic.es.EsSearchRequest;
import com.gzx.club.subject.infra.basic.es.EsSourceData;
import com.gzx.club.subject.infra.basic.service.SubjectEsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @program: club
 * @description: ES题目类服务实现类
 * @author: gzx
 * @create: 2024-06-30
 **/
@Service
public class SubjectEsServiceImpl implements SubjectEsService {

    @Override
    public boolean insert(EsSubjectInfo esSubjectInfo) {
        EsSourceData esSourceData = new EsSourceData();
        Map<String, Object> data = convert2EsSourceData(esSubjectInfo);
        esSourceData.setData(data);
        esSourceData.setDocId(esSubjectInfo.getDocId().toString());
        return EsRestClient.insertDoc(getEsIndexInfo(), esSourceData);
    }

    @Override
    public List<EsSubjectInfo> querySubjectList(EsSubjectInfo esSubjectInfo, Integer from, Integer size) {
        EsSearchRequest esSearchQuery = createEsSearchQuery(esSubjectInfo);
        esSearchQuery.setFrom(from);
        esSearchQuery.setSize(size);
        SearchResponse searchResponse = EsRestClient.searchWithTermQuery(getEsIndexInfo(), esSearchQuery);

        LinkedList<EsSubjectInfo> esSubjectInfos = new LinkedList<>();
        SearchHits searchResponseHits = searchResponse.getHits();
        if (ArrayUtils.isEmpty(searchResponseHits.getHits())) {
            return esSubjectInfos;
        }

        SearchHit[] hits = searchResponseHits.getHits();
        for (SearchHit hit : hits) {
            EsSubjectInfo esSubjectInfo1 = convertResult(hit);
            if (Objects.nonNull(esSubjectInfo1)) {
                esSubjectInfos.add(esSubjectInfo1);
            }
        }

        return esSubjectInfos;
    }

    private EsSubjectInfo convertResult(SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        if (MapUtils.isEmpty(sourceAsMap)) {
            return null;
        }
        EsSubjectInfo result = new EsSubjectInfo();
        result.setSubjectId(MapUtils.getLong(sourceAsMap, EsSubjectFields.SUBJECT_ID));
        result.setSubjectName(MapUtils.getString(sourceAsMap, EsSubjectFields.SUBJECT_NAME));

        result.setSubjectAnswer(MapUtils.getString(sourceAsMap, EsSubjectFields.SUBJECT_ANSWER));

        result.setDocId(MapUtils.getLong(sourceAsMap, EsSubjectFields.DOC_ID));
        result.setSubjectType(MapUtils.getInteger(sourceAsMap, EsSubjectFields.SUBJECT_TYPE));
        result.setScore(new BigDecimal(String.valueOf(hit.getScore())).multiply(new BigDecimal("100.00")
                .setScale(2, RoundingMode.HALF_UP)));

        //处理name的高亮
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField subjectNameField = highlightFields.get(EsSubjectFields.SUBJECT_NAME);
        if(Objects.nonNull(subjectNameField)){
            Text[] fragments = subjectNameField.getFragments();
            StringBuilder subjectNameBuilder = new StringBuilder();
            for (Text fragment : fragments) {
                subjectNameBuilder.append(fragment);
            }
            result.setSubjectName(subjectNameBuilder.toString());
        }

        //处理答案高亮
        HighlightField subjectAnswerField = highlightFields.get(EsSubjectFields.SUBJECT_ANSWER);
        if(Objects.nonNull(subjectAnswerField)){
            Text[] fragments = subjectAnswerField.getFragments();
            StringBuilder subjectAnswerBuilder = new StringBuilder();
            for (Text fragment : fragments) {
                subjectAnswerBuilder.append(fragment);
            }
            result.setSubjectAnswer(subjectAnswerBuilder.toString());
        }

        return result;
    }

    private EsSearchRequest createEsSearchQuery(EsSubjectInfo req) {
        EsSearchRequest esSearchRequest = new EsSearchRequest();
        BoolQueryBuilder bq = new BoolQueryBuilder();
        MatchQueryBuilder subjectNameQueryBuilder = QueryBuilders.matchQuery(
                EsSubjectFields.SUBJECT_NAME, req.getKeyword());
        bq.should(subjectNameQueryBuilder);
        subjectNameQueryBuilder.boost(2);

        MatchQueryBuilder subjectAnswerQueryBuilder = QueryBuilders.matchQuery(EsSubjectFields.SUBJECT_ANSWER, req.getKeyword());
        bq.should(subjectAnswerQueryBuilder);

        MatchQueryBuilder subjectTypQueryBuilder = QueryBuilders.matchQuery(EsSubjectFields.SUBJECT_TYPE, req.getSubjectType());
        bq.must(subjectTypQueryBuilder);
        bq.minimumShouldMatch(1);

        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        highlightBuilder.preTags("<span style = \"color:red\">");
        highlightBuilder.postTags("</span>");

        esSearchRequest.setBq(bq);
        esSearchRequest.setHighlightBuilder(highlightBuilder);
        esSearchRequest.setFields(EsSubjectFields.FIELD_QUERY);
        esSearchRequest.setNeedScroll(false);
        return esSearchRequest;
    }

    private Map<String, Object> convert2EsSourceData(EsSubjectInfo esSubjectInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put(EsSubjectFields.SUBJECT_ID, esSubjectInfo.getSubjectId());
        data.put(EsSubjectFields.DOC_ID, esSubjectInfo.getDocId());
        data.put(EsSubjectFields.SUBJECT_NAME, esSubjectInfo.getSubjectName());
        data.put(EsSubjectFields.SUBJECT_ANSWER, esSubjectInfo.getSubjectAnswer());
        data.put(EsSubjectFields.SUBJECT_TYPE, esSubjectInfo.getSubjectType());
        data.put(EsSubjectFields.CREATE_USER, esSubjectInfo.getCreateUser());
        data.put(EsSubjectFields.CREATE_TIME, esSubjectInfo.getCreateTime());
        return data;
    }

    private EsIndexInfo getEsIndexInfo() {
        EsIndexInfo esIndexInfo = new EsIndexInfo();
        esIndexInfo.setIndexName("subject_index");
        esIndexInfo.setClusterName("...");

        return esIndexInfo;
    }
}
