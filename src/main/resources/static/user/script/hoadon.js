userApp.controller("hoaDonKHCtrl", function ($scope, $location, $http, $timeout) {
    const auth = JSON.parse(localStorage.getItem("user"));
    if (!auth || !auth.user) {
        $location.path("/login");
        return;
    }
    const idKH = auth.user.id;

    $scope.dsHoaDon = [];
    $scope.hoaDonDangXem = {};
    $scope.dsHDCT = [];
    let hoaDonModal = null;

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
    $scope.trangThaiDangChon = '';
    $scope.filter = {
        keyword: '',
        loaiHoaDon: ''
    };

    $scope.chonTrangThai = function(value) {
        $scope.trangThaiDangChon = value === '' ? '' : Number(value);
    };

    $scope.filterHoaDon = function(hd) {
        if ($scope.trangThaiDangChon !== '' && Number(hd.trangThai) !== $scope.trangThaiDangChon) {
            return false;
        }
        if ($scope.filter.loaiHoaDon !== '' && hd.loaiHoaDon !== Number($scope.filter.loaiHoaDon)) {
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

    $scope.resetFilter = function() {
        $scope.filter.keyword = '';
        $scope.filter.loaiHoaDon = '';
        $scope.trangThaiDangChon = '';
    };

    $scope.diDenLichSu = function() {
        $location.path("/hoa-don");
    };

    $scope.loadHoaDon = function() {
        $http.get(`http://localhost:8084/hoa-don/getByKhachHangOnline/${idKH}`)
            .then(function(res) {
                let hoaDonOnline = res.data.filter(hd => hd.loaiHoaDon === 1);
                hoaDonOnline.sort((a, b) => new Date(b.ngayDatHang) - new Date(a.ngayDatHang));
                $scope.dsHoaDon = hoaDonOnline;
            })
            .catch(function(err) {
                console.error("Lỗi load hóa đơn", err);
            });
    };

    $scope.loadHoaDon();

    $scope.openHoaDonDetail = function(idHD) {
        $http.get("http://localhost:8084/ban-hang/hoa-don/detail-info/" + idHD)
            .then(function(res) {
                $scope.hoaDonDangXem = res.data;
            });

        $http.get("http://localhost:8084/ban-hang/hoa-don/detail/" + idHD)
            .then(function(res) {
                $scope.dsHDCT = res.data;

                $timeout(function() {
                    if (!hoaDonModal) {
                        hoaDonModal = new bootstrap.Modal(
                            document.getElementById("hoaDonDetailModal")
                        );
                    }
                    hoaDonModal.show();
                });
            })
            .catch(function(err) {
                console.error("Lỗi load CTHD", err);
            });
    };

    $scope.closeHoaDonDetail = function() {
        if (hoaDonModal) hoaDonModal.hide();
        $scope.hoaDonDangXem = {};
        $scope.dsHDCT = [];
    };

         $scope.huyHoaDon = function (hd) {

             if (hd.trangThai !== 2) {
                 alert("Chỉ được hủy hóa đơn khi đang chờ xác nhận!");
                 return;
             }

             if (!confirm("Bạn có chắc chắn muốn hủy hóa đơn này không?")) {
                 return;
             }

             $http.put("http://localhost:8084/hoa-don/huy-trang-thai", null, {
                 params: {
                     idHoaDon: hd.id,
                     trangThai: 5
                 }
             }).then(function () {

                 hd.trangThai = 5;

                 if ($scope.hoaDonDangXem && $scope.hoaDonDangXem.id === hd.id) {
                     $scope.hoaDonDangXem.trangThai = 5;
                 }

                 alert("Hủy hóa đơn thành công!");

             }).catch(function (err) {
                 console.error(err);
                 alert("Hủy hóa đơn thất bại!");
             });
         };

});
