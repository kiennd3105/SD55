app.controller("khachHangCtrl", function ($scope, $http) {


    $scope.dsKhachHang = [];

    $http.get("http://localhost:8084/khach-hang/hien-thi")
        .then(function (response) {
            $scope.dsKhachHang = response.data;
            console.log("Khách hàng:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load khách hàng", error);
        });

});