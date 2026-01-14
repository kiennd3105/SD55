userApp.controller("cartCtrl", function ($scope, $http, $location,$routeParams,$rootScope) {

    const auth = JSON.parse(localStorage.getItem("user"));
    const cartSelected = JSON.parse(localStorage.getItem("cartSelected")) || [];
    if (!auth) {
        $location.path("/login");
        return;
    }
    const idKH = auth.user.id;
    $scope.idKH = idKH;
    $scope.cart = [];
    $scope.tongTien = 0;
    $scope.cartCount = 0;
    $scope.tongTien = $scope.cart.reduce((sum, item) => sum + (item.gia * item.soLuong), 0);
     $scope.khachHang = {
            ten: auth.user.ten,
            email: auth.user.email,
            sdt: auth.user.sdt
        };
    function loadCart() {
        $http.get("http://localhost:8084/gio-hang/" + idKH)
            .then(function (res) {
             let gioHang = res.data;
                    $rootScope.cartCount = gioHang.length;
                $scope.cart = res.data|| [];
                console.log("ct giỏ hàng", res.data);
                $rootScope.cartCount = $scope.cart.length;
                $scope.cart.forEach(item => item.selected = false);
                $scope.selectAll = false;
                tinhTongTien();
                $scope.updateSelected();
            })
            .catch(function (err) {
                console.error("Lỗi load giỏ hàng", err);
                 alert("Không thể tải giỏ hàng!");
            });
    }

     $scope.updateSelected = function() {
         $scope.tongTienChon = $scope.cart
             .filter(item => item.selected)
             .reduce((sum, item) => sum + (item.gia * item.soLuong), 0);
         $scope.selectAll = $scope.cart.every(item => item.selected);
     };

    $scope.xoaChon = function() {
        const chon = $scope.cart.filter(item => item.selected);
        if (chon.length === 0) return alert("Chưa chọn sản phẩm nào!");
        if (!confirm("Xóa các sản phẩm đã chọn?")) return;
        chon.forEach(item => {
            $http.delete("http://localhost:8084/gio-hang/remove/" + item.id)
                .then(() => loadCart());
        });
    };
    $scope.thanhToanChon = function() {
        const chon = $scope.cart.filter(item => item.selected);
        if (chon.length === 0) return alert("Chưa chọn sản phẩm nào để thanh toán!");
        console.log("Thanh toán các sản phẩm:", chon);
        alert("Bạn sẽ thanh toán " + chon.length + " sản phẩm.");
    };
    function tinhTongTien() {
        $scope.tongTien = $scope.cart.reduce(function (sum, item) {
            return sum + (Number(item.gia) * Number(item.soLuong));
        }, 0);
    }

    $scope.tang = function (item) {
        const newSL = Number(item.soLuong) + 1;
        updateSoLuong(item.id, newSL);
    };

    $scope.giam = function (item) {
        if (item.soLuong > 1) {
            const newSL = Number(item.soLuong) - 1;
            updateSoLuong(item.id, newSL);
        }
    };

    function updateSoLuong(idCTGH, soLuong) {
        $http.put("http://localhost:8084/gio-hang/update/" + idCTGH, null, {
            params: { soLuong: soLuong }
        }).then(function () {
            loadCart();
        });
    }

    $scope.xoa = function (item) {
        if (confirm("Xóa sản phẩm này?")) {
            $http.delete("http://localhost:8084/gio-hang/remove/" + item.id)
                .then(function () {
                    loadCart();
                });
        }
    };

  $scope.diDenThanhToan = function() {
      const chon = $scope.cart.filter(item => item.selected);
      if(chon.length === 0) return alert("Chưa chọn sản phẩm nào để thanh toán!");

      localStorage.setItem("cartSelected", JSON.stringify(chon));
      $location.path("/checkout");
  };

        $scope.tongSauGiam = function() {
            const giam = $scope.voucherSelected ? Number($scope.voucherSelected.giaTriGiam) : 0;
            return $scope.tongTien - giam;
        };
    loadCart();
});
