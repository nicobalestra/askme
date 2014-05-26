askMeApp.config(["$routeProvider", "$locationProvider", "$httpProvider", function($routeProvider, $locationProvider, $httpProvider){

    //Add the newly defined interceptor for injecting JWT token
    $httpProvider.interceptors.push('authInterceptor');
    $locationProvider.html5Mode(true).hashPrefix("!");

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
