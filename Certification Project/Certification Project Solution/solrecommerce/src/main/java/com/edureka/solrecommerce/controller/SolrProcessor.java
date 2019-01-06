package com.edureka.solrecommerce.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;

import com.edureka.solrecommerce.dto.productDto;

/**
 *
 * @author Mohan Sandeep
 */
public class SolrProcessor {

    public HttpSolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr/products");

    public Map<String, Object> getProducts(String q, int pageSize, String category, int minPrice, int maxPrice) throws Exception {

        HashMap<String, Object> res = new HashMap<String, Object>();
        SolrQuery query = new SolrQuery();

        query.setQuery(q);
        query.addFacetField("category");
        query.setStart(0);
        query.set("sort", "price desc, rating asc");
        query.setRows(pageSize);
        query.setFacetMinCount(0);
        query.setFacet(true);
        query.setFacetLimit(-1);
        query.addNumericRangeFacet("price", 0, 100000, 10000);
        
        String fq = "";
        if (category != null) {
            fq = "category: \"" + category + "\"";
        }
        if (maxPrice > 0) {
            if (fq.length() > 0) {
                fq += " AND ";
            }
            fq = "price : [" + minPrice + " TO " + maxPrice + "]";
        }
        query.setFilterQueries(fq);

        QueryResponse response = solrClient.query(query);
        long totalProducts = response.getResults().getNumFound();
        List<productDto> products = response.getBeans(productDto.class);

        List<FacetField> facets = response.getFacetFields();
        List<Map<String, HashMap<String, String>>> facetList = new ArrayList<>();
        Map<String, HashMap<String, String>> facetMap = new HashMap<>();

        for (FacetField f : facets) {
            if (f != null && f.getValues() != null) {
                String facetName = f.getName();
                HashMap<String, String> records = new HashMap<String, String>();
                for (FacetField.Count c : f.getValues()) {
                    records.put(c.getName(), c.getCount() + "");
                }
                facetMap.put(facetName, records);
                facetList.add(facetMap);
            }
        }

        List<RangeFacet> facetRanges = response.getFacetRanges();
        List<HashMap<String, String>> facetRangeList = new ArrayList<HashMap<String, String>>();

        for (RangeFacet f : facetRanges) {
            String rangeFacetName = f.getName();
            HashMap<String, String> result = new HashMap<String, String>();
                for (Object c : f.getCounts()) {
                    RangeFacet.Count rfc = (RangeFacet.Count) c;
                    result.put(rfc.getValue(), rfc.getCount() + "");
               }
                res.put("priceRangeFacet", result);
        }

        res.put("numFound", totalProducts);
        res.put("products", products);
        res.put("facetList", facetList);
        res.put("productTotal", totalProducts);

        return res;
    }

    public List<String> getSuggestions(String term) throws Exception {
        SolrQuery sq = new SolrQuery();
        sq.setRequestHandler("/suggest");

        sq.setParam("suggest", "true");
        sq.setParam("suggest.build", "true");
        sq.setParam("suggest.dictionary", "mySuggester");
        sq.setParam("suggest.q", term);

        QueryResponse response = solrClient.query(sq);

        List<String> types = response.getSuggesterResponse().getSuggestedTerms().get("mySuggester");
        
        return types;
    }
}
