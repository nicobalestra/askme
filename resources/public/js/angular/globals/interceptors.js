//Configure the app to always include the bearer JWT token if it is present
//in the session storage
askMeApp.factory('authInterceptor', ["$rootScope", "$q", "$window", function ($rootScope, $q, $window) {
  return {
    request: function (config) {
      config.headers = config.headers || {};
      //By default communicate using application/json
      if (!config.headers["Content-Type"]) {
          config.headers["Content-Type"] = "application/json";
      }

      if ($window.sessionStorage.token) {
          console.log("Adding Authorization header to http request");
        config.headers.Authorization = 'Bearer ' + $window.sessionStorage.token;
      }
      return config;
    },
    response: function (response) {

      if (response.status === 401) {
        throw Error("Not Authenticated");
      }

      return response || $q.when(response);
    }
  };
}]);
