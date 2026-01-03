angular.module("myApp").controller("thuongHieuCtrl", function ($scope, $http) {
    $scope.dsThuongHieu = [];

    $http.get("http://localhost:8084/san-pham/thuong-hieu")
        .then(function (res) {
            $scope.dsThuongHieu = res.data;
        })
        .catch(function (err) {
            console.error("Lỗi load thương hiệu", err);
        });

    $scope.getSTT = function (i) { return i + 1; };
});
