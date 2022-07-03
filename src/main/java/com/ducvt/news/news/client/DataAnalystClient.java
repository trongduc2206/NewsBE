package com.ducvt.news.news.client;

import com.ducvt.news.news.payload.request.RecommendRequest;
import com.ducvt.news.news.payload.response.RecommendResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "dataAnalyst", url = "localhost:5000")
public interface DataAnalystClient {
    @RequestMapping(method = RequestMethod.GET)
    String test();

    @PostMapping(value = "/recommend")
    RecommendResponse getRecommend(@RequestBody RecommendRequest recommendRequest);

    @PostMapping(value = "/recommend/single")
    RecommendResponse getRelevant(@RequestBody RecommendRequest recommendRequest);
}
