app.controller("nhanVienCtrl", function ($scope, $http) {


    $scope.dsNhanVien = [];


    $http.get("http://localhost:8084/nhan-vien/hien-thi")
        .then(function (response) {
            $scope.dsNhanVien = response.data;
            console.log("Nhân viên:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load nhân viên", error);
        });

});
