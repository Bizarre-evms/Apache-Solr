package com.edureka.solrecommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class ApplicationController {
    
    @Resource(name = "solrProcessor")
    SolrProcessor solrProcessor;
    
    public String welcome(ModelMap model) {
        model.addAttribute("msgArgument", "Maven Java Web Application Project: Success!");
        return "index";
    }

    @RequestMapping(value={"/products"}, method=RequestMethod.GET,headers="Accept=*/*",produces = "application/json")
    @ResponseBody
    public Map<String, Object> getProducts(
                    @RequestParam(value = "query", required = false, defaultValue = "") String query,
                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                    @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
                    @RequestParam(value = "category", required = false) String category,
                    @RequestParam(value = "minPrice", required = false, defaultValue = "0") int minPrice,
                    @RequestParam(value = "maxPrice", required = false, defaultValue = "0") int maxPrice) {

        Map<String, Object> response = new HashMap<>();
        try {
            response = solrProcessor.getProducts(query, pageSize, category, minPrice, maxPrice);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping(value={"/suggestions"}, method=RequestMethod.GET,headers="Accept=*/*",produces = "application/json")
    @ResponseBody
    public Map<String, Object> getProducts(@RequestParam(value = "term", required = false, defaultValue = "") String term) {

        Map<String, Object> response = new HashMap<>();
        List<String> suggestionList = new ArrayList<>();
        try {
            suggestionList = solrProcessor.getSuggestions(term);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        response.put("suggestionList", suggestionList);
        return response;
    }

}
