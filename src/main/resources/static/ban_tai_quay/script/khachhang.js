
app.controller("khachHangCtrl", function($scope, $http) {

    $scope.dsKhachHang = [];
    $scope.detailKhachHang = null;
    $scope.showDetailModal = false;

    $scope.loadDanhSach = function () {
        $http.get("http://localhost:8084/khach-hang/hien-thi")
            .then(function (response) {
                $scope.dsKhachHang = response.data;
            })
            .catch(function (error) {
                console.error("Lỗi load khách hàng", error);
            });
    };

    $scope.loadDanhSach();

    $scope.showDetail = function(id) {
        $http.get("http://localhost:8084/khach-hang/detail/" + id)
            .then(function(response) {
                $scope.detailKhachHang = response.data;
                $scope.showDetailModal = true;
            })
            .catch(function(error) {
                console.error("Lỗi lấy chi tiết khách hàng", error);
            });
    };

    $scope.updateKhachHang = function () {
        if ($scope.khForm.$invalid) {
            alert("Vui lòng nhập đúng và đủ thông tin!");
            return;
        }

        $http.put(
            "http://localhost:8084/khach-hang/update/" + $scope.detailKhachHang.id,
            $scope.detailKhachHang
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
        $scope.detailKhachHang = null;
    };

    $scope.generateMaKH = function () {
        let max = 0;

        $scope.dsKhachHang.forEach(function (kh) {
            if (kh.ma && kh.ma.startsWith("KH")) {
                let num = parseInt(kh.ma.substring(2));
                if (!isNaN(num) && num > max) {
                    max = num;
                }
            }
        });

        let next = max + 1;
        return "KH" + next.toString().padStart(8, "0");
    };


    $scope.openAddModal = function () {
        $scope.newKhachHang = {
            ma: $scope.generateMaKH(),
            ten: "",
            email: "",
            gioiTinh: "",
            sdt: "",
            diaChi: "",
            trangThai: 1
        };
        $scope.showAddModal = true;
    };

    $scope.closeAddModal = function () {
        $scope.showAddModal = false;
    };

    $scope.addKhachHang = function () {
        if ($scope.addForm.$invalid) {
            alert("Vui lòng nhập đầy đủ và đúng thông tin!");
            return;
        }

        $http.post("http://localhost:8084/khach-hang/add", $scope.newKhachHang)
            .then(function () {
                alert("Thêm khách hàng thành công");
                $scope.closeAddModal();
                $scope.loadDanhSach();
            })
            .catch(function () {
                alert("Thêm khách hàng thất bại");
            });
    };



});
