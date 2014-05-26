var searchServices = angular.module('searchServices', ['ngResource'])
.factory('SearchREST', ['$resource',
  function($resource){
    return $resource('/questions/ask', {}, {
        ask: {method:'POST',
              url: "/questions/ask/:question"
           },
        search : {method: "GET",
                  url: "/questions/search",
                  isArray: true}
    });
  }])

searchServices.service("Recents", function(){
  this.recents = {};
})
