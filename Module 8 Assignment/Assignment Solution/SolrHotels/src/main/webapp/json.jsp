<%  String sUrl = request.getParameter("url");
      
    if(sUrl!=null && sUrl.length() > 10){
       //sUrl = sUrl.substring(1, sUrl.length()-1);
        System.out.println(sUrl);
    }  
    out.println( new com.edureka.solr.hotels.SolrProcessor().getResponse( sUrl ) ); %>