var userApp = angular.module("userApp", ['ngRoute']);

userApp.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/user/view/index.html',
            controller: 'homePageCtrl',
        })
        .when('/login', {
            templateUrl: '/user/view/login.html',
            controller: 'loginCtrl'
        })
        .when('/register', {
            templateUrl: '/user/view/register.html',
            controller: 'registerCtrl'
        })
        .when('/san-pham', {
            templateUrl: '/user/view/sponline.html',
            controller: 'homePageCtrl'
        })
        .when('/san-pham/:id', {
            templateUrl: '/user/view/ctsponlie.html',
            controller: 'ctspCtrl'
        })
        .when('/thong-tin-ca-nhan', {
            templateUrl: '/user/view/khachhang.html',
            controller: 'profileCtrl'
        })
        .when('/gio-hang/:idKH', {
            templateUrl: '/user/view/giohang.html',
            controller: 'cartCtrl'
        })
        .when('/checkout', {
             templateUrl: '/user/view/thanhtoan.html',
             controller: 'checkoutCtrl'
        })
        .when('/hoa-don', {
             templateUrl: '/user/view/hoadonkh.html',
             controller: 'hoaDonKHCtrl'
        })

        .otherwise({
            redirectTo: '/'
        });
});

userApp.run(function ($rootScope, $location) {
    $rootScope.$on('$routeChangeStart', function (event) {

        const authPages = ['/login', '/register'];
        const publicRoutes = ['/', '/san-pham'];

        const path = $location.path();

        // ẩn header/footer cho login, register
        $rootScope.isAuthPage = authPages.includes(path);

        const user = JSON.parse(localStorage.getItem("user"));

        const isPublic =
            authPages.includes(path) ||
            publicRoutes.includes(path) ||
            path.startsWith('/san-pham/');

        if (!user && !isPublic) {
            event.preventDefault();
            $location.path("/login");
        }
               const nvRoutes = ['/taiquay', '/sanpham', '/hoadon','trangchu','nhanvien']; // Các route chỉ NV được vào
                if (nvRoutes.includes(path) && user?.role !== 'NHANVIEN') {
                    event.preventDefault();
                    alert("Bạn không có quyền truy cập trang này!");
                    $location.path("/");
                }
    });
});

userApp.controller("headerCtrl", function ($scope, $location) {
    $scope.user = JSON.parse(localStorage.getItem("user"));
    $scope.isLogin = !!$scope.user;
     $scope.idKH = $scope.user?.user?.id;
    $scope.logout = function () {
        localStorage.removeItem("user");
        $location.path("/login");
    };
});
userApp.controller('homePageCtrl', function ($scope, $http, $location) {
    $scope.bestSellingProducts = [];
    $scope.newProducts = [];
    $scope.loadingBestSelling = true;
    $scope.loadingNewProducts = true;
    $scope.loadBestSellingProducts = function () {
        $scope.loadingBestSelling = true;
        $http.get("http://localhost:8084/user/san-pham-ban-chay?limit=8")
            .then(function (response) {
                $scope.bestSellingProducts = response.data;
                $scope.loadingBestSelling = false;
                console.log("Sản phẩm bán chạy:", response.data);
            })
            .catch(function (error) {
                console.error("Lỗi load sản phẩm bán chạy:", error);
                $scope.loadingBestSelling = false;
            });
    };
    $scope.viewProduct = function (idSanPham) {
        $location.path("/san-pham/" + idSanPham);
    };

    $scope.loadNewProducts = function () {
        $scope.loadingNewProducts = true;
        $http.get("http://localhost:8084/user/san-pham-moi?limit=8")
            .then(function (response) {
                $scope.newProducts = response.data;
                $scope.loadingNewProducts = false;
                console.log("Sản phẩm mới:", response.data);
            })
            .catch(function (error) {
                console.error("Lỗi load sản phẩm mới:", error);
                $scope.loadingNewProducts = false;
            });
    };


    $scope.loadBestSellingProducts();
    $scope.loadNewProducts();

});
userApp.controller('sanPhamCtrl', function ($scope, $http) {
    $scope.products = [];
    $scope.loading = true;
    $http.get("http://localhost:8084/san-pham/getAll")
        .then(function (response) {
            $scope.products = response.data;
            $scope.loading = false;
        })
        .catch(function (error) {
            console.error("Lỗi load sản phẩm:", error);
            $scope.loading = false;
        });
});

userApp.filter('number', function() {
    return function(input) {
        if (!input) return '0';
        return input.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    };
});

