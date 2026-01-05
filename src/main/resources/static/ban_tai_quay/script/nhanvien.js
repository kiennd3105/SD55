// var app = angular.module("myApp", []);

app.controller("nhanVienCtrl", function ($scope, $http) {


    $scope.dsNhanVien = [];
    $scope.detailNhanVien = null;
    $scope.showDetailModal = false;



    $scope.loadDanhSach = function() {
        $http.get("http://localhost:8084/nhan-vien/hien-thi")
            .then(function(response) {
                $scope.dsNhanVien = response.data;
            })
            .catch(function(error) {
                console.error("Lỗi load nhân viên", error);
            });
    };

    $scope.loadDanhSach();


    $scope.showDetail = function(id) {
        $http.get("http://localhost:8084/nhan-vien/detail/" + id)
            .then(function(response) {
                $scope.detailNhanVien = response.data;
                $scope.showDetailModal = true;
            })
            .catch(function(error) {
                console.error("Lỗi lấy chi tiết nhân viên", error);
            });
    };


    $scope.updateNhanVien = function () {
        if ($scope.nvForm.$invalid) {
            alert("Vui lòng nhập đúng và đủ thông tin!");
            return;
        }

        $http.put(
            "http://localhost:8084/nhan-vien/update/" + $scope.detailNhanVien.id,
            $scope.detailNhanVien
        ).then(function () {
            alert("Cập nhật thành công");
            $scope.closeDetailModal();
            $scope.loadDanhSach();
        }).catch(function (error) {
            console.error(error);
            alert("Cập nhật thất bại");
        });
    };



    $scope.closeDetailModal = function() {
        $scope.showDetailModal = false;
        $scope.detailNhanVien = null;
    };

    $scope.keyword = "";

    $scope.searchNhanVien = function (nv) {
        if (!$scope.keyword) return true;

        let key = $scope.keyword.toLowerCase();

        return (
            (nv.ma && nv.ma.toLowerCase().includes(key)) ||
            (nv.ten && nv.ten.toLowerCase().includes(key)) ||
            (nv.email && nv.email.toLowerCase().includes(key))
        );
    };

    $scope.newNhanVien = {};

    $scope.showAddModal = false;

    $scope.openAddModal = function () {
        $scope.newNhanVien = {
            ma: "",
            ten: "",
            email: "",
            // sdt: "",
            trangThai: true
        };
        $scope.showAddModal = true;
    };

    $scope.closeAddModal = function () {
        $scope.showAddModal = false;
        $scope.newNhanVien = {};
    };

    $scope.addNhanVien = function () {
        if ($scope.addForm.$invalid) {
            alert("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        $http.post("http://localhost:8084/nhan-vien/add", $scope.newNhanVien)
            .then(function () {
                alert("Thêm nhân viên thành công");
                $scope.closeAddModal();
                $scope.loadDanhSach();
            })
            .catch(function (error) {
                if (error.status === 400) {
                    let msg = "";
                    angular.forEach(error.data, function (value) {
                        msg += value + "\n";
                    });
                    alert(msg);
                } else {
                    alert("Lỗi hệ thống");
                }
            });
    };
    $scope.generateMaKH = function () {
        let max = 0;

        $scope.dsNhanVien.forEach(nv => {
            if (nv.ma && nv.ma.startsWith("KH")) {
                let num = parseInt(nv.ma.substring(2));
                if (!isNaN(num) && num > max) {
                    max = num;
                }
            }
        });

        let next = max + 1;

        $scope.newNhanVien.ma = "NV" + next.toString().padStart(8, "0");
    };

    $scope.openAddModal = function () {
        $scope.newNhanVien = {
            trangThai: 1
        };

        $scope.generateMaKH(); //
        $scope.showAddModal = true;
    };



});
