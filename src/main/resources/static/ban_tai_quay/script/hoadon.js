        app.controller("hoaDonCtrl", function ($scope, $http, $timeout) {
                $scope.userDangNhap = JSON.parse(localStorage.getItem("user"));

                if (!$scope.userDangNhap) {
                    alert("Bạn chưa đăng nhập");
                    window.location.href = "/user/layout-user.html#!/login";
                    return;
                }

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
               $scope.getStepIndex = function(status) {
                   switch(status) {
                       case 0: return 0;
                       case 2: return 1;
                       case 3: return 2;
                       case 4: return 3;
                       case 1: return 4;
                       case 5: return 0;
                       default: return 0;
                   }
               };

               $scope.getProgressWidth = function() {
                   const stepIndex = $scope.getStepIndex($scope.hoaDonDangXem.trangThai);
                   const totalSteps = 4;
                   return (stepIndex / totalSteps) * 100;
               };
               $scope.coTheChuyenTrangThai = function (trangThaiHienTai, trangThaiMoi) {
                   let dsChoPhep = $scope.trangThaiTiepTheoMap[trangThaiHienTai] || [];
                   return dsChoPhep.includes(trangThaiMoi);
               };

                $scope.trangThaiTiepTheoMap = {
                    0: [2, 5],
                    2: [3, 5],
                    3: [4],
                    4: [1],
                    1: [],
                    5: []
                };

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
            $scope.doiTrangThaiHoaDon = function (hd, trangThaiMoi) {
                if (!hd || !hd.id) return;
                const user = JSON.parse(localStorage.getItem("user"));
                if (!user || !user.user || !user.user.id) {
                    alert("Chưa đăng nhập nhân viên");
                    return;
                }
                const nhanVien = user.user;
                let tenTrangThai = $scope.trangThaiMap[trangThaiMoi];
                if (!confirm("Xác nhận chuyển hóa đơn sang trạng thái: " + tenTrangThai + " ?")) {
                    return;
                }
                $http.put("http://localhost:8084/hoa-don/doi-trang-thai", null, {
                    params: {
                        idHoaDon: hd.id,
                        trangThai: trangThaiMoi,
                        idNhanVien: nhanVien.id
                    }
                }).then(function () {

                    hd.trangThai = trangThaiMoi;

                    if ($scope.hoaDonDangXem?.id === hd.id) {
                        $scope.hoaDonDangXem.trangThai = trangThaiMoi;
                        $scope.hoaDonDangXem.tenNV = nhanVien.ten;
                    }

                    alert("Cập nhật trạng thái thành công");

                }).catch(function (err) {
                    console.error(err);
                    alert("Lỗi cập nhật trạng thái");
                });
            };




        });
