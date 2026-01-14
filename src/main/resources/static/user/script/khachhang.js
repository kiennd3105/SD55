userApp.controller("profileCtrl", function ($scope, $location,$http) {
    const auth = JSON.parse(localStorage.getItem("user"));
        $scope.editing = false;
    $scope.isLogin = !!auth;
    $scope.user = auth ? auth.user : null;
    if (!auth || !auth.user) {
        $location.path("/login");
        return;
    }
     $scope.toggleEdit = function() {
               if ($scope.editing) {
                   if (!$scope.user.ten || !$scope.user.email || !$scope.user.sdt) {
                       alert("Vui lòng nhập đầy đủ thông tin bắt buộc!");
                       return;
                   }
                   $http.put("http://localhost:8084/khach-hang/update/" + $scope.user.id, $scope.user)
                       .then(function(res) {
                           alert("Cập nhật thành công!");
                           $scope.editing = false;
                           localStorage.setItem("user", JSON.stringify($scope.user));
                       })
                       .catch(function(err) {
                           console.error(err);
                           alert("Cập nhật thất bại!");
                       });

               } else {
                   $scope.editing = true;
               }
           };
    $scope.user = auth.user;
    $scope.role = auth.role;
    $scope.diDenLichSu = function() {
            $location.path("/hoa-don");
        };
    $scope.logout = function () {
        localStorage.removeItem("user");
        $location.path("/login");
    };
});
