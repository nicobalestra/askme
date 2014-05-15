askMeApp.factory("auth", ["$window", function($window){
    return {
        isUserLoggedin : function(){
            var toReturn = ($window.sessionStorage.token != undefined &&
                            $window.sessionStorage.token != null &&
                            $window.sessionStorage.token != "" &&
                            $window.sessionStorage.token != "null");
            console.log("Check wheter user is logged in. Value of session token is " + $window.sessionStorage.token + " and returning " + toReturn);

            return toReturn;
        },
        getUser : function(){
            console.log("Call to auth.getUser() when username is " + $window.sessionStorage.username);
            var user = {};
            user.username = $window.sessionStorage.username;

            return user;
        },
        login: function($scope, json){
            console.log("Call to login with obj..");
            console.log(json);
            $window.sessionStorage.token = json.jwt;
            $window.sessionStorage.username = json.username;
            $scope.$parent.$broadcast('userLoggedIn',{user : json});
        }
    }
}]);
