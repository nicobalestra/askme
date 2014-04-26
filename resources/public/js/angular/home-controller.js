var askMeApp = angular.module('askMeApp', ['askmeServices', "ui.bootstrap", "ngRoute"]);

askMeApp.config(["$routeProvider", "$locationProvider", function($routeProvider, $locationProvider){



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
