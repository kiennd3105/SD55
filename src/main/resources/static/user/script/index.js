var userApp = angular.module("userApp", ['ngRoute']);

// Route configuration
userApp.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '/user/view/index.html',
            controller: 'homePageCtrl'
        })
        .when('/san-pham', {
            templateUrl: '/user/view/sanpham.html',
            controller: 'sanPhamCtrl'
        })
        .otherwise({
            redirectTo: '/'
        });
});

// Header Controller
userApp.controller('headerCtrl', function ($scope, $http) {
    // Header logic if needed
});

// Home Page Controller
userApp.controller('homePageCtrl', function ($scope, $http) {
    $scope.bestSellingProducts = [];
    $scope.newProducts = [];
    $scope.loadingBestSelling = true;
    $scope.loadingNewProducts = true;

    // Load best selling products
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

    // Load new products
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

    // View product details
    $scope.viewProduct = function (productId) {
        // Navigate to product detail page
        window.location.href = "#!/san-pham/" + productId;
    };

    // Add to cart
    $scope.addToCart = function (product) {
        // Get cart from localStorage or initialize
        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
        
        // Check if product already in cart
        var existingItem = cart.find(function(item) {
            return item.id === product.id;
        });

        if (existingItem) {
            existingItem.quantity = (existingItem.quantity || 1) + 1;
        } else {
            cart.push({
                id: product.id,
                ma: product.ma,
                tenSanPham: product.tenSanPham,
                gia: product.gia,
                hinhAnh: product.hinhAnh,
                quantity: 1
            });
        }

        // Save to localStorage
        localStorage.setItem('cart', JSON.stringify(cart));
        
        // Show notification
        alert('Đã thêm sản phẩm vào giỏ hàng!');
        
        // Update cart badge (if exists)
        updateCartBadge();
    };

    // Update cart badge
    function updateCartBadge() {
        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
        var totalItems = cart.reduce(function(sum, item) {
            return sum + (item.quantity || 1);
        }, 0);
        
        var badge = document.querySelector('.cart-icon .badge');
        if (badge) {
            badge.textContent = totalItems;
        }
    }

    // Initialize
    $scope.loadBestSellingProducts();
    $scope.loadNewProducts();
    updateCartBadge();
});

// Product list controller (for future use)
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

// Format number filter
userApp.filter('number', function() {
    return function(input) {
        if (!input) return '0';
        return input.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    };
});

