askMeApp.controller("SearchFormController", ['$scope','$http', 'AskMEService', '$location', '$route', 'Recents',

function($scope, $http, AskMEService, $location, $route, Recents){

  $scope.allowSubmit = function(question){
    return question != 'undefined' &&
           question != null &&
           question != "";
  }
  $scope.askQuestion = function(){
    //Need to perform a post
    console.log("Asking a question " + $scope.question);

//If anonymous, search for the same question.
    //if question(s) exists, show'em

//If I'm logged in, search for the question.
    //If I've already asked the question, go to the question
    //If I haven't already asked the question, ask the question and show similar questions

    AskMEService.ask({"question" : $scope.question},
       function(resp){

        if (resp.response == "ok" && resp.result.length == 0){
              resp = AskMEService.allRecents(
                function(resp){
                  console.log("Query for all recent questions");
                  console.log(resp);
                  $scope.answers = resp.result;
                  Recents.recents = resp.result;
                  $location.path("/recents");

              });
        } else {
          $scope.answers = resp.result;
          $location.path("/my-questions.html").replace();
        }
    });

  };

  $scope.getQuestions = function(query) {
  console.log("Get questions..");

    return $http.get("/user/question", {
              params: {
                q: query
              }
            }).then(function(res){
               var results = [];
                angular.forEach(res.data.results, function(item){
                  results.push(item.question);
               });

              return results;
            });
  };

}]);
