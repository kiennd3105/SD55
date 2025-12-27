app.controller("sanPhamCtrl", function ($scope, $http,$routeParams,$timeout) {
     $scope.dsSanPham = [];
     $scope.sanPhamDetail = {};
     $scope.dsSPCT = [];
    $http.get("http://localhost:8084/san-pham/getAll")
        .then(function (response) {
            $scope.dsSanPham = response.data;
            console.log("sanpham:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load sản phẩm" , error);
        });

    $scope.openDetail = function (id) {
           $http.get("http://localhost:8084/san-pham/detail/" + id)
               .then(function (res) {

                   console.log("DETAIL từ API:", res.data);

                   $scope.sanPhamDetail = res.data;

                   // ⚡ ÉP Angular render xong rồi mới mở modal
                   $timeout(function () {
                       var modal = new bootstrap.Modal(
                           document.getElementById('detailModal')
                       );
                       modal.show();
                   });
               });
       };


$scope.openCTSP = function (idSanPham) {

    $http.get("http://localhost:8084/san-pham/san-pham/" + idSanPham)
        .then(function (res) {

            console.log("DS CTSP:", res.data);
            $scope.dsSPCT = res.data;

            // mở modal CTSP
            $timeout(function () {
                var modal = new bootstrap.Modal(
                    document.getElementById('ctspModal')
                );
                modal.show();
            });
        })
        .catch(function (err) {
            console.error("Lỗi load CTSP", err);
        });
};
});
