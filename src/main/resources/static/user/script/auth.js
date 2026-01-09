var app = angular.module("userApp");

/* ===== LOGIN ===== */
app.controller("loginCtrl", function ($scope, $http, $window) {

    if (localStorage.getItem("registerSuccess")) {
        $scope.success = "Đăng ký thành công. Vui lòng đăng nhập.";
        localStorage.removeItem("registerSuccess");
    }

    // ✅ PHẢI là username
    $scope.login = {
        username: "",
        password: ""
    };

    $scope.error = "";

    $scope.dangNhap = function () {

        $http.post("http://localhost:8084/api/auth/login", $scope.login)
            .then(function (res) {

                // lưu user + role
                localStorage.setItem("user", JSON.stringify(res.data));

                // 🔥 PHÂN QUYỀN
                if (res.data.role === "ADMIN") {
                    $window.location.href = "/ban_tai_quay/layout.html#!/sanpham";
                } else {
                    $window.location.href = "/user/layout-user.html#!/";
                }

            })
            .catch(function (err) {
                $scope.error = err.data || "Sai tên đăng nhập hoặc mật khẩu";
            });
    };
})

    .controller("registerCtrl", function ($scope, $http, $location) {

        $scope.user = {
            ten: "",
            email: "",
            passw: "",
            sdt: "",
            diaChi: "",
            gioiTinh: ""
        };

        $scope.error = "";
        $scope.submitted = false;

        $scope.dangKy = function (form) {
            $scope.submitted = true;

            if (form.$invalid) return;

            $http.post("http://localhost:8084/api/auth/register", $scope.user)
                .then(function () {
                    localStorage.setItem("registerSuccess", "true");
                    window.location.href = "/user/layout-user.html#!/login";
                })
                .catch(function (err) {
                    $scope.error = err.data || "Đăng ký thành công";
                });
        };

    });
