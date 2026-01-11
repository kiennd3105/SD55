userApp.controller("checkoutCtrl", function($scope, $http, $location) {
    const auth = JSON.parse(localStorage.getItem("user"));
    if (!auth) {
        $location.path("/login");
        return;
    }

    $scope.khachHang = {
        ten: auth.user.ten,
        email: auth.user.email,
        sdt: auth.user.sdt
    };
      $scope.diaChi = {}; // chứa thông tin địa chỉ
        $scope.listTinh = [];
        $scope.listHuyen = [];
        $scope.listXa = [];
const mienBac = [
    "Hà Nội",
    "Hải Phòng",
    "Quảng Ninh",
    "Thái Bình",
    "Nam Định",
    "Ninh Bình",
    "Hà Nam",
    "Hưng Yên",
    "Hải Dương",
    "Vĩnh Phúc",
    "Bắc Ninh",
    "Phú Thọ",
    "Thái Nguyên",
    "Bắc Giang",
    "Lào Cai",
    "Yên Bái",
    "Tuyên Quang",
    "Hà Giang",
    "Điện Biên",
    "Lai Châu",
    "Sơn La",
    "Bắc Kạn",
    "Cao Bằng",
    "Hòa Bình",
    "Lạng Sơn"
];
const mienTrung = [
    "Thanh Hóa",
    "Nghệ An",
    "Hà Tĩnh",
    "Quảng Bình",
    "Quảng Trị",
    "Thừa Thiên Huế",
    "Đà Nẵng",
    "Quảng Nam",
    "Quảng Ngãi",
    "Bình Định",
    "Phú Yên",
    "Khánh Hòa",
    "Ninh Thuận",
    "Bình Thuận"
];
const mienNam = [
    "TP Hồ Chí Minh",
    "Bình Dương",
    "Đồng Nai",
    "Bà Rịa – Vũng Tàu",
    "Long An",
    "Tiền Giang",
    "Bến Tre",
    "Trà Vinh",
    "Vĩnh Long",
    "Cần Thơ",
    "Hậu Giang",
    "Sóc Trăng",
    "Bạc Liêu",
    "Cà Mau",
    "An Giang",
    "Kiên Giang",
    "Đồng Tháp",
    "Tây Ninh",
    "Bình Phước",
    "Bình Dương",
    "Bình Thuận"
];

$scope.tinhPhiVanChuyen = function() {
    if (!$scope.diaChi.tinh) return 0;

    const tinh = $scope.diaChi.tinh.name;

    if (mienBac.includes(tinh)) return 30000;
    if (mienTrung.includes(tinh)) return 50000;
    if (mienNam.includes(tinh)) return 70000;

    return 60000; // mặc định
};

$scope.$watch('diaChi.tinh', function(newVal) {
    $scope.phiVanChuyen = $scope.tinhPhiVanChuyen();
});

    const selectedCart = JSON.parse(localStorage.getItem("cartSelected")) || [];
    $scope.cart = selectedCart;

    $scope.tongTien = $scope.cart.reduce((sum, item) => sum + (item.gia * item.soLuong), 0);

    $scope.voucherSelected = null;
     $scope.chonVoucher = function(voucher) {
            $scope.voucherSelected = voucher;
        }

    $http.get("http://localhost:8084/api/provinces/p").then(res => {
           $scope.listTinh = res.data; // mỗi phần tử: { code, name, division_type, ... }
       });

       $scope.loadHuyen = function(tinh) {
           if (!tinh) return;
           $http.get(`http://localhost:8084/api/provinces/p/${tinh.code}?depth=2`).then(res => {
               $scope.listHuyen = res.data.districts; // districts = quận/huyện
               $scope.listXa = [];
               $scope.diaChi.huyen = null;
               $scope.diaChi.xa = null;
           });
       };

       $scope.loadXa = function(huyen) {
           if (!huyen) return;
           $http.get(`http://localhost:8084/api/provinces/d/${huyen.code}?depth=2`).then(res => {
               $scope.listXa = res.data.wards; // wards = xã/phường
               $scope.diaChi.xa = null;
           });
       };

          $scope.getDiaChiDayDu = function() {
                 let chiTiet = $scope.diaChi.chiTiet || "";
                 let xa = $scope.diaChi.xa ? $scope.diaChi.xa.name : "";
                 let huyen = $scope.diaChi.huyen ? $scope.diaChi.huyen.name : "";
                 let tinh = $scope.diaChi.tinh ? $scope.diaChi.tinh.name : "";
                 return `${chiTiet}, ${xa}, ${huyen}, ${tinh}`;
             };
    function loadVouchers() {
        $http.get("http://localhost:8084/gio-hang/voucher/active")
            .then(function(res) {
                const total = $scope.tongTien;
                $scope.vouchers = res.data.filter(v => {
                    const dieuKien = v.dieuKienApDung ? Number(v.dieuKienApDung) : 0;
                    return total >= dieuKien;
                });
                console.log("Voucher khả dụng:", $scope.vouchers);
            })
            .catch(function(err) {
                console.error("Lỗi load voucher:", err);
            });
    }

    $scope.tienDuocGiam = function() {
        let tong = $scope.tongTien || 0;
        let giam = 0;

        if ($scope.voucherSelected) {
            if ($scope.voucherSelected.loai === 1) {
                giam = tong * (Number($scope.voucherSelected.giaTriGiam || 0) / 100);
            } else if ($scope.voucherSelected.loai === 2) {
                giam = Number($scope.voucherSelected.giaTriGiam || 0);
            }
            if ($scope.voucherSelected.giamMax) {
                giam = Math.min(giam, Number($scope.voucherSelected.giamMax));
            }
        }

        return giam;
    };

    $scope.tongSauGiam = function() {
        const tong = $scope.tongTien || 0;
        const giam = $scope.tienDuocGiam();
        const phi = $scope.phiVanChuyen || 0;
        return tong - giam + phi;
    };
    $scope.phuongThucThanhToan = "COD";

    $scope.moModalThanhToan = function () {
        const modal = new bootstrap.Modal(
            document.getElementById('paymentModal')
        );
        modal.show();
    };

    $scope.xacNhanThanhToan = function () {
        const modalEl = document.getElementById('paymentModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();
        if ($scope.phuongThucThanhToan === "COD") {
            $scope.thanhToanCOD();
        } else if ($scope.phuongThucThanhToan === "VNPAY") {
            $scope.thanhToanVNPay();
        }
    };


   $scope.thanhToanCOD = function () {
       if ($scope.cart.length === 0) {
           alert("Chưa có sản phẩm nào để thanh toán!");
           return;
       }
       if (!$scope.diaChi.tinh || !$scope.diaChi.huyen || !$scope.diaChi.xa) {
           alert("Vui lòng chọn đầy đủ địa chỉ giao hàng!");
           return;
       }
       const payload = {
           idKH: auth.user.id,
           diaChi: $scope.getDiaChiDayDu(),
           phiVanChuyen: $scope.phiVanChuyen || 0,
           tongTien: $scope.tongTien,
           giamGia: Math.floor($scope.tienDuocGiam()),
           thanhTien: Math.floor($scope.tongSauGiam()),
           idVoucher: $scope.voucherSelected ? $scope.voucherSelected.idVoucher : null,
           items: $scope.cart.map(item => ({
               idCTSP: item.idCTSP,
               giaBan: String(item.gia),
               soLuong: String(item.soLuong)
           }))
       };
       console.log("Payload thanh toán:", payload);
       $http.post("http://localhost:8084/gio-hang/online", payload)
           .then(function (res) {
               alert("Thanh toán thành công!");
               localStorage.removeItem("cartSelected");
              console.log("ITEMS GỬI LÊN:", payload.items);
               $location.path("/thank-you");
           })
           .catch(function (err) {
               console.error(err);
               alert(err.data || "Thanh toán thất bại!");
           });
   };


    loadVouchers();
});
