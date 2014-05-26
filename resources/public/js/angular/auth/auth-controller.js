askMeApp.controller('authController', ["$scope", "$http", "$modal", "$window", "LoginService", "auth", function($scope, $http, $modal, $window, LoginService, auth){

  $scope.auth = auth;
  $scope.openLogin = function() {

    var modalLogin = $modal.open({
                        templateUrl: "/users/login.html",
                        controller: "LoginModalController"
                      });

    modalLogin.result.then(function (loggedIn){

    }, function(){

        console.log("Modal dismissed");
    });
  };

  $scope.openJoin = function() {
    var modalJoin = $modal.open({
                        templateUrl: "/users/join.html",
                        controller : 'JoinModalController'

    });

    modalJoin.result.then(function (loggedIn){

       console.log("called");

    }, function(){

        console.log("Modal dismissed");
    });
  }

  $scope.logout = function(){

    LoginService.logout();
    console.log("Logging out.. removing session token");
    $window.sessionStorage.token = null;
    $scope.$parent.$parent.$broadcast('userLoggedOut');


  }

}]);
