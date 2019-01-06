 <!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="jquery-1.11.1.min.js"></script>
        <link rel="stylesheet" href="./jquery-ui/themes/smoothness/jquery-ui.css">
        <script src="./jquery-ui/jquery-ui.js"></script>

        <script>
            $(function() {
                var availableTags = [
                "Delhi",
                "Bangalore"
                ];

                $( "#query" ).autocomplete({
                    source: function(request, resolve) {
                        var term = request.term;
                        var sUrl     = 'http://localhost:8983/solr/hotels/suggest?wt=json&suggest.q=' + term + '&suggest=true&suggest.build=true&suggest.dictionary=mySuggester';
    					var response = [];
    					$.ajax({
                            type: "POST",
                            url: 'json.jsp',
                            data: {url: sUrl},
                            success: function(resp) {
                                var suggestions = resp.suggest.mySuggester[term].suggestions;
                                for (suggestTerm in suggestions) {
                                    response.push(suggestions[suggestTerm].term);
                                }
                                resolve(response);
                            },
                            dataType: 'json'
                        });
                    }
                });
            });
       </script>

    </head>
    <body style="margin:0px; width:100%; height: 100%;">
        <table width="100%" border="0" style="border: solid 1px #242424;">
        <tr> <td colspan="2"><br/></td> </tr>    
        <tr>
            <td width="15%"><span style="margin-left: 20px;"><img width="50%" src="./images/logo.png"></span></td> 
            <td width="85%">
                <b>Search</b> <input style="line-height: 25px; width: 60%;" type="text" name="query" id="query"  value=""/>
                <input style="line-height: 25px; font-size: 14px;" type="button" value="Search" onclick="javascript:searchSolr();"/>
            </td>
        </tr>  
        
        <tr> <td colspan="2"><br/></td> </tr> 
        
        
        <tr id="idRow"> 
            <td valign="top" style="border: solid 1px #ccc;">
            <div id="starRatingTitle" style="margin-bottom:10px; color: #fff9e5; background: #F04167; font-size: 16px; font-weight: bold;"></div>    
            <div id="starRating"></div>
            
            <div id="priceRangeFilterTitle" style="margin-top:20px; margin-bottom:10px; color: #fff9e5; background: #F04167; font-size: 16px; font-weight: bold;"></div>    
            <div id="priceRangeFilter"></div>
            </td>
            <td valign="top" id="resultsTD"  style="border: solid 1px #ccc;">
            </td>
        </tr>    
        
        </table>
    </body>
    <script>
        function searchSolr(){
            var queryStr = $('#query').val();
            var sUrl     = 'http://localhost:8983/solr/hotels/select?wt=json&q='+queryStr+'&facet=true&facet.field=star&facet.range=price&f.price.facet.range.start=0&f.price.facet.range.end=10000&f.price.facet.range.gap=1000';
            
            $.ajax({
            type: "POST",
            url: 'json.jsp',
            data: {url: sUrl},
            success: onSuccess,
            dataType: 'json'
            });        
        }
        
        function onSuccess(rData){
            var recordsFound = rData.response.numFound;
            
            
            var facets = '';
            $('#starRatingTitle').html("Star Rating");
            var starRatingFacetData = rData.facet_counts.facet_fields.star;
            for (var i=0; i < starRatingFacetData.length ; i++) {
                if (starRatingFacetData[i+1] === 0) {
                    i++;
                    continue;
                }
                $('#starRating').append('<span style="display:block;">' + starRatingFacetData[i] + ' Stars  (' + starRatingFacetData[i+1] + ')</span>');
                i++;
            }

            $('#priceRangeFilterTitle').html("Price Filter");
            var priceFilterData = rData.facet_counts.facet_ranges.price.counts;
            var priceRangeEnd = rData.facet_counts.facet_ranges.price.end;
            for (var i=0; i < priceFilterData.length ; i++) {
                if (priceFilterData[i+1] === 0) {
                    i++;
                    continue;
                }
                if (!priceFilterData[i+2]) {
                    var priceUpperBound = priceRangeEnd;
                } else {
                    var priceUpperBound = priceFilterData[i+2];
                }
                $('#priceRangeFilter').append('<span style="display:block;">' + priceFilterData[i] + ' - ' + priceUpperBound + ' (' + priceFilterData[i+1] + ')</span>');
                i++;
            }
            
            
            var res = ''; 
            for(var i=0;i<recordsFound;i++){
                res = res.concat("<table><tr valign='top'>");
                res = res.concat("<td valign='top' style='width:175px;'>");
                res = res.concat("<img width='175' src='./"+rData.response.docs[i].img+"'/>");
                res = res.concat("</td>");
                res = res.concat("<td valign='top'>");
                res = res.concat("<p style='color: #949595; margin:0px; font-weight:bold; font-size: 24px;'>"+rData.response.docs[i].name +"</p>");
                res = res.concat("<br/>");
                res = res.concat(rData.response.docs[i].desc);
                res = res.concat("</td>");
                res = res.concat("<td style='width: 120px; color:red; font-size:24px; font-weight: bold;'>");
                res = res.concat("$ " +rData.response.docs[i].price +  "");
                res = res.concat("</td>");
                res = res.concat("</tr></table>");
            }
            $("#idRow").css("display: block;");
            $("#resultsTD").html(res);
        }
    </script>
</html>
