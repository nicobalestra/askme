askMeApp.controller('navController', function($scope, $http, $modal, LoginService){

  $scope.navbarUrl = "/nav-menu.html";

  $scope.$on("userLoggedIn", function(event, args){
          $scope.navbarUrl="null";
          $scope.navbarUrl="/nav-menu.html?" + Math.random();
        });

  $scope.$on("userLoggedOut", function(event, args){
    console.log("Fired userLoggedOut");
          $scope.navbarUrl="null";
          $scope.navbarUrl="/nav-menu.html?" + Math.random();
        });



});
