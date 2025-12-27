app.controller("theLoaiCtrl", function ($scope, $http) {
      $scope.dsTheLoai = [];

    $http.get("http://localhost:8084/the-loai/getAll")
        .then(function (response) {
            $scope.dsTheLoai = response.data;
            console.log("Thể loại:", response.data);
        })
        .catch(function (error) {
            console.error("Lỗi load thể loại", error);
        });
});
