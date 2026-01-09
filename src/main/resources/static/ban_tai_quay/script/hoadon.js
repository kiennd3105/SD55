        app.controller("hoaDonCtrl", function ($scope, $http, $timeout) {

            $scope.dsHoaDon = [];
            $scope.hoaDonDangXem = {};
            $scope.dsHDCT = [];
            let hoaDonModal = null;
            $scope.trangThaiMap = {
                1: "Hoàn thành",
                2: "Đang chờ xác nhận",
                3: "Đã xác nhận",
                4: "Đang giao",
                5: "Đã hủy",
                0: "Chờ thanh toán"
            };
                $scope.trangThaiTabs = [
                    { value: '', label: 'Tất cả' },
                    { value: 0, label: 'Chờ thanh toán' },
                    { value: 2, label: 'Đang chờ xác nhận' },
                    { value: 3, label: 'Đã xác nhận' },
                    { value: 4, label: 'Đang giao' },
                    { value: 1, label: 'Hoàn thành' },
                    { value: 5, label: 'Đã hủy' }
                ];

                $scope.trangThaiDangChon = '';


                $scope.filter = {
                    keyword: '',
                    loaiHoaDon: ''
                };
                $scope.chonTrangThai = function (value) {
                     $scope.trangThaiDangChon = value === '' ? '' : Number(value);
                 };

             $scope.filterHoaDon = function (hd) {

                    if ($scope.trangThaiDangChon !== '' &&
                            Number(hd.trangThai) !== $scope.trangThaiDangChon) {
                            return false;
                        }

                 if ($scope.filter.loaiHoaDon !== '' &&
                     hd.loaiHoaDon !== Number($scope.filter.loaiHoaDon)) {
                     return false;
                 }

                 if (!$scope.filter.keyword) return true;

                 let keyword = $scope.filter.keyword.toLowerCase();

                 return (
                     (hd.ma && hd.ma.toLowerCase().includes(keyword)) ||
                     (hd.tenNV && hd.tenNV.toLowerCase().includes(keyword)) ||
                     (hd.tenKH && hd.tenKH.toLowerCase().includes(keyword))
                 );
             };


                $scope.resetFilter = function () {
                    $scope.filter.keyword = '';
                    $scope.filter.loaiHoaDon = '';
                };


            $http.get("http://localhost:8084/hoa-don/getAll")
                .then(function (res) {
                    $scope.dsHoaDon = res.data;
                })
                .catch(function (err) {
                    console.error("Lỗi load hóa đơn", err);
                });

            $scope.openHoaDonDetail = function (idHD) {

                $http.get("http://localhost:8084/ban-hang/hoa-don/detail-info/" + idHD)
                    .then(function (res) {
                        $scope.hoaDonDangXem = res.data;
                         console.log("Reload HD:", res.data);
                    });

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

            $scope.closeHoaDonDetail = function () {
                if (hoaDonModal) hoaDonModal.hide();

                $scope.hoaDonDangXem = null;
                $scope.dsHDCT = [];
            };

        });
