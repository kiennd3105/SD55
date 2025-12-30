angular.module("myApp").controller("mauSacCtrl", function ($scope, $http) {
    $scope.dsMauSac = [];

    $http.get("http://localhost:8084/san-pham/mau-sac")
        .then(function (res) {
            $scope.dsMauSac = res.data;
        })
        .catch(function (err) {
            console.error("Lỗi load màu sắc", err);
        });

    $scope.getSTT = function (i) { return i + 1; };
});
