    app.controller("hoaDonCtrl", function ($scope, $http, $timeout) {

        $scope.dsHoaDon = [];
        $scope.hoaDonDangXem = {};
        $scope.dsHDCT = [];
        let hoaDonModal = null;

        // load danh sách hóa đơn
        $http.get("http://localhost:8084/hoa-don/getAll")
            .then(function (res) {
                $scope.dsHoaDon = res.data;
            })
            .catch(function (err) {
                console.error("Lỗi load hóa đơn", err);
            });

        // 🔹 mở chi tiết hóa đơn
        $scope.openHoaDonDetail = function (idHD) {

            // load thông tin hóa đơn
            $http.get("http://localhost:8084/ban-hang/hoa-don/detail-info/" + idHD)
                .then(function (res) {
                    $scope.hoaDonDangXem = res.data;
                     console.log("Reload HD:", res.data);
                });

            // load chi tiết hóa đơn
            $http.get("http://localhost:8084/ban-hang/hoa-don/detail/" + idHD)
                .then(function (res) {
                    $scope.dsHDCT = res.data;
                   console.log("Reload HDCT:", res.data);

                    $timeout(function () {
                        if (!hoaDonModal) {
                            hoaDonModal = new bootstrap.Modal(
                                document.getElementById("hoaDonDetailModal")
                            );
                        }
                        hoaDonModal.show();
                    });
                })
                .catch(function (err) {
                    console.error("Lỗi load CTHD", err);
                });
        };

        // 🔹 đóng modal
        $scope.closeHoaDonDetail = function () {
            if (hoaDonModal) hoaDonModal.hide();

            $scope.hoaDonDangXem = null;
            $scope.dsHDCT = [];
        };

    });
