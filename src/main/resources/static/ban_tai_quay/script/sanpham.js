app.controller("sanPhamCtrl", function ($scope, $http) {
      $scope.dsTheLoai = [];

    $http.get("http://localhost:8080/san-pham/the-loai")
        .then(function (response) {
            $scope.dsTheLoai = response.data;
            console.log("Thể loại:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load thể loại", error);
        });
});
