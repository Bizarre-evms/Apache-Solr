/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edureka.solr.hotels;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author MadanKN
 */
public class SolrProcessor {
    
    
    
    public String getResponse(String sUrl) throws Exception {
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(sUrl);
        HttpResponse response = client.execute(request);
        
	BufferedReader rd = new BufferedReader(
		new InputStreamReader(response.getEntity().getContent()));
 
	StringBuilder result = new StringBuilder();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}
        
        return result.toString();

    } 
    
}
