userApp.controller("ctspCtrl", function ($scope, $http, $routeParams, $location,$timeout,$rootScope) {

    const idSP = $routeParams.id;
    const auth = JSON.parse(localStorage.getItem("user"));
    if (!auth) {
        $location.path("/login");
        return;
    }
    $scope.idKH = auth.user.id;
    $scope.dsCTSP = [];
    $scope.selectedCTSP = null;
    $scope.tenSP = "";

    $scope.listSize = [];
    $scope.listMau = [];
    $scope.message = "";
    $scope.messageType = "";
    $scope.selectedSize = null;
    $scope.selectedMau = null;

    $scope.qty = 1;
    $scope.mainImage = null;

    $scope.minPrice = 0;
    $scope.maxPrice = 0;

    $http.get("http://localhost:8084/san-pham/san-pham/" + idSP)
        .then(function (res) {
            $scope.dsCTSP = res.data;
            console.log("CTSP:", res.data);

            if ($scope.dsCTSP.length === 0) {
                alert("Sản phẩm chưa có biến thể");
                return;
            }
            $scope.tenSP = $scope.dsCTSP[0].tenSP;

            let prices = $scope.dsCTSP.map(p => Number(p.gia));
            $scope.minPrice = Math.min(...prices);
            $scope.maxPrice = Math.max(...prices);

            buildOptions();

            if ($scope.dsCTSP.length === 1) {
                $scope.selectedCTSP = $scope.dsCTSP[0];
                $scope.selectedSize = $scope.selectedCTSP.tenSize;
                $scope.selectedMau = $scope.selectedCTSP.tenMau;
                $scope.mainImage = $scope.selectedCTSP.img;
            } else {
                $scope.mainImage = $scope.dsCTSP[0].img;
            }
        })
        .catch(function () {
            alert("Không tìm thấy sản phẩm");
            $location.path("/");
        });

    function buildOptions() {
        let sizeMap = {};
        let mauMap = {};

        $scope.dsCTSP.forEach(p => {
            sizeMap[p.tenSize] = true;
            mauMap[p.tenMau] = true;
        });

        $scope.listSize = Object.keys(sizeMap);
        $scope.listMau = Object.keys(mauMap);
    }
    $scope.chonSize = function (size) {
        $scope.selectedSize = size;
        timCTSP();
    };
    $scope.chonMau = function (mau) {
        $scope.selectedMau = mau;
        timCTSP();
    };
    function timCTSP() {
        if ($scope.selectedSize && $scope.selectedMau) {
            $scope.selectedCTSP = $scope.dsCTSP.find(p =>
                p.tenSize === $scope.selectedSize &&
                p.tenMau === $scope.selectedMau
            );

            if ($scope.selectedCTSP) {
                $scope.mainImage = $scope.selectedCTSP.img;
            }
        }
    }
    $scope.changeImage = function (img) {
        $scope.mainImage = img;
    };
     $scope.addToCart = function(idKH, idCTSP, soLuong) {
            if (!$scope.selectedCTSP) {
                 $scope.message = "Vui lòng chọn Size và Màu ";
                        $scope.messageType = "error";
                         $timeout(function() {
                                $scope.message = "";
                            }, 3000);

                return;
            }
            $http({
                method: "POST",
                url: "http://localhost:8084/gio-hang/add",
                params: {
                    idKH: idKH,
                    idCTSP: idCTSP,
                    soLuong: soLuong
                }
            }).then(function(response) {
                $rootScope.cartCount = $scope.cart.length;
                console.log("Thêm sản phẩm vào giỏ hàng thành công!");
            }, function(error) {
                console.error("Lỗi khi thêm sản phẩm vào giỏ hàng:", error);
                alert("Thêm giỏ hàng thất bại!");
            });
        };

    $scope.buyNow = function () {
        $scope.addToCart();
        $location.path("/gio-hang");
    };
});
