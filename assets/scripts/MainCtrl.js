import * as angular from 'angular';

angular.module('app')
    .controller('MainCtrl', function($scope, $window) {
        $scope.$window = $window;
    });
