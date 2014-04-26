var askmeServices = angular.module('askmeServices', ['ngResource']);

askmeServices.factory('AskMEService', ['$resource',
  function($resource){
    return $resource('/questions/ask', {}, {
      ask: {method:'POST'},
      allRecents : {
                    method: "GET",
                    url: "/questions/recents"
                   }
    });
  }]);

/*
  AUTH SERVICES
*/

askmeServices.factory("LoginService", ["$resource",
  function($resource){
     return $resource("/users/login/", {}, {
        login : {method: "POST"},
        logout : {method: "POST", url: "/users/logout"}
     });
  }]);

askmeServices.factory("JoinService", ["$resource",
  function($resource){
     return $resource("/users/join/", {}, {
        join : {method: "POST"}
     });
  }]);


askmeServices.factory("UserExistsService", ["$resource",
  function($resource){
    return $resource("/users/by/:by/count", {}, {
      byEmail : {method: "GET", params: {by :"email"}},
      byUsername : {method: "GET", params: {by :"username"}}
     });
  }]);


askmeServices.service("Recents", function(){
  this.recents = {};
})