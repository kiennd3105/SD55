angular.module("userApp")

    /* ===== LOGIN ===== */
    .controller("loginCtrl", function ($scope, $http) {

        if (localStorage.getItem("registerSuccess")) {
            $scope.success = "Đăng ký thành công. Vui lòng đăng nhập.";
            localStorage.removeItem("registerSuccess");
        }

        $scope.login = {
            email: "",
            password: ""
        };

        $scope.error = "";

        $scope.dangNhap = function () {
            $http.post("http://localhost:8084/api/auth/login", $scope.login)
                .then(function (res) {
                    localStorage.setItem("user", JSON.stringify(res.data));
                    window.location.href = "/user/layout-user.html#!/";
                })
                .catch(function (err) {
                    $scope.error = err.data || "Sai email hoặc mật khẩu";
                });
        };

    })   // 👈 👈 BẮT BUỘC PHẢI CÓ

    /* ===== REGISTER ===== */
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
