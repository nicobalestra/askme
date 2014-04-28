askMeApp.controller('authController', function($scope, $http, $modal, LoginService){

  $scope.openLogin = function() {

    var modalLogin = $modal.open({
                        templateUrl: "/users/login.html",
                        controller: "LoginModalController"
                      });

    modalLogin.result.then(function (loggedIn){

       console.log("called");

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

    console.log($scope);
    $scope.$parent.$parent.$broadcast('userLoggedOut');


  }

});


/* LOGIN MODAL CONTROLLER */
askMeApp.controller('LoginModalController', function($scope, $modalInstance, LoginService) {

  $scope.login = {
    username: "",
    password: ""
  };

  $scope.loginErrors = {

    login : ""

  };

  $scope.ok = function(){

    LoginService.login(angular.toJson($scope.login), function(res){
      if (res.id){
        $scope.$parent.$broadcast('userLoggedIn',{user : res});
        $modalInstance.close();
      }
      else
        $scope.loginErrors.login = "Username or password not recognised.";

    });

  };


  $scope.cancel = function(){
    console.log("Pressed cancel")
    $modalInstance.dismiss('cancel');
  };

});


/* JOIN MODAL CONTROLLER */

askMeApp.controller('JoinModalController', function($scope, $modalInstance, JoinService, UserExistsService) {
  $scope.join = {
                 email    : "",
                 username : "",
                 password : "",
                 password1: ""
                };

  $scope.errors= {
                  email    : "",
                  username : "",
                  password     : "",
                  password1    : ""};

  $scope.emailExists = function(){


    UserExistsService.byEmail({q : $scope.join.email},
              function(res){

                if (res.count > 0)
                  $scope.errors.email = "E-mail already in use";
                else
                  $scope.errors.email = "";
              });

  };

  $scope.usernameExists = function(){


    UserExistsService.byUsername({q : $scope.join.username},
              function(res){

                if (res.count > 0)
                  $scope.errors.username = "Username already in use";
                else
                  $scope.errors.username = "";
              });

  };

  $scope.register = function(){

      JoinService.join(angular.toJson($scope.join),

                       function(res) {

                         if (res.response == "error"){
                           console.log("There was an error");
                           console.log($scope);
                           $scope.errors = res.errors;

                         } else

                           $modalInstance.close();

                     });
  };

  $scope.closeAlert = function(err){
    err = null;
  }

});
