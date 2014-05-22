askMeApp.controller('navController', ["$scope", "$http", "$modal", "LoginService", "auth", function($scope, $http, $modal, LoginService, auth){

  $scope.navbarUrl = "/nav-menu.html";
  $scope.loggedIn = auth.isUserLoggedin();

  $scope.$on("userLoggedIn", function(event, args){
      console.log("Event userLoggedIn cathed");
      $scope.loggedIn = null;
      $scope.loggedIn = auth.isUserLoggedin();
      $scope.navbarUrl="null";
      $scope.navbarUrl="/nav-menu.html?" + Math.random();

  });

  $scope.$on("userLoggedOut", function(event, args){
      console.log("Fired userLoggedOut");
      $scope.loggedIn = null;
      $scope.loggedIn = auth.isUserLoggedin();
      $scope.navbarUrl="null";
      $scope.navbarUrl="/nav-menu.html?" + Math.random();
  });



}]);
