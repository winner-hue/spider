package com.pj.spider.esservice;

import com.pj.spider.entity.StructData;
import com.pj.spider.esmapper.ESStructDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StructDataService {
    @Autowired
    private ESStructDataRepository repository;

    public void save(StructData structData) {
        repository.save(structData);
    }

    public List<StructData> findByTitleAndAuthor(String title, String author) {
        return repository.findByTitleAndAuthor(title, author);
    }


    public List<StructData> findByTitle(String title) {
        return repository.findByTitle(title);
    }


    public List<StructData> findByPostTimeBetween(String postTime, String postTime2) {
        return repository.findByPostTimeBetween(postTime, postTime2);
    }


    public List<StructData> findByTitleContains(String containText) {
        return repository.findByTitleContains(containText);
    }


    public List<StructData> findByContentContains(String containText) {
        return repository.findByContentContains(containText);
    }

    public SearchHits<StructData> find(String keyword) {
        return repository.find(keyword);
    }


    public StructData searchById(String id) {
        return repository.searchById(id);
    }

}
