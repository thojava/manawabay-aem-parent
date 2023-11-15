/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function() {
    "use strict";

    const urls = new URL(window.location.href);
    const searchParams = urls.searchParams;
    const queryParam =  searchParams.get('fulltext') || searchParams.get('q'); 
    var cmpStoreList = document.querySelector("#cmp-store-list");
    var cmpArticleList = document.querySelector("#cmp-article-list");
    var searchResultEndMessage = document.querySelector(".noresultmessage");
    var articleTitle = document.querySelector(".cmp-article-title");
    var storeTitle = document.querySelector(".cmp-store-title");

    var LIST_GROUP;
    var LIST_GROUP_Article;


   function isTargetComponent(componentClassName) { 
        var component = { exist: false } 
        if (componentClassName){ 
            component.exist = (componentClassName) ? true : false; 
        }
        return component; 
    } 
     

    // On page load function
    function onDocumentReady() {
        var targetComponent = document.querySelector(".search__field--view");
        if (isTargetComponent(targetComponent).exist) {
            fetchDataNew();
        } 
       
    }

    // FETCH DATA API CALL
    function fetchDataNew() {
        var apiURL = getDataURL();
      
        fetch(apiURL)
            .then(function(response) {
                return response.json();
            })
            .then(function(json) {
                return displayDataOnPage(json);
            });
    }

    // FETCH DATA URL CREATION
    function getDataURL() {
        var endpoint = document.querySelector(".search__field--view").dataset.cmpEndpoint;
        var limit = document.querySelector(".search__field--view").dataset.cmpLimit;
        var fetchAPIURL = endpoint + "?query=" + queryParam + "&limit=" + limit;
        return fetchAPIURL;
    }

    // Check null value and replace with empty string
    function checkNull(inputValue) {
        var value = "";
        if (inputValue === null) {
            return value;
        } else {
            return inputValue;
        }
    }

    // DISPLAY DATA ON PAGE LOAD
    function displayDataOnPage(resultData) {
       
         var dataStore = resultData.storeResults;
         var dataArticle = resultData.articleResults;
         var dataStoreCount = Object.keys(dataStore).length;
         var dataArticleCount = Object.keys(dataArticle).length;
        
         if (dataStoreCount > 0) {
            cmpStoreList.innerHTML = "";
            LIST_GROUP = "";
            storeTitle.style.display = "block";
        }
        if (dataArticleCount > 0) {
            cmpArticleList.innerHTML = "";
            LIST_GROUP_Article = "";
            articleTitle.style.display = "block";
        }
         
        if (dataStoreCount !== 0 || dataArticleCount !== 0) {
            searchResultEndMessage.style.display = "none";
        } else {
            searchResultEndMessage.style.display = "block";
        }


        for (var i = 0; i < dataStoreCount; i++) {
            LIST_GROUP += "<li class='cmp-searchresult-item'><a class='cmp-searchresult-link'  href=" + checkNull(dataStore[i].uri) + "><img src = "+checkNull(dataStore[i].featuredImage)+"> <p><img src = "+checkNull(dataStore[i].brandImage)+"></p></a></li>";
        }

        for (var i = 0; i < dataArticleCount; i++) {
            LIST_GROUP_Article += "<li class='cmp-searchresult-item'><a class='cmp-searchresult-link'  href=" + checkNull(dataArticle[i].uri) + "><img src = "+checkNull(dataArticle[i].featuredImage)+"><span>" + checkNull(dataArticle[i].publishedDate) + "</span><h3 class='cmp-searchresult-title'>" + checkNull(dataArticle[i].title) + "</h3><p>" + checkNull(dataArticle[i].description) + "</p></a></li>";
        }
        cmpStoreList.innerHTML = ""; //clear the container first before adding content
        if(LIST_GROUP !== undefined){
            cmpStoreList.innerHTML = LIST_GROUP;
        }
        cmpArticleList.innerHTML = ""; //clear the container first before adding content
        if(LIST_GROUP_Article !== undefined){
            cmpArticleList.innerHTML = LIST_GROUP_Article;
        }
    }

    document.addEventListener("DOMContentLoaded", onDocumentReady);
})();