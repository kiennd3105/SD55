app.controller("hoaDonCtrl", function ($scope, $http,$routeParams,$timeout) {
    $scope.dsHoaDon = [];
    $scope.hoaDonDetail = {};
    $scope.dsHDCT = [];
    $http.get("http://localhost:8084/hoa-don/getAll")
        .then(function (response) {
            $scope.dsHoaDon = response.data;
            console.log("hoadon:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load hóa đơn" , error);
        });

});