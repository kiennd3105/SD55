    var app = angular.module("myApp", ['ngRoute']);

    app.config(function ($routeProvider) {
        $routeProvider
             .when('/login', {
                        templateUrl: '/user/view/login.html',
                        controller: 'loginCtrl'
             })
            .when('/sanpham', {
                templateUrl: '/ban_tai_quay/view/sanpham.html',
                controller: 'sanPhamCtrl'
            })
            .when('/hoadon', {
                templateUrl: '/ban_tai_quay/view/hoadon.html',
                controller: 'hoaDonCtrl'
            })
            .when('/theloai', {
                templateUrl: '/ban_tai_quay/view/theloai.html',
                controller: 'theLoaiCtrl'
            })
            .when('/trangchu', {
                templateUrl: '/ban_tai_quay/view/trangchu.html',
                controller: 'trangChuCtrl'
            })
            .when('/nhanvien', {
                templateUrl: '/ban_tai_quay/view/nhanvien.html',
                controller: 'nhanVienCtrl'
            })
            .when('/khachhang', {
                templateUrl: '/ban_tai_quay/view/khachhang.html',
                controller: 'khachHangCtrl'
            })
            .when('/thuonghieu', {
                templateUrl: '/ban_tai_quay/view/thuonghieu.html',
                controller: 'thuongHieuCtrl'
            })
            .when('/mausac', {
                templateUrl: '/ban_tai_quay/view/mausac.html',
                controller: 'mauSacCtrl'
            })
            .when('/chatlieu', {
                templateUrl: '/ban_tai_quay/view/chatlieu.html',
                controller: 'chatLieuCtrl'
            })
            .when('/size', {
                templateUrl: '/ban_tai_quay/view/size.html',
                controller: 'sizeCtrl'
            })
            .when('/voucher', {
                templateUrl: '/ban_tai_quay/view/voucher.html',
                controller: 'voucherCtrl'
            })
            .when('/taiquay', {
                templateUrl: '/ban_tai_quay/view/taiquay.html',
                controller: 'taiQuayCtrl'
            })
            .otherwise({
                redirectTo: '/trangchu'
            });
    });

app.controller('myCtrl', function ($scope,$location) {

    $scope.user = JSON.parse(localStorage.getItem("user")) || {};
    $scope.showProfile = false;

    $scope.toggleProfile = function () {
        $scope.showProfile = !$scope.showProfile;
    };

    $scope.logout = function () {
        localStorage.removeItem("user");
        $scope.showProfile = false;

        window.location.href = "/user/layout-user.html#!/login";
    };
     $scope.showQLSP = false;

        $scope.toggleQLSP = function () {
            $scope.showQLSP = !$scope.showQLSP;
        };

        // ✅ TỰ ĐỘNG MỞ MENU KHI ĐANG Ở TRANG SẢN PHẨM
        $scope.$watch(function () {
            return $location.path();
        }, function (path) {
            if (
                path.startsWith("/sanpham") ||
                path.startsWith("/thuonghieu") ||
                path.startsWith("/mausac") ||
                path.startsWith("/chatlieu") ||
                path.startsWith("/size") ||
                path.startsWith("/theloai")
            ) {
                $scope.showQLSP = true;
            }
        });
});


app.run(function($rootScope, $location) {
    const user = JSON.parse(localStorage.getItem("user"));
    $rootScope.currentUser = user || { ten: "Chưa đăng nhập" };
    console.log("nv",user)

    $rootScope.$on('$routeChangeStart', function(event, next, current) {
        const publicRoutes = ['/login'];

        if (!user && !publicRoutes.includes(location.hash.replace('#!', ''))) {
                   event.preventDefault();
                   window.location.href = "/user/layout-user.html#!/login";
                   return;
               }


        // Chỉ NV mới vào /taiquay
        const nvRoutes = ['/taiquay'];
        if (user && nvRoutes.includes($location.path()) && user.role.toUpperCase() !== 'NHANVIEN') {
            event.preventDefault();
            alert("Bạn không có quyền truy cập!");
            $location.path('/trangchu');
            return;
        }
    });
});


    document.addEventListener('DOMContentLoaded', function () {
        const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a');

        allSideMenu.forEach(item => {
            const li = item.parentElement;

            item.addEventListener('click', function () {
                allSideMenu.forEach(i => {
                    i.parentElement.classList.remove('active');
                });
                li.classList.add('active');
            });
        });

        const menuBar = document.querySelector('#bx_menu');
        const sidebar = document.getElementById('sidebar');

        menuBar.addEventListener('click', function () {
            sidebar.classList.toggle('hide');
        });
    });
