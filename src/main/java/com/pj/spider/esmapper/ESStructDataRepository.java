package com.pj.spider.esmapper;

import com.pj.spider.entity.StructData;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ESStructDataRepository extends ElasticsearchRepository<StructData, String> {
    List<StructData> findByTitleAndAuthor(String title, String author);

    List<StructData> findByTitle(String title);

    List<StructData> findByPostTimeBetween(String postTime, String postTime2);

    List<StructData> findByTitleContains(String containText);

    List<StructData> findByContentContains(String containText);

    @Highlight(fields = {
            @HighlightField(name = "title"),
            @HighlightField(name = "content")
    })
    @Query("{\"match\":{\"title\":\"?0\", \"content\":\"?0\"}}")
    SearchHits<StructData> find(String keyword);

    @Highlight(fields = {
            @HighlightField(name = "title"),
            @HighlightField(name = "content")
    })
    @Query("{\"match\":{\"title\":\"?0\", \"content\":\"?0\"}}")
    StructData searchById(String id);
}
