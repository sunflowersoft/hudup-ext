/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */

function hdpGetUrl(host, port, regName, externalUserId, externalItemId, maxRecommend, rating) {
  	var url = "http://" + host + ":" + port + "/recommendlet?" + 
  		"host=" + host +
  		"&port=" + port +
  		"&reg_name=" + regName +
  		"&external_userid=" + externalUserId +
  		"&max_recommend=" + maxRecommend;
	
  	if (externalItemId)
  		url += "&external_itemid=" + externalItemId;
  	if (rating)
  		url += "&rating=" + rating;
  	
  	return url;
}


function hdpGetJsonUrl(host, port, regName, externalUserId, externalItemId, maxRecommend, rating) {
  	var url = "http://" + host + ":" + port + "/json/recommendlet?" + 
  		"host=" + host +
  		"&port=" + port +
  		"&reg_name=" + regName +
  		"&external_userid=" + externalUserId +
  		"&max_recommend=" + maxRecommend;
	
  	if (externalItemId)
  		url += "&external_itemid=" + externalItemId;
  	if (rating)
  		url += "&rating=" + rating;
  	
  	return url;
}


function hdpAjaxCall(host, port, regName, externalUserId, externalItemId, maxRecommend, rating) {
	var xmlhttp;
	var url = hdpGetUrl(host, port, regName, externalUserId, externalItemId, maxRecommend, rating);
		
	if (window.XMLHttpRequest) {
		xmlhttp=new XMLHttpRequest();
	}
	else {
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	
	xmlhttp.onreadystatechange = function() {
		var recommendlet = document.getElementById("recommendlet")
  		if (recommendlet && xmlhttp.readyState == 4) {
  			alert(xmlhttp.responseText);
    		recommendlet.innerHTML = xmlhttp.responseText;
    	}
  	}
  	
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
		
	var recommendlet = document.getElementById("recommendlet")
    recommendlet.innerHTML = xmlhttp.responseText;
}


