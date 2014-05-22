function recentQuestionsController($scope, Recents){
  console.log("Called the controller");
  $scope.container = {};

  $scope.container.recents = Recents.recents;

  console.log($scope.container.recents);
};
