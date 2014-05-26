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
