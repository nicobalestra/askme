var askMeApp = angular.module('askMeApp', ['askmeServices', "ui.bootstrap", "ngRoute"]);

askMeApp.factory("auth", function($window){
    return {
        isUserLoggedin : function(){
            return ($window.sessionStorage.token != undefined &&
                    $window.sessionStorage.token !== null &&
                    $window.sessionStorage.token != "");
        }
    }
});


//Configure the app to always include the bearer JWT token if it is present
//in the session storage

  askMeApp.factory('authInterceptor', ["$rootScope", "$q", "$window", function ($rootScope, $q, $window) {
  return {
    request: function (config) {
        console.log("In http interceptor");
      config.headers = config.headers || {};
      //By default communicate using application/json
      if (!config.headers["Content-Type"]) {
          config.headers["Content-Type"] = "application/json";
      }

      if ($window.sessionStorage.token) {
        config.headers.Authorization = 'Bearer ' + $window.sessionStorage.token;
      }
      return config;
    },
    response: function (response) {
        console.log("http interceptor response:");
        console.log(response);
      if (response.status === 401) {
        throw Error("Not Authenticated");
      }

      return response || $q.when(response);
    }
  };
}]);


askMeApp.config(["$routeProvider", "$locationProvider", "$httpProvider", function($routeProvider, $locationProvider, $httpProvider){

  //Add the newly defined interceptor for injecting JWT token
  $httpProvider.interceptors.push('authInterceptor');

  $routeProvider
    .when("/",
         {
           templateUrl : "partials/search.html",
           controller : "SearchFormController as searchForm"
         })
    .when("/search-question",
         {
           templateUrl: "partials/search-question",
           controller : "SearchAll"
         })
    .when("/all-recents",
         {
           templateUrl: "/recents.html",
           controller : "allRecentQuestionsController as allRecents"
         })

    .when("/recents",
         {
           templateUrl: "/partials/recents.html",
           controller : 'recentQuestionsController'
         })

  .otherwise(
         {
           redirectTo: "/"
         });
}]);


askMeApp.controller("askMeController", ['$scope','$http', 'AskMEService', '$location', '$route',

function($scope, $http, AskMEService, $location, $route){

}]);
