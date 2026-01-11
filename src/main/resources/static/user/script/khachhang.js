userApp.controller("profileCtrl", function ($scope, $location) {
    const auth = JSON.parse(localStorage.getItem("user"));
    $scope.isLogin = !!auth;
    $scope.user = auth ? auth.user : null;
    if (!auth || !auth.user) {
        $location.path("/login");
        return;
    }

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
