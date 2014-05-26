/*
  AUTH SERVICES
*/

var authServicesModule = angular.module("authServicesModule", [])

.factory("LoginService", ["$resource",
  function($resource){
     return $resource("/users/login", {}, {
        login : {method: "POST"},

        logout : {method: "POST", url: "/users/logout"}
     });
  }])
.factory("JoinService", ["$resource",
  function($resource){
     return $resource("/users/join/", {}, {
        join : {method: "POST"}
     });
  }])
.factory("UserExistsService", ["$resource",
  function($resource){
    return $resource("/users/by/:by/count", {}, {
      byEmail : {method: "GET", params: {by :"email"}},
      byUsername : {method: "GET", params: {by :"username"}}
     });
  }]);
