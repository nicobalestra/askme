/* LOGIN MODAL CONTROLLER */
askMeApp.controller('LoginModalController', function($scope, $window, $modalInstance, LoginService, auth) {

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
        console.log("User successfully logged in: " + res.id);
        console.log(res);
        console.log( "JWT:" + res.jwt );
        auth.login($scope, res);

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
