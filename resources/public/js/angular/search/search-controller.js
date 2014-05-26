askMeApp.controller("SearchFormController", ['$scope','$http', 'AskMEService', '$location', '$route', 'Recents', 'auth', '$log',

function($scope, $http, AskMEService, $location, $route, Recents, auth, $log){

  $scope.allowSubmit = function(question){
    return question != 'undefined' &&
           question != null &&
           question != "";
}

  $scope.askQuestion = function(){
//Need to perform a post
    $log.debug("Asking a question " + $scope.question);

//If anonymous, search for the same question.
//if question(s) exists, show'em

//If I'm logged in, search for the question.
//If I've already asked the question, go to the question
//If I haven't already asked the question, ask the question and show similar questions

 if (auth.isUserLoggedin()) {
    $log.debug("User is logged in...");
    AskMEService.ask({"question" : $scope.question},
        function(resp){
            $log.debug("Response from ask service...");
            $log.debug(resp);
            if (resp.response == "ok" && resp.result.length == 0){
               resp = AskMEService.allRecents(
                        function(resp){
                            $log.debug("Query for all recent questions");
                            $log.debug(resp);
                            $scope.answers = resp.result;
                            Recents.recents = resp.result;
                            $location.path("/recents");

                        });
            } else {
                $scope.answers = resp.result;
                $location.path("/my-questions.html").replace();
            }
        });
    } else{
        //Anonymous user. First search for the message. If none is found search for recents
        AskMEService.search({"q" : $scope.question},
                            function(resp) {
                                //Expecting an array
                                if (resp!=null && resp.length > 0) {
                                    $log.debug("Call to AskMEService.search with " + resp);


                                }
                                else{
                                    //Should do something when no results are found.. probably going to
                                    //the latest questions...
                                    $log.debug("No results found..");
                                    AskMEService.search(function(resp){
                                        $log.debug("Since no results were found I searched for all recent questions...");
                                        Recents.recents = resp;
                                        $location.path("/recents");

                                    });
                                }
                            });
    }

  };

  $scope.getQuestions = function(query) {
  console.log("Get questions..");

      return $http.get("/questions/search", {
              params: {
                q: query
              }
            }).then(function(res){
               var results = [];
                angular.forEach(res.data, function(item){
                  results.push(item.question);
               });

              return results;
            });
  };

}]);
