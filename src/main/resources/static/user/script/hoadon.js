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
           case 0: return 0; // Chờ thanh toán
           case 2: return 1; // Chờ xác nhận
           case 3: return 2; // Đã xác nhận
           case 4: return 3; // Đang giao
           case 1: return 4; // Hoàn thành
           case 5: return 0; // Hủy
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

    // Chuyển trang lịch sử mua hàng
    $scope.diDenLichSu = function() {
        $location.path("/hoa-don");
    };

    // Load hóa đơn online của khách hàng
    $scope.loadHoaDon = function() {
        $http.get(`http://localhost:8084/hoa-don/getByKhachHangOnline/${idKH}`)
            .then(function(res) {
                // Lọc chỉ lấy online
                let hoaDonOnline = res.data.filter(hd => hd.loaiHoaDon === 1);
                // Sắp xếp theo ngày đặt giảm dần
                hoaDonOnline.sort((a, b) => new Date(b.ngayDatHang) - new Date(a.ngayDatHang));
                $scope.dsHoaDon = hoaDonOnline;
            })
            .catch(function(err) {
                console.error("Lỗi load hóa đơn", err);
            });
    };

    $scope.loadHoaDon();

    // Mở modal xem chi tiết hóa đơn
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
});
